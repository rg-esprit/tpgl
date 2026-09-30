package com.tpgl.tpgl.service.tarification;

import com.tpgl.tpgl.domain.Vehicule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Stratégie de tarification longue durée (GoF Strategy).
 * Applique une remise de 10% à partir de 7 jours de location, et de 20% à partir de 14 jours,
 * sur le tarif du véhicule, plus le coût journalier des équipements.
 */
@Component
public class LongueDureeTarificationStrategy implements ITarificationStrategy {

    public static final String NOM_STRATEGIE = "LONGUE_DUREE";
    private static final BigDecimal REMISE_7_JOURS = new BigDecimal("0.90");
    private static final BigDecimal REMISE_14_JOURS = new BigDecimal("0.80");

    @Override
    public BigDecimal calculerTarif(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin) {
        if (vehicule == null || vehicule.getTarifJournalier() == null) {
            throw new IllegalArgumentException("Le véhicule et son tarif journalier sont obligatoires.");
        }
        long nbJours = calculerNombreJours(dateDebut, dateFin);
        BigDecimal coutVehicule = vehicule.getTarifJournalier().multiply(BigDecimal.valueOf(nbJours));

        if (nbJours >= 14) {
            coutVehicule = coutVehicule.multiply(REMISE_14_JOURS);
        } else if (nbJours >= 7) {
            coutVehicule = coutVehicule.multiply(REMISE_7_JOURS);
        }

        BigDecimal coutEquipements = calculerCoutEquipements(vehicule, nbJours);
        return coutVehicule.add(coutEquipements).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getNomStrategie() {
        return NOM_STRATEGIE;
    }
}
