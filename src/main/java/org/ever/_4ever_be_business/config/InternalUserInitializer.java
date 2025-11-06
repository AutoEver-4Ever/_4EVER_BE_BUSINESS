package org.ever._4ever_be_business.config;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.enums.Gender;
import org.ever._4ever_be_business.hr.enums.UserStatus;
import org.ever._4ever_be_business.hr.repository.DepartmentRepository;
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
    private final DepartmentRepository departmentRepository;

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

        // 부서별 11명 생성: 각 직급 1명 + 사원 1명 추가
        seedDepartmentMockUsers();

        log.info("[Initializer] 내부 사용자 기본 데이터 점검 완료");
    }

    // 샘플 한국인 이름 목록
    private static final String[] KOREAN_NAMES = new String[] {
        "김민수", "이서연", "박지훈", "최예린", "정하준",
        "강다은", "윤도현", "임수빈", "장하늘", "오유진",
        "한서준", "서지우", "신민재", "문예원", "권하린"
    };

    private void seedDepartmentMockUsers() {
        try {
            List<org.ever._4ever_be_business.hr.entity.Department> departments = departmentRepository.findAll();
            for (int d = 0; d < departments.size(); d++) {
                var dept = departments.get(d);
                List<Position> positions = positionRepository.findByDepartmentId(dept.getId());
                if (positions == null || positions.isEmpty()) {
                    log.warn("[Initializer] 부서에 직급이 없습니다. departmentCode={}", dept.getDepartmentCode());
                    continue;
                }
                positions.sort(Comparator.comparing(Position::getPositionCode));

                String deptCode = dept.getDepartmentCode().toLowerCase();

                // 11명 생성
                for (int i = 0; i < 11; i++) {
                    int posIndex = (i < positions.size()) ? i : 0; // 0..9 각 1명, 10번째는 사원(0)
                    Position position = positions.get(posIndex);

                    String seq = String.format("%03d", i + 1);
                    String userId = "internal-" + deptCode + "-" + seq;
                    if (internelUserRepository.findByUserId(userId).isPresent()) {
                        continue; // 멱등 처리
                    }

                    String name = KOREAN_NAMES[(d * 11 + i) % KOREAN_NAMES.length];
                    String email = deptCode + "-user" + seq + "@everp.com";
                    String phone = String.format("010-%04d-%04d", 1000 + ((d * 11 + i) % 9000), i + 1);

                    String empCode = "EMP-" + trailing7(userId);

                    InternelUser internelUser = new InternelUser(
                        userId,               // id (mock용 고정)
                        userId,               // userId
                        name,
                        empCode,
                        position,
                        (i % 2 == 0) ? Gender.MALE : Gender.FEMALE,
                        LocalDateTime.now().minusYears(25 + (i % 10)),
                        LocalDateTime.now().minusYears(1 + (i % 3)),
                        "서울특별시",
                        email,
                        phone,
                        LocalDateTime.now().minusMonths(6 + i),
                        "학사",
                        "경력 " + (i % 6) + "년",
                        UserStatus.ACTIVE
                    );

                    InternelUser saved = internelUserRepository.save(internelUser);
                    Employee employee = new Employee(saved, 15L, LocalDateTime.now().minusMonths(6));
                    employeeRepository.save(employee);

                    log.info("[Initializer] 부서별 내부 사용자 생성 - dept: {}, position: {}, userId: {}",
                        dept.getDepartmentCode(), position.getPositionName(), userId);
                }
            }
        } catch (Exception e) {
            log.warn("[Initializer] 부서별 내부 사용자 시드 중 오류: {}", e.getMessage());
        }
    }

    private String trailing7(String s) {
        if (s == null) return "0000000";
        String compact = s.replaceAll("[^A-Za-z0-9]", "");
        int len = compact.length();
        return (len <= 7) ? compact : compact.substring(len - 7);
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
