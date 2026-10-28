package com.tpgl.tpgl.service.tarification;

import com.tpgl.tpgl.domain.Vehicule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Stratégie de tarification entreprise (GoF Strategy).
 * Applique une réduction commerciale de 15% sur le tarif de location du véhicule,
 * majoré du coût des équipements éventuels.
 */
@Component
public class EntrepriseTarificationStrategy implements ITarificationStrategy {

    public static final String NOM_STRATEGIE = "ENTREPRISE";
    private static final BigDecimal COEFFICIENT_REDUCTION = new BigDecimal("0.85");

    @Override
    public BigDecimal calculerTarif(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin) {
        if (vehicule == null || vehicule.getTarifJournalier() == null) {
            throw new IllegalArgumentException("Le véhicule et son tarif journalier sont obligatoires.");
        }
        long nbJours = calculerNombreJours(dateDebut, dateFin);
        BigDecimal coutVehicule = vehicule.getTarifJournalier()
                .multiply(BigDecimal.valueOf(nbJours))
                .multiply(COEFFICIENT_REDUCTION);

        BigDecimal coutEquipements = calculerCoutEquipements(vehicule, nbJours);
        return coutVehicule.add(coutEquipements).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getNomStrategie() {
        return NOM_STRATEGIE;
    }
}
