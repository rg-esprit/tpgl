package com.tpgl.tpgl.service;

import com.tpgl.tpgl.domain.Vehicule;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface du service métier de tarification AutoLoc.
 */
public interface ITarificationService {

    /**
     * Calcule le tarif d'une réservation en sélectionnant automatiquement la stratégie adaptée via la Factory.
     */
    BigDecimal calculerTarifReservation(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Calcule le tarif d'une réservation en forçant l'utilisation d'une stratégie nommée.
     */
    BigDecimal calculerTarifAvecStrategie(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin, String strategie);
}
