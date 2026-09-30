package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.CategorieVehicule;
import com.tpgl.tpgl.domain.StatutVehicule;
import com.tpgl.tpgl.domain.Vehicule;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Spécifications JPA (Criteria API) pour le filtrage dynamique des véhicules (Figure 2).
 */
public final class VehiculeSpecifications {

    private VehiculeSpecifications() {
    }

    public static Specification<Vehicule> hasVille(String ville) {
        return (root, query, cb) -> {
            if (ville == null || ville.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.join("agence").get("ville")), ville.toLowerCase());
        };
    }

    public static Specification<Vehicule> hasCategorie(CategorieVehicule categorie) {
        return (root, query, cb) -> {
            if (categorie == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("categorie"), categorie);
        };
    }

    public static Specification<Vehicule> hasStatut(StatutVehicule statut) {
        return (root, query, cb) -> {
            if (statut == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("statut"), statut);
        };
    }

    public static Specification<Vehicule> hasMaxTarif(BigDecimal maxTarif) {
        return (root, query, cb) -> {
            if (maxTarif == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("tarifJournalier"), maxTarif);
        };
    }
}
