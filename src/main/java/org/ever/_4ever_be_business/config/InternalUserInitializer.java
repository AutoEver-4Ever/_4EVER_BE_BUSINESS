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
            "1f0b4b1b-43f0-6735-befc-ff061e0fbaad", // hrm-user@everp.com
            "hrm-user@everp.com",
            "HRM 사용자",
            "EMP-HRM-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "1f0b4b1b-44bb-6166-befc-ff061e0fbaad", // hrm-admin@everp.com
            "hrm-admin@everp.com",
            "HRM 관리자",
            "EMP-HRM-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "1f0b4b1b-3d63-633d-befc-ff061e0fbaad", // mm-user@everp.com
            "mm-user@everp.com",
            "MM 사용자",
            "EMP-MM-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "1f0b4b1b-3e61-61be-befc-ff061e0fbaad", // mm-admin@everp.com
            "mm-admin@everp.com",
            "MM 관리자",
            "EMP-MM-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "1f0b4b1b-3f52-6cef-befc-ff061e0fbaad", // sd-user@everp.com
            "sd-user@everp.com",
            "SD 사용자",
            "EMP-SD-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "1f0b4b1b-401b-6010-befc-ff061e0fbaad", // sd-admin@everp.com
            "sd-admin@everp.com",
            "SD 관리자",
            "EMP-SD-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "1f0b4b1b-40e3-6331-befc-ff061e0fbaad", // im-user@everp.com
            "im-user@everp.com",
            "IM 사용자",
            "EMP-IM-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "1f0b4b1b-41a6-6832-befc-ff061e0fbaad", // im-admin@everp.com
            "im-admin@everp.com",
            "IM 관리자",
            "EMP-IM-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "1f0b4b1b-4267-6623-befc-ff061e0fbaad", // fcm-user@everp.com
            "fcm-user@everp.com",
            "FCM 사용자",
            "EMP-FCM-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "1f0b4b1b-432d-6234-befc-ff061e0fbaad", // fcm-admin@everp.com
            "fcm-admin@everp.com",
            "FCM 관리자",
            "EMP-FCM-ADMIN-001",
            POSITION_CODE_MANAGER
        ),
        new SeedUser(
            "1f0b4b1b-4580-6d77-befc-ff061e0fbaad", // pp-user@everp.com
            "pp-user@everp.com",
            "PP 사용자",
            "EMP-PP-001",
            POSITION_CODE_EMPLOYEE
        ),
        new SeedUser(
            "1f0b4b1b-4644-6278-befc-ff061e0fbaad", // pp-admin@everp.com
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
