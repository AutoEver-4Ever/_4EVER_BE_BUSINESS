package org.ever._4ever_be_business.hr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeListResponseDto {

    private Integer totalCount; // 전체 직원 수
    private List<EmployeeListItemDto> items; // 직원 목록
    private PageInfo pageInfo; // 페이징 정보
}
