package com.navershop.navershop.config;

/**
 * 메모리량 모니터링 유틸리티
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 11.
 */

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * 메모리 모니터링 유틸리티
 *
 * 크롤링 중 메모리 사용량을 추적하여 OOM 방지
 */
@Slf4j
public class MemoryMonitor {

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private static final long MB = 1024 * 1024;

    /**
     * 현재 메모리 사용량 로깅
     */
    public static void logMemoryUsage(String context) {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        long used = heapUsage.getUsed() / MB;
        long max = heapUsage.getMax() / MB;
        long committed = heapUsage.getCommitted() / MB;

        double usagePercent = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;

        log.info("📊 [{}] 메모리: {}MB / {}MB ({}%) | Committed: {}MB",
                context, used, max, String.format("%.1f", usagePercent), committed);
    }

    /**
     * 메모리 사용률 확인
     *
     * @return 메모리 사용률 (0.0 ~ 1.0)
     */
    public static double getMemoryUsageRatio() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        return (double) heapUsage.getUsed() / heapUsage.getMax();
    }

    /**
     * 메모리가 위험 수준인지 확인
     *
     * @param threshold 임계값 (0.0 ~ 1.0, 예: 0.85 = 85%)
     * @return 임계값 초과 여부
     */
    public static boolean isMemoryDanger(double threshold) {
        return getMemoryUsageRatio() > threshold;
    }

    /**
     * GC 유도 (필요 시)
     */
    public static void requestGC() {
        log.info("🧹 GC 요청 중...");
        long before = memoryBean.getHeapMemoryUsage().getUsed() / MB;

        System.gc();
        System.runFinalization();

        // GC 완료 대기 (최대 1초)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long after = memoryBean.getHeapMemoryUsage().getUsed() / MB;
        long freed = before - after;

        log.info("✅ GC 완료: {}MB 해제", freed);
    }

    /**
     * 메모리 사용량 모니터링 (자동 GC)
     *
     * 85% 이상 사용 시 자동으로 GC 유도
     */
    public static void monitorAndCleanIfNeeded(String context) {
        logMemoryUsage(context);

        if (isMemoryDanger(0.85)) {
            log.warn("⚠️ 메모리 사용률 85% 초과! GC 유도");
            requestGC();
            logMemoryUsage(context + " (After GC)");
        }
    }

    /**
     * 메모리 사용 가능 여부 확인
     *
     * @param requiredMB 필요한 메모리 (MB)
     * @return 메모리 충분 여부
     */
    public static boolean hasAvailableMemory(long requiredMB) {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long available = (heapUsage.getMax() - heapUsage.getUsed()) / MB;

        if (available < requiredMB) {
            log.warn("⚠️ 메모리 부족: 필요 {}MB, 사용 가능 {}MB", requiredMB, available);
            return false;
        }

        return true;
    }
}
