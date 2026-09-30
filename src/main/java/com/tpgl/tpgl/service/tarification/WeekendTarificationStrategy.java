package com.tpgl.tpgl.service.tarification;

import com.tpgl.tpgl.domain.Vehicule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Stratégie de tarification week-end (GoF Strategy).
 * Applique une majoration de +15% sur les jours de week-end (samedi et dimanche),
 * le tarif standard en semaine (lundi à vendredi), ainsi que le coût des équipements.
 */
@Component
public class WeekendTarificationStrategy implements ITarificationStrategy {

    public static final String NOM_STRATEGIE = "WEEKEND";
    private static final BigDecimal COEFFICIENT_WEEKEND = new BigDecimal("1.15");

    @Override
    public BigDecimal calculerTarif(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin) {
        if (vehicule == null || vehicule.getTarifJournalier() == null) {
            throw new IllegalArgumentException("Le véhicule et son tarif journalier sont obligatoires.");
        }
        long nbJours = calculerNombreJours(dateDebut, dateFin);
        BigDecimal tarifBase = vehicule.getTarifJournalier();
        BigDecimal coutVehicule = BigDecimal.ZERO;

        for (long i = 0; i < nbJours; i++) {
            LocalDate jour = dateDebut.plusDays(i);
            DayOfWeek dayOfWeek = jour.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                coutVehicule = coutVehicule.add(tarifBase.multiply(COEFFICIENT_WEEKEND));
            } else {
                coutVehicule = coutVehicule.add(tarifBase);
            }
        }

        BigDecimal coutEquipements = calculerCoutEquipements(vehicule, nbJours);
        return coutVehicule.add(coutEquipements).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getNomStrategie() {
        return NOM_STRATEGIE;
    }
}
