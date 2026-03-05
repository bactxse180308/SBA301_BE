package com.sba302.electroshop.config;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Explicitly registers Micrometer binders so all metric groups appear in Prometheus/Grafana.
 * Without this, HikariCP, Logback, HTTP, GC metrics may show as "No data".
 */
@Configuration
public class MetricsConfig {

    // ─── JVM ─────────────────────────────────────────────────────────────────

    @Bean
    public JvmMemoryMetrics jvmMemoryMetrics() {
        return new JvmMemoryMetrics();
    }

    @Bean
    public JvmGcMetrics jvmGcMetrics() {
        return new JvmGcMetrics();
    }

    @Bean
    public JvmThreadMetrics jvmThreadMetrics() {
        return new JvmThreadMetrics();
    }

    @Bean
    public ClassLoaderMetrics classLoaderMetrics() {
        return new ClassLoaderMetrics();
    }

    @Bean
    public JvmHeapPressureMetrics jvmHeapPressureMetrics() {
        return new JvmHeapPressureMetrics();
    }

    // ─── System ──────────────────────────────────────────────────────────────

    @Bean
    public ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics();
    }

    @Bean
    public UptimeMetrics uptimeMetrics() {
        return new UptimeMetrics();
    }

    @Bean
    public FileDescriptorMetrics fileDescriptorMetrics() {
        return new FileDescriptorMetrics();
    }

    // ─── Logback ─────────────────────────────────────────────────────────────

    @Bean
    public LogbackMetrics logbackMetrics() {
        return new LogbackMetrics();
    }

    // ─── Tomcat ──────────────────────────────────────────────────────────────

    @Bean
    public io.micrometer.core.instrument.binder.tomcat.TomcatMetrics tomcatMetrics() {
        return new io.micrometer.core.instrument.binder.tomcat.TomcatMetrics(null, java.util.Collections.emptyList());
    }

    // ─── HikariCP ────────────────────────────────────────────────────────────

    /**
     * Đăng ký HikariCP metrics thủ công.
     * Chỉ kích hoạt khi DataSource là HikariDataSource.
     */
    @Bean
    @ConditionalOnClass(HikariDataSource.class)
    public Object hikariMetricsBinder(DataSource dataSource, MeterRegistry registry) {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.setMetricRegistry(null); // clear old registry nếu có
            hikariDataSource.setMetricsTrackerFactory(
                    new com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory(registry)
            );
        }
        return "hikariMetricsBound";
    }
}
