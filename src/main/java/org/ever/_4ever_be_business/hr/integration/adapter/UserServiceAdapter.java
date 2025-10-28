package org.ever._4ever_be_business.hr.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.internel.InternelServerResponse;
import org.ever._4ever_be_business.hr.dto.request.UserInfoRequest;
import org.ever._4ever_be_business.hr.dto.response.UserInfoResponse;
import org.ever._4ever_be_business.hr.integration.port.UserServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * UserServicePort 인터페이스를 구현하여 실제 HTTP API 호출 수행
 * RestClient를 사용하여 User Service와 통신
 * prod 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "external.mock.enabled", havingValue = "false")
public class UserServiceAdapter implements UserServicePort {

    private final RestClient restClient;

    @Value("${external.auth-service.url:http://auth:8081}")
    private String authServiceUrl;

    @Override
    public CompletableFuture<UserInfoResponse> getMultipleUserInfo(List<Long> internelUserIds) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                UserInfoRequest request = new UserInfoRequest(internelUserIds);

                InternelServerResponse<UserInfoResponse> response = restClient.post()
                        .uri(authServiceUrl + "/user/info/internel/multiple?salary=exclude")
                        .body(request)
                        .retrieve()
                        .body(new ParameterizedTypeReference<InternelServerResponse<UserInfoResponse>>() {});

                // 응답 검증
                if (response == null || !response.isSuccess() || response.getData() == null) {
                    log.error("User Service 응답이 올바르지 않습니다 - response: {}", response);
                    throw new RuntimeException("User Service 응답이 올바르지 않습니다.");
                }

                return response.getData();

            } catch (Exception e) {
                log.error("User Service 호출 실패 - internelUserIds: {}", internelUserIds, e);
                throw new RuntimeException("직원 정보 조회 실패", e);
            }
        });
    }
}
