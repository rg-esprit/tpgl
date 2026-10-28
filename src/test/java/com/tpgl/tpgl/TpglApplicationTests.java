package com.tpgl.tpgl;

import com.tpgl.tpgl.aspect.PerformanceAspect;
import com.tpgl.tpgl.domain.*;
import com.tpgl.tpgl.repository.*;
import com.tpgl.tpgl.scheduler.ContratEcheanceScheduler;
import com.tpgl.tpgl.service.*;
import com.tpgl.tpgl.service.pattern.AuditObserver;
import com.tpgl.tpgl.service.pattern.AutoLocConfigManager;
import com.tpgl.tpgl.service.pattern.NotificationObserver;
import com.tpgl.tpgl.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TpglApplicationTests {

	@Autowired
	private IAgenceService agenceService;

	@Autowired
	private IVehiculeService vehiculeService;

	@Autowired
	private IClientService clientService;

	@Autowired
	private IReservationService reservationService;

	@Autowired
	private IContratService contratService;

	@Autowired
	private ITarificationService tarificationService;

	@Autowired
	private ContratEcheanceScheduler contratEcheanceScheduler;

	@Autowired
	private PerformanceAspect performanceAspect;

	@Autowired
	private NotificationObserver notificationObserver;

	@Autowired
	private AuditObserver auditObserver;

	@Autowired
	private IPaiementRepository paiementRepository;

	@Autowired
	private IVehiculeRepository vehiculeRepository;

	@Autowired
	private IReservationRepository reservationRepository;

	@BeforeEach
	void setUp() {
		notificationObserver.clear();
		auditObserver.clear();
	}

	@Test
	@DisplayName("1. Cycle complet AutoLoc : Agence, Véhicule, Équipement, Client, Réservation, Contrat, Paiement & Cascade")
	void testFullAutoLocLifecycleAndCascade() {
		// 1. Créer une agence
		AgenceDTO agence = agenceService.creerAgence(AgenceDTO.builder()
				.nom("Agence Test Tunis")
				.ville("Tunis")
				.adresse("Centre Urbain Nord")
				.telephone("71000111")
				.build());
		assertThat(agence.getId()).isNotNull();

		// 2. Créer un employé et l'affecter à l'agence
		EmployeDTO employe = agenceService.creerEmploye(EmployeDTO.builder()
				.nom("Khelifi")
				.prenom("Rami")
				.role(RoleEmploye.AGENT)
				.build());
		EmployeDTO affectedEmploye = agenceService.affecterEmployeAAgence(employe.getId(), agence.getId());
		assertThat(affectedEmploye.getAgenceId()).isEqualTo(agence.getId());

		// 3. Créer un équipement et un véhicule, puis les affecter (Séance 8)
		EquipementDTO gps = agenceService.creerEquipement(EquipementDTO.builder()
				.libelle("GPS Test")
				.tarifJournalier(new BigDecimal("10.00"))
				.build());

		VehiculeDTO vehicule = vehiculeService.creerVehicule(VehiculeDTO.builder()
				.immatriculation("999-TN-9999")
				.marque("Toyota")
				.modele("Yaris")
				.categorie(CategorieVehicule.CITADINE)
				.tarifJournalier(new BigDecimal("90.00"))
				.statut(StatutVehicule.DISPONIBLE)
				.build());

		vehicule = vehiculeService.affecterVehiculeAAgence(vehicule.getId(), agence.getId());
		vehicule = vehiculeService.affecterEquipementAVehicule(vehicule.getId(), gps.getId());
		assertThat(vehicule.getAgenceId()).isEqualTo(agence.getId());
		assertThat(vehicule.getEquipementIds()).contains(gps.getId());

		// 4. Inscrire un client
		ClientDTO client = clientService.inscrireClient(ClientDTO.builder()
				.nom("Saidi")
				.prenom("Nour")
				.email("nour.saidi@test.tn")
				.telephone("50123456")
				.numPermis("PERM-001")
				.build());
		assertThat(client.getId()).isNotNull();

		// 5. Créer et valider une réservation -> génère un Contrat et notifie les Observers (GoF)
		ReservationResponseDTO res = reservationService.creerReservation(ReservationRequestDTO.builder()
				.clientId(client.getId())
				.vehiculeId(vehicule.getId())
				.dateDebut(LocalDate.now().plusDays(1))
				.dateFin(LocalDate.now().plusDays(3))
				.build());
		assertThat(res.getStatut()).isEqualTo(StatutReservation.EN_ATTENTE);

		ReservationResponseDTO validatedRes = reservationService.validerReservation(res.getId());
		assertThat(validatedRes.getStatut()).isEqualTo(StatutReservation.CONFIRMEE);
		assertThat(validatedRes.getContratId()).isNotNull();

		// Vérifier que les Observers GoF ont été notifiés et l'aspect AOP a mesuré les appels
		assertThat(notificationObserver.getNotifications()).isNotEmpty();
		assertThat(auditObserver.getAuditLogs()).isNotEmpty();
		assertThat(performanceAspect.getTotalCalls()).isGreaterThan(0);

		// 6. Enregistrer un paiement sur le contrat
		Long contratId = validatedRes.getContratId();
		PaiementDTO paiement = contratService.enregistrerPaiement(contratId, PaiementDTO.builder()
				.montant(new BigDecimal("150.00"))
				.modePaiement(ModePaiement.CARTE)
				.build());
		assertThat(paiement.getId()).isNotNull();

		// 7. Vérifier les statistiques de l'agence
		StatistiqueAgenceDTO stats = agenceService.getStatistiquesAgence(agence.getId());
		assertThat(stats.getTotalVehicules()).isEqualTo(1);
		assertThat(stats.getVehiculesLoues()).isEqualTo(1);
		assertThat(stats.getChiffreAffairesTotal()).isGreaterThan(BigDecimal.ZERO);

		// 8. Vérifier la suppression en cascade Contrat -> Paiement (CascadeType.ALL)
		Long paiementId = paiement.getId();
		contratService.supprimerContrat(contratId);
		assertThat(paiementRepository.findById(paiementId)).isEmpty();
	}

	@Test
	@DisplayName("2. Patrons GoF : Singleton, Strategy et Factory pour la tarification")
	void testGoFPatternsAndTarification() {
		AutoLocConfigManager config1 = AutoLocConfigManager.getInstance();
		AutoLocConfigManager config2 = AutoLocConfigManager.getInstance();
		assertThat(config1).isSameAs(config2);
		assertThat(config1.getNomApplication()).isEqualTo("AutoLoc");

		Vehicule v = Vehicule.builder()
				.immatriculation("100-TN-100")
				.marque("Dacia")
				.modele("Duster")
				.categorie(CategorieVehicule.SUV)
				.tarifJournalier(new BigDecimal("100.00"))
				.statut(StatutVehicule.DISPONIBLE)
				.build();

		// Location longue durée (10 jours : du 1er au 11 octobre -> remise 10%)
		BigDecimal tarifLongueDuree = tarificationService.calculerTarifAvecStrategie(
				v, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 11), "LONGUE_DUREE");
		// 10 jours * 100 = 1000 - 10% = 900.00
		assertThat(tarifLongueDuree).isEqualByComparingTo(new BigDecimal("900.00"));

		// Location standard (2 jours : du 1er au 3 octobre -> 100 * 2 = 200.00)
		BigDecimal tarifStandard = tarificationService.calculerTarifAvecStrategie(
				v, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 3), "STANDARD");
		assertThat(tarifStandard).isEqualByComparingTo(new BigDecimal("200.00"));

		// Location entreprise (2 jours : 2 * 100 = 200 - 15% = 170.00)
		BigDecimal tarifEntreprise = tarificationService.calculerTarifAvecStrategie(
				v, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 3), "ENTREPRISE");
		assertThat(tarifEntreprise).isEqualByComparingTo(new BigDecimal("170.00"));
	}

	@Test
	@DisplayName("3. Tâches planifiées (Spring Scheduler) : Libération automatique des véhicules en fin de location")
	void testSchedulerLibererVehicules() {
		AgenceDTO agence = agenceService.creerAgence(AgenceDTO.builder()
				.nom("Agence Scheduler")
				.ville("Sfax")
				.build());

		Vehicule v = vehiculeRepository.save(Vehicule.builder()
				.immatriculation("555-TN-5555")
				.marque("Volkswagen")
				.modele("Golf 8")
				.categorie(CategorieVehicule.BERLINE)
				.tarifJournalier(new BigDecimal("140.00"))
				.statut(StatutVehicule.LOUE)
				.build());

		ClientDTO client = clientService.inscrireClient(ClientDTO.builder()
				.nom("Ben Salah")
				.prenom("Mehdi")
				.email("mehdi.scheduler@test.tn")
				.numPermis("PERM-SCHED-1")
				.build());

		Client clientEntity = Client.builder().id(client.getId()).build();
		Reservation expiredRes = reservationRepository.save(Reservation.builder()
				.dateDebut(LocalDate.now().minusDays(5))
				.dateFin(LocalDate.now().minusDays(1))
				.statut(StatutReservation.CONFIRMEE)
				.client(clientEntity)
				.vehicule(v)
				.build());

		int liberes = contratEcheanceScheduler.libererVehiculesEnFinDeLocation();
		assertThat(liberes).isGreaterThanOrEqualTo(1);

		Vehicule updatedVehicule = vehiculeRepository.findById(v.getId()).orElseThrow();
		Reservation updatedRes = reservationRepository.findById(expiredRes.getId()).orElseThrow();
		assertThat(updatedVehicule.getStatut()).isEqualTo(StatutVehicule.DISPONIBLE);
		assertThat(updatedRes.getStatut()).isEqualTo(StatutReservation.TERMINEE);
	}
}
