package com.tpgl.tpgl.aspect;

import com.tpgl.tpgl.domain.RoleEmploye;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation personnalisée (Séance 12 - Spring AOP) permettant de restreindre
 * l'accès à une méthode ou une classe selon le rôle de l'employé AutoLoc.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRole {

    /**
     * Liste des rôles autorisés à invoquer la méthode cible.
     */
    RoleEmploye[] value() default {};

    /**
     * Indique si l'opération est réservée uniquement aux responsables (MANAGER).
     */
    boolean adminOnly() default false;
}
