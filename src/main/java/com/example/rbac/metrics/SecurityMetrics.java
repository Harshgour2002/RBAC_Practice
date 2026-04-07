package com.example.rbac.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class SecurityMetrics {
    public final Counter loginFailures;
    public final Counter authErrors;
    public final Timer authorizationLatency;

    public SecurityMetrics(MeterRegistry meterRegistry) {
        this.loginFailures = meterRegistry.counter("security_login_failures_total");
        this.authErrors = meterRegistry.counter("security_error_total");
        this.authorizationLatency = meterRegistry.timer("security_authorization_latency");
    }
}
