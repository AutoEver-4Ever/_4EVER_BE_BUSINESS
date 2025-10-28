package org.ever._4ever_be_business.hr.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserCreateRequestDto {
    private String userId;          // userId: uuid v7으로 생성된 userId
    private String userEmail;       // 사용자의 이메일
    private String departmentName;  // 부서 이름
    private String positionName;    // 직급 이름
    private String status;      // ACTIVE, INACTIVE
}
