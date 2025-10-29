package org.ever._4ever_be_business.hr.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.mock.MockDataProvider;
import org.ever._4ever_be_business.hr.dto.response.UserInfoResponse;
import org.ever._4ever_be_business.hr.integration.port.UserServicePort;
import org.ever.event.CreateAuthUserEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * UserServicePort의 Mock 구현체
 * dev 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockUserServiceAdapter implements UserServicePort {

    private final MockDataProvider mockDataProvider;

    @Override
    public CompletableFuture<UserInfoResponse> getMultipleUserInfo(List<Long> internelUserIds) {
        log.info("[MOCK ADAPTER] getMultipleUserInfo 호출 - internelUserIds: {}", internelUserIds);
        return CompletableFuture.completedFuture(mockDataProvider.createMockUserInfo(internelUserIds));
    }

    @Override
    public CompletableFuture<Void> createAuthUserPort(CreateAuthUserEvent request) {
        log.info("[MOCK ADAPTER] createInternalUserAccount 호출");
        return CompletableFuture.completedFuture(null);
    }
}
