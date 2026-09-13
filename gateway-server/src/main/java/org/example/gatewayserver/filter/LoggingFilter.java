package org.example.gatewayserver.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        // Thời gian bắt đầu
        long startTime = System.currentTimeMillis();
        log.info("========== REQUEST ==========");
        log.info("Method: {}", method);
        log.info("Path: {}", path);
        log.info("Start Time: {}", startTime);

        exchange.getResponse().beforeCommit(() -> {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            exchange.getResponse().getHeaders().add("X-Response-Time", duration + "ms");
            log.info("Method: {}", method);
            log.info("Path: {}", path);
            log.info("Status: {}", exchange.getResponse().getStatusCode());
            log.info("Response Time: {} ms", duration);
            return Mono.empty();
        });
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
