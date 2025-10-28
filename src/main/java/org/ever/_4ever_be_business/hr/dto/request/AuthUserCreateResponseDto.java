package org.ever._4ever_be_business.hr.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 인가 및 사용자 서버에서 사용자 등록 성공 시, 응답 받을 dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserCreateResponseDto {
    private String userId;
    private String status;
    private String message;
    private String createdAt;
}
