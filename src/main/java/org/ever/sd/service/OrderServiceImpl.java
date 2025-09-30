package org.ever.sd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final RestTemplate restTemplate;

    @Value("${external.scm.enabled:true}")
    private boolean externalEnabled;
    @Value("${external.scm.url}")
    private String externalOrderUrl;

    @Override
    public void registerOrder(Long id, String name, Integer count) {
        if (!externalEnabled) {
            log.info(
                "Skip external order POST because external.sales-order.enabled=false. payload id={}, name={}, count={}",
                id, name, count);
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        payload.put("name", name);
        payload.put("count", count);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(externalOrderUrl, requestEntity, Void.class);
            log.info("Posted order to external server. url={}, id={}, name={}, count={}",
                externalOrderUrl, id, name, count);
        } catch (RestClientException e) {
            log.warn("Failed to POST order to external server. url={}, message={}",
                externalOrderUrl, e.getMessage());
            // 필요 시 재시도/알림/큐 적재 등 확장 가능
        }
    }
}


