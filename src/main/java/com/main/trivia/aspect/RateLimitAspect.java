package com.main.trivia.aspect;

import com.main.trivia.annotation.RateLimit;
import com.main.trivia.exception.RateLimitExceededException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RateLimitAspect {
    private final Map<String, RateLimitInfo> ipRequestMap = new ConcurrentHashMap<>();
    private static final long LIMIT_PERIOD = 120 * 1000; // 2 minutes
    private static final int REQUEST_LIMIT = 10;

    private HttpServletRequest request;

    public RateLimitAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Before("@annotation(com.main.trivia.annotation.RateLimit)")
    public void rateLimit() {
        String clientIp = request.getRemoteAddr();
        RateLimitInfo rateLimitInfo = ipRequestMap.computeIfAbsent(clientIp, ip -> new RateLimitInfo());

        synchronized (rateLimitInfo) {
            if (rateLimitInfo.isRateLimited()) {
                throw new RateLimitExceededException("Too Many Requests - Rate limit exceeded");
            }
            rateLimitInfo.recordRequest();
        }
    }

    private static class RateLimitInfo {
        private int requestCount = 0;
        private long startTime = Instant.now().toEpochMilli();

        boolean isRateLimited() {
            long currentTime = Instant.now().toEpochMilli();
            if (currentTime - startTime > LIMIT_PERIOD) {
                requestCount = 0;
                startTime = currentTime;
            }
            return requestCount >= REQUEST_LIMIT;
        }

        void recordRequest() {
            requestCount++;
        }
    }
}
