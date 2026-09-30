package com.tpgl.tpgl.service;

import com.tpgl.tpgl.domain.Vehicule;
import com.tpgl.tpgl.service.tarification.ITarificationStrategy;
import com.tpgl.tpgl.service.tarification.TarificationStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Implémentation du service de tarification s'appuyant sur les patrons GoF Strategy et Factory.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TarificationServiceImpl implements ITarificationService {

    private final TarificationStrategyFactory strategyFactory;

    @Override
    public BigDecimal calculerTarifReservation(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin) {
        ITarificationStrategy strategy = strategyFactory.getStrategy(dateDebut, dateFin);
        log.debug("Calcul du tarif pour le véhicule {} du {} au {} avec la stratégie {}",
                vehicule != null ? vehicule.getImmatriculation() : "null",
                dateDebut, dateFin, strategy.getNomStrategie());
        return strategy.calculerTarif(vehicule, dateDebut, dateFin);
    }

    @Override
    public BigDecimal calculerTarifAvecStrategie(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin, String strategie) {
        ITarificationStrategy strategy = strategyFactory.getStrategy(strategie);
        log.debug("Calcul du tarif forcé pour le véhicule {} du {} au {} avec la stratégie {}",
                vehicule != null ? vehicule.getImmatriculation() : "null",
                dateDebut, dateFin, strategy.getNomStrategie());
        return strategy.calculerTarif(vehicule, dateDebut, dateFin);
    }
}
