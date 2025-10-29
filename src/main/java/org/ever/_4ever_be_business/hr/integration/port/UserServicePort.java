package org.ever._4ever_be_business.hr.integration.port;

import org.ever._4ever_be_business.hr.dto.response.UserInfoResponse;
import org.ever.event.CreateAuthUserEvent;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 비즈니스 로직에서 외부 User Service와의 의존성을 추상화
 * 실제 구현은 Adapter에서 담당
 */
public interface UserServicePort {

    /**
     * 여러 직원의 정보를 비동기로 조회
     *
     * @param internelUserIds 조회할 내부 직원 ID 목록
     * @return CompletableFuture<UserInfoResponse> User Service로부터 받은 직원 정보
     */
    CompletableFuture<UserInfoResponse> getMultipleUserInfo(List<Long> internelUserIds);

    /**
     * 인증 서비스에 내부 사용자 생성을 요청한다.
     *
     * @param request 생성 이벤트
     * @return 항상 null이 아닌 {@link CompletableFuture}; 즉시 실패를 전달해야 할 경우 {@link CompletableFuture#failedFuture(Throwable)} 사용
     */
    @NonNull
    CompletableFuture<Void> createAuthUserPort(CreateAuthUserEvent request);
}
