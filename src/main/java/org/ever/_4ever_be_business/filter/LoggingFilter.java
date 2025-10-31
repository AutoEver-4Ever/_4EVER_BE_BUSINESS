package org.ever._4ever_be_business.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Request per 단위 Filter

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static final long MAX_LOG_LENGTH = 1000;

    public String maskSensitiveData(String requestBody) {
        return requestBody.replaceAll("\"password\"\\s*:\\s*\"(.*?)\"", "\"password\":\"****\"");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        ContentCachingRequestWrapper wrappedReq = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedRes = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedReq, wrappedRes);
        } catch (Exception e) {
            log.warn("[WARN] 로깅 처리 중 에러 발생", e);
        } finally {
            try {
                String requestBody = new String(wrappedReq.getContentAsByteArray(), StandardCharsets.UTF_8).trim();
                if (!requestBody.isEmpty()) {
                    log.info("[INFO] 요청 본문: {}", maskSensitiveData(requestBody));
                }

                String responseString = "";
                byte[] content = wrappedRes.getContentAsByteArray();
                if (content.length > 0) {
                    String responseBody = new String(content, StandardCharsets.UTF_8).trim();

                    responseString = responseBody.length() > MAX_LOG_LENGTH
                            ? "[RES] 응답 본문이 너무 커서 생략되었습니다."
                            : "[RES] 응답 본문: " + responseBody;
                }
                log.info("\n" + "HTTP 메서드: [ {} ] 엔드포인트: [ {} ] Content-Type: [ {} ] Authorization: [ {} ] User-agent: [ {} ] Host: [ {} ] Content-length: [ {} ] 응답 본문: [ {} ]",
                    request.getMethod(), request.getRequestURI(),
                    request.getHeader("content-type"),
                    request.getHeader("authorization"),
                    request.getHeader("member-agent"),
                    request.getHeader("host"),
                    request.getHeader("content-length"),
                    responseString
            );

            long end = System.currentTimeMillis();
            log.info(">>> 소요 시간: {} sec", (end-start) / 1000.0);
            } finally {
                wrappedRes.copyBodyToResponse();
            }
        }
    }
}
