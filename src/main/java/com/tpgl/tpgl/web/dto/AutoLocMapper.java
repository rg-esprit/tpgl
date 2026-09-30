package com.tpgl.tpgl.web.dto;

import com.tpgl.tpgl.domain.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mapper centralisé pour convertir les entités du domaine AutoLoc vers leurs DTOs respectifs et inversement.
 */
@Component
public class AutoLocMapper {

    public AgenceDTO toAgenceDto(Agence agence) {
        if (agence == null) {
            return null;
        }
        int nbVehicules = agence.getVehicules() != null ? agence.getVehicules().size() : 0;
        int nbEmployes = agence.getEmployes() != null ? agence.getEmployes().size() : 0;
        return AgenceDTO.builder()
                .id(agence.getId())
                .nom(agence.getNom())
                .ville(agence.getVille())
                .adresse(agence.getAdresse())
                .telephone(agence.getTelephone())
                .nombreVehicules(nbVehicules)
                .nombreEmployes(nbEmployes)
                .build();
    }

    public Agence toAgenceEntity(AgenceDTO dto) {
        if (dto == null) {
            return null;
        }
        return Agence.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .ville(dto.getVille())
                .adresse(dto.getAdresse())
                .telephone(dto.getTelephone())
                .build();
    }

    public EmployeDTO toEmployeDto(Employe employe) {
        if (employe == null) {
            return null;
        }
        return EmployeDTO.builder()
                .id(employe.getId())
                .nom(employe.getNom())
                .prenom(employe.getPrenom())
                .role(employe.getRole())
                .agenceId(employe.getAgence() != null ? employe.getAgence().getId() : null)
                .agenceNom(employe.getAgence() != null ? employe.getAgence().getNom() : null)
                .build();
    }

    public Employe toEmployeEntity(EmployeDTO dto) {
        if (dto == null) {
            return null;
        }
        return Employe.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .role(dto.getRole())
                .build();
    }

    public EquipementDTO toEquipementDto(Equipement equipement) {
        if (equipement == null) {
            return null;
        }
        return EquipementDTO.builder()
                .id(equipement.getId())
                .libelle(equipement.getLibelle())
                .tarifJournalier(equipement.getTarifJournalier())
                .build();
    }

    public Equipement toEquipementEntity(EquipementDTO dto) {
        if (dto == null) {
            return null;
        }
        return Equipement.builder()
                .id(dto.getId())
                .libelle(dto.getLibelle())
                .tarifJournalier(dto.getTarifJournalier() != null ? dto.getTarifJournalier() : new BigDecimal("5.00"))
                .build();
    }

    public MaintenanceDTO toMaintenanceDto(Maintenance maintenance) {
        if (maintenance == null) {
            return null;
        }
        return MaintenanceDTO.builder()
                .id(maintenance.getId())
                .dateDebut(maintenance.getDateDebut())
                .dateFin(maintenance.getDateFin())
                .description(maintenance.getDescription())
                .vehiculeId(maintenance.getVehicule() != null ? maintenance.getVehicule().getId() : null)
                .vehiculeImmatriculation(maintenance.getVehicule() != null ? maintenance.getVehicule().getImmatriculation() : null)
                .build();
    }

    public Maintenance toMaintenanceEntity(MaintenanceDTO dto) {
        if (dto == null) {
            return null;
        }
        return Maintenance.builder()
                .id(dto.getId())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .description(dto.getDescription())
                .build();
    }

    public ClientDTO toClientDto(Client client) {
        if (client == null) {
            return null;
        }
        return ClientDTO.builder()
                .id(client.getId())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .numPermis(client.getNumPermis())
                .dateInscription(client.getDateInscription())
                .build();
    }

    public Client toClientEntity(ClientDTO dto) {
        if (dto == null) {
            return null;
        }
        return Client.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .telephone(dto.getTelephone())
                .numPermis(dto.getNumPermis())
                .dateInscription(dto.getDateInscription() != null ? dto.getDateInscription() : LocalDate.now())
                .build();
    }

    public ReservationResponseDTO toReservationResponseDto(Reservation reservation, BigDecimal montantEstime) {
        if (reservation == null) {
            return null;
        }
        Client client = reservation.getClient();
        Vehicule vehicule = reservation.getVehicule();
        Contrat contrat = reservation.getContrat();

        String clientNomComplet = client != null
                ? (client.getPrenom() + " " + client.getNom()).trim()
                : null;
        String vehiculeMarqueModele = vehicule != null
                ? (vehicule.getMarque() + " " + vehicule.getModele()).trim()
                : null;

        BigDecimal montant = montantEstime;
        if (montant == null && contrat != null) {
            montant = contrat.getMontantTotal();
        }

        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .dateDebut(reservation.getDateDebut())
                .dateFin(reservation.getDateFin())
                .statut(reservation.getStatut())
                .clientId(client != null ? client.getId() : null)
                .clientNomComplet(clientNomComplet)
                .vehiculeId(vehicule != null ? vehicule.getId() : null)
                .vehiculeImmatriculation(vehicule != null ? vehicule.getImmatriculation() : null)
                .vehiculeMarqueModele(vehiculeMarqueModele)
                .montantEstime(montant)
                .contratId(contrat != null ? contrat.getId() : null)
                .build();
    }

    public ReservationResponseDTO toReservationResponseDto(Reservation reservation) {
        return toReservationResponseDto(reservation, null);
    }

    public ContratDTO toContratDto(Contrat contrat, BigDecimal montantPaye) {
        if (contrat == null) {
            return null;
        }
        Reservation reservation = contrat.getReservation();
        Client client = reservation != null ? reservation.getClient() : null;
        Vehicule vehicule = reservation != null ? reservation.getVehicule() : null;

        BigDecimal total = contrat.getMontantTotal() != null ? contrat.getMontantTotal() : BigDecimal.ZERO;
        BigDecimal paye = montantPaye;
        if (paye == null) {
            if (contrat.getPaiements() != null) {
                paye = contrat.getPaiements().stream()
                        .map(Paiement::getMontant)
                        .filter(m -> m != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            } else {
                paye = BigDecimal.ZERO;
            }
        }
        BigDecimal reste = total.subtract(paye).max(BigDecimal.ZERO);

        return ContratDTO.builder()
                .id(contrat.getId())
                .dateSignature(contrat.getDateSignature())
                .montantTotal(total)
                .valide(contrat.isValide())
                .reservationId(reservation != null ? reservation.getId() : null)
                .clientNomComplet(client != null ? (client.getPrenom() + " " + client.getNom()).trim() : null)
                .vehiculeImmatriculation(vehicule != null ? vehicule.getImmatriculation() : null)
                .montantPaye(paye)
                .resteAPayer(reste)
                .build();
    }

    public ContratDTO toContratDto(Contrat contrat) {
        return toContratDto(contrat, null);
    }

    public PaiementDTO toPaiementDto(Paiement paiement) {
        if (paiement == null) {
            return null;
        }
        return PaiementDTO.builder()
                .id(paiement.getId())
                .montant(paiement.getMontant())
                .datePaiement(paiement.getDatePaiement())
                .modePaiement(paiement.getModePaiement())
                .contratId(paiement.getContrat() != null ? paiement.getContrat().getId() : null)
                .build();
    }

    public Paiement toPaiementEntity(PaiementDTO dto) {
        if (dto == null) {
            return null;
        }
        return Paiement.builder()
                .id(dto.getId())
                .montant(dto.getMontant())
                .datePaiement(dto.getDatePaiement() != null ? dto.getDatePaiement() : LocalDate.now())
                .modePaiement(dto.getModePaiement())
                .build();
    }
}
