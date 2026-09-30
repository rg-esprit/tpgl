package com.tpgl.tpgl.service.tarification;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fabrique de stratégies de tarification (GoF Factory Pattern).
 * Sélectionne automatiquement la stratégie appropriée en fonction de la durée
 * et de la période de location, ou à partir du nom explicite de la stratégie.
 */
@Component
public class TarificationStrategyFactory {

    private final StandardTarificationStrategy standardStrategy;
    private final WeekendTarificationStrategy weekendStrategy;
    private final LongueDureeTarificationStrategy longueDureeStrategy;
    private final Map<String, ITarificationStrategy> strategiesByName;

    public TarificationStrategyFactory(
            StandardTarificationStrategy standardStrategy,
            WeekendTarificationStrategy weekendStrategy,
            LongueDureeTarificationStrategy longueDureeStrategy,
            List<ITarificationStrategy> allStrategies
    ) {
        this.standardStrategy = standardStrategy;
        this.weekendStrategy = weekendStrategy;
        this.longueDureeStrategy = longueDureeStrategy;
        this.strategiesByName = allStrategies.stream()
                .collect(Collectors.toMap(
                        s -> s.getNomStrategie().toUpperCase(),
                        Function.identity(),
                        (a, b) -> a
                ));
    }

    /**
     * Sélectionne automatiquement la stratégie selon la période de location :
     * - Durée >= 7 jours : LONGUE_DUREE
     * - Inclut au moins un samedi ou un dimanche : WEEKEND
     * - Sinon : STANDARD
     */
    public ITarificationStrategy getStrategy(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début.");
        }

        long nbJours = Math.max(1L, ChronoUnit.DAYS.between(dateDebut, dateFin));
        if (nbJours >= 7) {
            return longueDureeStrategy;
        }

        if (contientWeekend(dateDebut, nbJours)) {
            return weekendStrategy;
        }

        return standardStrategy;
    }

    /**
     * Récupère une stratégie par son nom ("STANDARD", "WEEKEND", "LONGUE_DUREE").
     */
    public ITarificationStrategy getStrategy(String nomStrategie) {
        if (nomStrategie == null || nomStrategie.isBlank()) {
            return standardStrategy;
        }
        ITarificationStrategy strategy = strategiesByName.get(nomStrategie.trim().toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Stratégie de tarification inconnue : " + nomStrategie);
        }
        return strategy;
    }

    private boolean contientWeekend(LocalDate dateDebut, long nbJours) {
        for (long i = 0; i < nbJours; i++) {
            DayOfWeek day = dateDebut.plusDays(i).getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                return true;
            }
        }
        return false;
    }
}
