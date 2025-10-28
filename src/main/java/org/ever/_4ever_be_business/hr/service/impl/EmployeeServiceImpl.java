package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dao.EmployeeDAO;
import org.ever._4ever_be_business.hr.dto.request.TrainingRequestDto;
import org.ever._4ever_be_business.hr.dto.request.UpdateEmployeeRequestDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeDetailDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.EmployeeTraining;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeTrainingRepository;
import org.ever._4ever_be_business.hr.repository.PositionRepository;
import org.ever._4ever_be_business.hr.repository.TrainingRepository;
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
    private final EmployeeTrainingRepository employeeTrainingRepository;

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
}
