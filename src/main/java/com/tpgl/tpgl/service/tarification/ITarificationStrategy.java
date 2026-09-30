package com.tpgl.tpgl.service.tarification;

import com.tpgl.tpgl.domain.Equipement;
import com.tpgl.tpgl.domain.Vehicule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Interface du patron de conception GoF Strategy pour le calcul du tarif de location.
 * Permet de varier dynamiquement l'algorithme de tarification selon la durée ou la période.
 */
public interface ITarificationStrategy {

    /**
     * Calcule le tarif total d'une location pour un véhicule sur une période donnée.
     *
     * @param vehicule  le véhicule loué (incluant ses équipements éventuels)
     * @param dateDebut la date de début de location
     * @param dateFin   la date de fin de location
     * @return le montant total calculé (échelle de 2 décimales)
     */
    BigDecimal calculerTarif(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Retourne l'identifiant unique de la stratégie (ex: "STANDARD", "WEEKEND", "LONGUE_DUREE").
     *
     * @return le nom de la stratégie
     */
    String getNomStrategie();

    /**
     * Valide les paramètres et calcule le nombre de jours de location (minimum 1 jour).
     */
    default long calculerNombreJours(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de début et de fin de location sont obligatoires.");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début.");
        }
        long days = ChronoUnit.DAYS.between(dateDebut, dateFin);
        return Math.max(1L, days);
    }

    /**
     * Calcule le coût additionnel des équipements associés au véhicule sur la durée de location.
     */
    default BigDecimal calculerCoutEquipements(Vehicule vehicule, long nbJours) {
        if (vehicule == null || vehicule.getEquipements() == null || vehicule.getEquipements().isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sommeJournalierEquipements = vehicule.getEquipements().stream()
                .filter(e -> e != null && e.getTarifJournalier() != null)
                .map(Equipement::getTarifJournalier)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sommeJournalierEquipements.multiply(BigDecimal.valueOf(nbJours));
    }
}
