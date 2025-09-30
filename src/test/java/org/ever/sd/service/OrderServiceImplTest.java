package org.ever.sd.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Test
    @DisplayName("given external disabled when registerOrder then skip http call")
    void givenExternalDisabled_whenRegisterOrder_thenSkipHttpCall() {
        // given
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        OrderServiceImpl service = new OrderServiceImpl(restTemplate);
        ReflectionTestUtils.setField(service, "externalEnabled", false);
        ReflectionTestUtils.setField(service, "externalOrderUrl", "http://localhost:18080/api/orders");

        // when
        service.registerOrder(1L, "상품A", 3);

        // then
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("given external enabled when registerOrder then call external post")
    void givenExternalEnabled_whenRegisterOrder_thenCallExternalPost() {
        // given
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        OrderServiceImpl service = new OrderServiceImpl(restTemplate);
        String url = "http://localhost:18080/api/orders";
        ReflectionTestUtils.setField(service, "externalEnabled", true);
        ReflectionTestUtils.setField(service, "externalOrderUrl", url);

        // when
        service.registerOrder(10L, "상품B", 5);

        // then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate, times(1))
                .postForEntity(urlCaptor.capture(), any(HttpEntity.class), eq(Void.class));
        assertThat(urlCaptor.getValue()).isEqualTo(url);
    }

    @Test
    @DisplayName("given external enabled and http fails when registerOrder then swallow exception")
    void givenExternalEnabled_andHttpFails_whenRegisterOrder_thenSwallowException() {
        // given
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new RestClientException("connection refused"));

        OrderServiceImpl service = new OrderServiceImpl(restTemplate);
        ReflectionTestUtils.setField(service, "externalEnabled", true);
        ReflectionTestUtils.setField(service, "externalOrderUrl", "http://localhost:18080/api/orders");

        // when
        service.registerOrder(7L, "상품C", 2);

        // then
        verify(restTemplate, times(1))
                .postForEntity(any(String.class), any(HttpEntity.class), eq(Void.class));
        // 예외 전파 없이 정상 종료
    }
}


