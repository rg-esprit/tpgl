package com.tpgl.tpgl.aspect;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Aspect de supervision des performances (Séance 12 - Spring AOP).
 * Mesure le temps d'exécution en millisecondes de tous les appels de la couche service,
 * émet une alerte en cas de lenteur et maintient des métriques d'exécution en mémoire.
 */
@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    private final AtomicLong totalCalls = new AtomicLong(0);
    private final AtomicLong maxExecutionTimeMs = new AtomicLong(0);

    @Getter
    @Setter
    private long slowThresholdMs = 500L;

    @Around("execution(* com.tpgl.tpgl.service..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNanos = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
            totalCalls.incrementAndGet();
            maxExecutionTimeMs.accumulateAndGet(durationMs, Math::max);

            String methodName = joinPoint.getSignature().toShortString();
            if (durationMs >= slowThresholdMs) {
                log.warn("[AOP-PERF] Exécution lente détectée sur {} : {} ms (seuil: {} ms)",
                        methodName, durationMs, slowThresholdMs);
            } else {
                log.debug("[AOP-PERF] {} exécuté en {} ms", methodName, durationMs);
            }
        }
    }

    public long getTotalCalls() {
        return totalCalls.get();
    }

    public long getMaxExecutionTimeMs() {
        return maxExecutionTimeMs.get();
    }

    public void resetMetrics() {
        totalCalls.set(0);
        maxExecutionTimeMs.set(0);
    }
}
