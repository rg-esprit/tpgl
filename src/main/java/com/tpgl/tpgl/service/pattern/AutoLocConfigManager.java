package com.tpgl.tpgl.service.pattern;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Gestionnaire de configuration globale AutoLoc implémentant le patron GoF Singleton (thread-safe).
 * Centralise les paramètres globaux de l'application (devise, pénalité de retard, délai d'alerte).
 */
@Getter
@Setter
public final class AutoLocConfigManager {

    private static volatile AutoLocConfigManager instance;

    private String nomApplication;
    private String devise;
    private double tauxPenaliteRetard;
    private int delaiAlerteEcheanceJours;
    private int dureeMaximaleLocationJours;

    private AutoLocConfigManager() {
        resetDefaults();
    }

    /**
     * Retourne l'instance unique du gestionnaire de configuration (Double-Checked Locking).
     */
    public static AutoLocConfigManager getInstance() {
        if (instance == null) {
            synchronized (AutoLocConfigManager.class) {
                if (instance == null) {
                    instance = new AutoLocConfigManager();
                }
            }
        }
        return instance;
    }

    /**
     * Réinitialise les paramètres aux valeurs par défaut d'AutoLoc.
     */
    public void resetDefaults() {
        this.nomApplication = "AutoLoc";
        this.devise = "EUR";
        this.tauxPenaliteRetard = 1.25;
        this.delaiAlerteEcheanceJours = 3;
        this.dureeMaximaleLocationJours = 90;
    }

    public BigDecimal getTauxPenaliteRetardBigDecimal() {
        return BigDecimal.valueOf(this.tauxPenaliteRetard);
    }
}
