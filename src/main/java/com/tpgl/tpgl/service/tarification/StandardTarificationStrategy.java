package com.tpgl.tpgl.service.tarification;

import com.tpgl.tpgl.domain.Vehicule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Stratégie de tarification standard (GoF Strategy).
 * Applique le tarif journalier de base multiplié par le nombre de jours,
 * majoré du coût journalier des équipements optionnels.
 */
@Component
public class StandardTarificationStrategy implements ITarificationStrategy {

    public static final String NOM_STRATEGIE = "STANDARD";

    @Override
    public BigDecimal calculerTarif(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin) {
        if (vehicule == null || vehicule.getTarifJournalier() == null) {
            throw new IllegalArgumentException("Le véhicule et son tarif journalier sont obligatoires.");
        }
        long nbJours = calculerNombreJours(dateDebut, dateFin);
        BigDecimal coutVehicule = vehicule.getTarifJournalier().multiply(BigDecimal.valueOf(nbJours));
        BigDecimal coutEquipements = calculerCoutEquipements(vehicule, nbJours);
        return coutVehicule.add(coutEquipements).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getNomStrategie() {
        return NOM_STRATEGIE;
    }
}
