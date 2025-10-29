package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.async.AsyncResultManager;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.saga.SagaTransactionManager;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.dao.EmployeeDAO;
import org.ever._4ever_be_business.hr.dto.request.EmployeeCreateRequestDto;
import org.ever._4ever_be_business.hr.dto.request.TrainingRequestDto;
import org.ever._4ever_be_business.hr.dto.request.UpdateEmployeeRequestDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeDetailDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.entity.*;
import org.ever._4ever_be_business.hr.enums.UserStatus;
import org.ever._4ever_be_business.hr.integration.port.UserServicePort;
import org.ever._4ever_be_business.hr.repository.*;
import org.ever._4ever_be_business.hr.service.EmployeeService;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.ever.event.CreateAuthUserEvent;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

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
    private final AsyncResultManager<CreateAuthUserResultEvent> asyncResultManager;
    private final SagaTransactionManager sagaManager;
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
    public void createEmployee(
            EmployeeCreateRequestDto requestDto,
            DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> deferredResult
    ) {
        // 트랜잭션 id 생성
        String transactionId = UuidV7Generator.generate();

        asyncResultManager.registerResult(transactionId, deferredResult);
        log.info("[INFO] 내부 사용자 등록 시작 - name: {}, email: {}", requestDto.getName(), requestDto.getEmail());

        sagaManager.executeSagaWithId(transactionId, () -> {
            try {
                Position position = positionRepository.findById(requestDto.getPositionId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "[ERROR] 직급 정보를 찾을 수 없습니다."));
                Department department = position.getDepartment();

                String userId = UuidV7Generator.generate();
                String employeeCode = "EMP-" + generateNumberByUuidLast7(userId);
                String address = requestDto.getBaseAddress() + " " + requestDto.getDetailAddress();

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

                Employee employee = new Employee(internalUser, 15L, null);
                employeeRepository.save(employee);

                CreateAuthUserEvent event = CreateAuthUserEvent.builder()
                    .eventId(UuidV7Generator.generate())
                    .transactionId(transactionId)
                    .success(true)      // 여기는 성공한 경우임.
                    .userId(userId)
                    .email(requestDto.getEmail())
                    .departmentCode(department.getDepartmentCode())
                    .positionCode(position.getPositionCode())
                    .build();

                userServicePort.createAuthUserPort(event)
                    .exceptionally(error -> {
                        asyncResultManager.setErrorResult(
                            transactionId,
                            "[SAGA][FAIL] 사용자 계정 생성 요청 발행 실패: " + error.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                        );
                        return null;
                    });
                return null;
            } catch (Exception error) {
                asyncResultManager.setErrorResult(
                    transactionId,
                    "[SAGA][FAIL] 내부 사용자 생성 처리에 실패했습니다.: " + error.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
                );
                throw error;
            }
        });
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
