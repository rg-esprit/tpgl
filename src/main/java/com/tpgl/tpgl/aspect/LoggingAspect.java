package com.tpgl.tpgl.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect de journalisation transversale (Séance 12 - Spring AOP).
 * Trace automatiquement l'entrée, les arguments, la valeur de retour et les exceptions
 * des couches Service et Controller.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.tpgl.tpgl.service..*(..)) || execution(* com.tpgl.tpgl.web.controller..*(..))")
    public void applicationLayerPointcut() {
        // Pointcut couvrant les services métiers et les contrôleurs REST
    }

    @Before("applicationLayerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.debug("[AOP-LOG] Entrée dans {} avec arguments : {}", methodName, Arrays.toString(args));
    }

    @AfterReturning(pointcut = "applicationLayerPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.debug("[AOP-LOG] Sortie normale de {} -> résultat : {}", methodName, result);
    }

    @AfterThrowing(pointcut = "applicationLayerPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().toShortString();
        log.warn("[AOP-LOG] Exception levée dans {} : {} - {}",
                methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }
}
