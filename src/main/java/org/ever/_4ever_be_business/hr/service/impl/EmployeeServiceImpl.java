package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.dao.EmployeeDAO;
import org.ever._4ever_be_business.hr.dto.request.*;
import org.ever._4ever_be_business.hr.dto.response.EmployeeCreateResponseDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeDetailDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.entity.*;
import org.ever._4ever_be_business.hr.enums.UserStatus;
import org.ever._4ever_be_business.hr.integration.port.UserServicePort;
import org.ever._4ever_be_business.hr.repository.*;
import org.ever._4ever_be_business.hr.service.EmployeeService;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final TrainingRepository trainingRepository;
    private final InternelUserRepository internalUserRepository;
    private final EmployeeTrainingRepository employeeTrainingRepository;
    private final UserServicePort userServicePort;

    @Override
    @Transactional(readOnly = true)
    public EmployeeDetailDto getEmployeeDetail(String employeeId) {
        log.info("직원 상세 정보 조회 요청 - employeeId: {}", employeeId);

        EmployeeDetailDto result = employeeDAO.findEmployeeDetailById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        log.info("직원 상세 정보 조회 성공 - employeeId: {}, employeeName: {}",
                employeeId, result.getName());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeListItemDto> getEmployeeList(EmployeeListSearchConditionVo condition, Pageable pageable) {
        log.info("직원 목록 조회 요청 - department: {}, position: {}, name: {}",
                condition.getDepartment(), condition.getPosition(), condition.getName());

        Page<EmployeeListItemDto> result = employeeDAO.findEmployeeList(condition, pageable);

        log.info("직원 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional
    public void updateEmployee(String employeeId, UpdateEmployeeRequestDto requestDto) {
        log.info("직원 정보 수정 요청 - employeeId: {}", employeeId);

        // 1. Employee 조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        InternelUser internelUser = employee.getInternelUser();

        // 2. Position 조회 (positionId가 제공된 경우)
        Position position = null;
        if (requestDto.getPositionId() != null) {
            position = positionRepository.findById(requestDto.getPositionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "직급 정보를 찾을 수 없습니다."));
        }

        // 3. InternelUser 정보 업데이트
        internelUser.updateEmployeeInfo(
                requestDto.getEmployeeName(),
                position
        );

        // 4. 저장 (Dirty Checking으로 자동 저장)
        log.info("직원 정보 수정 성공 - employeeId: {}, employeeName: {}",
                employeeId, requestDto.getEmployeeName());
    }

    @Override
    @Transactional
    public void requestTraining(TrainingRequestDto requestDto) {
        log.info("교육 프로그램 신청 요청 - employeeId: {}, programId: {}",
                requestDto.getEmployeeId(), requestDto.getProgramId());

        // 1. Employee 조회
        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. Training (Program) 조회
        Training training = trainingRepository.findById(requestDto.getProgramId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "교육 프로그램 정보를 찾을 수 없습니다."));

        // 3. EmployeeTraining 생성 및 저장
        EmployeeTraining employeeTraining = new EmployeeTraining(
                employee,
                training,
                false  // completionStatus: 신청 시점에는 미완료
        );

        employeeTrainingRepository.save(employeeTraining);

        log.info("교육 프로그램 신청 성공 - employeeId: {}, trainingId: {}, trainingName: {}",
                requestDto.getEmployeeId(), training.getId(), training.getTrainingName());
    }

    @Override
    public EmployeeCreateResponseDto createEmployee(EmployeeCreateRequestDto requestDto) {
        log.info("[INFO] 내부 사용자 등록 시작 - name: {}, email: {}", requestDto.getName(), requestDto.getEmail());

        // 직급 조회
        Position position = positionRepository.findById(requestDto.getPositionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "직급 정보를 찾을 수 없습니다."));

        Department department = position.getDepartment();

        // userId 생성
        String userId = UuidV7Generator.generate();

        // employeeNumber(employeeCode) 생성
        String employeeCode = "EMP-" + generateNumberByUuidLast7(userId);

        // address 조합
        String address = requestDto.getBaseAddress() + " " + requestDto.getDetailAddress();

        // Auth 서버 전달용
        AuthUserCreateRequestDto authRequest = AuthUserCreateRequestDto.builder()
                .userId(userId)
                .userEmail(requestDto.getEmail())
                .departmentName(department.getDepartmentName())
                .positionName(position.getPositionName())
                .status("ACTIVE")
                .build();


        // 이 부분은 Kafka를 이용해서 처리해야함 -> 추후 처리
//        authResponse = userServicePort.createInternalUserAccount(authRequest).join();
//        if (authResponse == null || authResponse.getUserId() == null) {
//            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "사용자 계정 생성에 실패했습니다.");
//        }

        // InternalUser 저장
        InternelUser internalUser = InternelUser.createNewInternalUser(
                userId,
                requestDto.getName(),
                employeeCode,
                position,
                requestDto.getBirthDate(),
                requestDto.getHireDate(),
                address,
                requestDto.getEmail(),
                requestDto.getPhoneNumber(),
                UserStatus.ACTIVE
        );
        internalUserRepository.save(internalUser);

        // Employee 저장
        Employee employee = new Employee(
                internalUser,
                15L, // 초기 휴가 날짜 설정
                null
        );
        employeeRepository.save(employee);

        // 응답
        return EmployeeCreateResponseDto.builder()
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE.name())
                .build();
    }

    private String generateNumberByUuidLast7(String uuidId) {
        if (uuidId == null || uuidId.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] UUID가 null 이거나 비어있습니다.");
        }
        String compactUuid = uuidId.replaceAll("-", "");
        log.info("[INFO] Number로 사용될 생성된 uuid: {}", compactUuid);
        if (compactUuid.length() < 7) {
            throw new IllegalArgumentException("패딩된 uuid가 7자리 이하 이므로 Number를 생성할 수 없습니다. uuidId를 점검해주세요");
        }
        return compactUuid.substring(compactUuid.length() - 7);
    }
}
