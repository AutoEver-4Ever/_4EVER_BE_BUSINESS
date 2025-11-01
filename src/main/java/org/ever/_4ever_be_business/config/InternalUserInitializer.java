package org.ever._4ever_be_business.config;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.enums.Gender;
import org.ever._4ever_be_business.hr.enums.UserStatus;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.InternelUserRepository;
import org.ever._4ever_be_business.hr.repository.PositionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalUserInitializer implements CommandLineRunner {

    private final InternelUserRepository internelUserRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;

    private static final String POSITION_CODE_EMPLOYEE = "POS-00501"; // 인적자원-사원
    private static final String POSITION_CODE_MANAGER = "POS-00505";  // 인적자원-차장

    private static final List<SeedUser> INTERNAL_USERS = List.of(
        new SeedUser(
            "019a3ded-4e50-7e19-aa43-eb1820cd8649", // hrm-user@everp.com
            "hrm-user@everp.com",
            "HRM 사용자",
            "EMP-HRM-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "019a3ded-6488-795b-a1fc-31557a2b1aa5", // hrm-admin@everp.com
            "hrm-admin@everp.com",
            "HRM 관리자",
            "EMP-HRM-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "019a3dee-8f03-77a0-92f9-e34b09e467fe", // mm-user@everp.com
            "mm-user@everp.com",
            "MM 사용자",
            "EMP-MM-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "019a3dec-a3f3-781c-986b-8c0368cb1e73", // mm-admin@everp.com
            "mm-admin@everp.com",
            "MM 관리자",
            "EMP-MM-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "019a3dec-cf67-7ff7-9e94-b829bbd01152", // sd-user@everp.com
            "sd-user@everp.com",
            "SD 사용자",
            "EMP-SD-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "019a3e39-cbac-7773-a09b-5da7bb0ee3ec", // sd-admin@everp.com
            "sd-admin@everp.com",
            "SD 관리자",
            "EMP-SD-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "019a3dec-f1f1-7696-8195-54b87025022a", // im-user@everp.com
            "im-user@everp.com",
            "IM 사용자",
            "EMP-IM-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "019a3ded-1748-75ea-932c-1d8ad64f75f1", // im-admin@everp.com
            "im-admin@everp.com",
            "IM 관리자",
            "EMP-IM-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "019a3ded-2a80-7104-8552-dd26c30ed45c", // fcm-user@everp.com
            "fcm-user@everp.com",
            "FCM 사용자",
            "EMP-FCM-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "019a3ded-3c02-7294-b376-de49c32e0754", // fcm-admin@everp.com
            "fcm-admin@everp.com",
            "FCM 관리자",
            "EMP-FCM-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "019a3e3c-57e9-7e9a-b10f-0ee551498cae", // pp-user@everp.com
            "pp-user@everp.com",
            "PP 사용자",
            "EMP-PP-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "019a3df5-456d-7e0c-8212-388ca6118c18", // pp-admin@everp.com
            "pp-admin@everp.com",
            "PP 관리자",
            "EMP-PP-ADMIN-001",
            POSITION_CODE_MANAGER
        )
    );

    @Override
    @Transactional
    public void run(String... args) {
        log.info("[Initializer] 내부 사용자 기본 데이터 점검 시작");

        Position employeePosition = positionRepository.findByPositionCode(POSITION_CODE_EMPLOYEE)
            .orElseThrow(() -> new IllegalStateException("필수 직급을 찾을 수 없습니다: " + POSITION_CODE_EMPLOYEE));
        Position managerPosition = positionRepository.findByPositionCode(POSITION_CODE_MANAGER)
            .orElseThrow(() -> new IllegalStateException("필수 직급을 찾을 수 없습니다: " + POSITION_CODE_MANAGER));

        for (SeedUser seed : INTERNAL_USERS) {
            internelUserRepository.findByUserId(seed.userId()).ifPresentOrElse(
                existing -> log.debug("[Initializer] 내부 사용자 이미 존재: {}", existing.getUserId()),
                () -> createInternalUser(seed, employeePosition, managerPosition)
            );
        }

        log.info("[Initializer] 내부 사용자 기본 데이터 점검 완료");
    }

    private void createInternalUser(SeedUser seed, Position employeePosition, Position managerPosition) {
        Position position = seed.positionCode().equals(POSITION_CODE_MANAGER)
            ? managerPosition
            : employeePosition;

        InternelUser internelUser = new InternelUser(
            seed.userId(),
            seed.userId(),
            seed.displayName(),
            seed.employeeCode(),
            position,
            Gender.MALE,
            LocalDateTime.now().minusYears(30),
            LocalDateTime.now().minusYears(3),
            "서울특별시 강남구",
            seed.loginEmail(),
            "010-0000-0000",
            LocalDateTime.now().minusYears(3),
            "학사",
            "경력 3년",
            UserStatus.ACTIVE
        );
        InternelUser savedInternelUser = internelUserRepository.save(internelUser);

        Employee employee = new Employee(
            savedInternelUser,
            15L,
            LocalDateTime.now().minusMonths(6)
        );
        employeeRepository.save(employee);

        log.info("[Initializer] 내부 사용자 생성 - userId: {}, name: {}", seed.userId(), seed.displayName());
    }

    private record SeedUser(
        String userId,
        String loginEmail,
        String displayName,
        String employeeCode,
        String positionCode
    ) {
    }
}
