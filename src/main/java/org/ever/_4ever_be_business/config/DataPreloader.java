package org.ever._4ever_be_business.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.company.repository.CustomerCompanyRepository;
import org.ever._4ever_be_business.hr.entity.*;
import org.ever._4ever_be_business.hr.enums.Gender;
import org.ever._4ever_be_business.hr.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 애플리케이션 시작 시 필요한 초기 데이터를 생성하는 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataPreloader {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final CustomerCompanyRepository customerCompanyRepository;
    private final InternelUserRepository internelUserRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerUserRepository customerUserRepository;
    private final TrainingRepository trainingRepository;

    @PostConstruct
    @Transactional
    public void preloadData() {
        log.info("========================================");
        log.info("초기 데이터 로딩 시작");
        log.info("========================================");

        loadDepartments();
        loadPositions();
        loadCustomerCompanies();
        loadInternelUsers();
        loadCustomerUsers();
        loadTrainings();

        log.info("========================================");
        log.info("초기 데이터 로딩 완료");
        log.info("========================================");
    }

    /**
     * 부서 데이터 생성
     */
    private void loadDepartments() {
        if (departmentRepository.count() > 0) {
            log.info("부서 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("부서 데이터 생성 중...");

        Department[] departments = {
                new Department("DEPT-001", "구매", "구매 및 조달 업무", LocalDateTime.now()),
                new Department("DEPT-002", "영업", "영업 및 고객 관리 업무", LocalDateTime.now()),
                new Department("DEPT-003", "재고", "재고 관리 및 물류 업무", LocalDateTime.now()),
                new Department("DEPT-004", "재무", "재무 및 회계 업무", LocalDateTime.now()),
                new Department("DEPT-005", "인적자원", "인사 및 조직 관리 업무", LocalDateTime.now()),
                new Department("DEPT-006", "생산", "생산 및 제조 업무", LocalDateTime.now())
        };

        for (Department dept : departments) {
            departmentRepository.save(dept);
            log.info("부서 생성: {} ({})", dept.getDepartmentName(), dept.getDepartmentCode());
        }

        log.info("총 {}개의 부서 생성 완료", departments.length);
    }

    /**
     * 직급 데이터 생성
     */
    private void loadPositions() {
        if (positionRepository.count() > 0) {
            log.info("직급 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("직급 데이터 생성 중...");

        // 부서 조회 (직급에 부서를 할당하기 위해)
        Department dept1 = departmentRepository.findByDepartmentCode("DEPT-001")
                .orElseThrow(() -> new RuntimeException("Department not found"));
        Department dept2 = departmentRepository.findByDepartmentCode("DEPT-002")
                .orElseThrow(() -> new RuntimeException("Department not found"));
        Department dept3 = departmentRepository.findByDepartmentCode("DEPT-003")
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // 직급별 연봉 (만원 단위) - 각 직급을 부서에 할당
        Position[] positions = {
                new Position("POS-001", "사원", dept1, false, new BigDecimal("3500")),      // 구매 부서
                new Position("POS-002", "주임", dept1, false, new BigDecimal("4000")),      // 구매 부서
                new Position("POS-003", "대리", dept2, false, new BigDecimal("4800")),      // 영업 부서
                new Position("POS-004", "과장", dept2, true, new BigDecimal("5800")),       // 영업 부서
                new Position("POS-005", "차장", dept3, true, new BigDecimal("7000")),       // 재고 부서
                new Position("POS-006", "부장", dept3, true, new BigDecimal("8500")),       // 재고 부서
                new Position("POS-007", "이사", dept1, true, new BigDecimal("10500")),      // 구매 부서
                new Position("POS-008", "상무", dept2, true, new BigDecimal("13000")),      // 영업 부서
                new Position("POS-009", "전무", dept3, true, new BigDecimal("16000")),      // 재고 부서
                new Position("POS-010", "사장", dept1, true, new BigDecimal("20000"))       // 구매 부서
        };

        for (Position pos : positions) {
            positionRepository.save(pos);
            log.info("직급 생성: {} ({}) - 부서: {}, 연봉: {}만원, 관리직: {}",
                    pos.getPositionName(),
                    pos.getPositionCode(),
                    pos.getDepartment().getDepartmentName(),
                    pos.getSalary(),
                    pos.getIsManager() ? "Y" : "N");
        }

        log.info("총 {}개의 직급 생성 완료", positions.length);
    }

    /**
     * 고객사 데이터 생성
     */
    private void loadCustomerCompanies() {
        if (customerCompanyRepository.count() > 0) {
            log.info("고객사 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("고객사 데이터 생성 중...");

        CustomerCompany[] companies = {
                new CustomerCompany(
                        null,                           // customerUserId (nullable)
                        "CUST-001",                     // companyCode
                        "현대자동차",                    // companyName
                        "123-81-12345",                 // businessNumber
                        "장재훈",                        // ceoName
                        "06797",                        // zipCode
                        "서울특별시 서초구 헌릉로 12",    // baseAddress
                        "현대자동차 본사",               // detailAddress
                        "02-3464-1114",                 // officePhone
                        "contact@hyundai.com",          // officeEmail
                        "대한민국 대표 완성차 제조업체"   // etc
                ),
                new CustomerCompany(
                        null,
                        "CUST-002",
                        "삼성전자",
                        "124-81-00998",
                        "한종희",
                        "06765",
                        "서울특별시 서초구 서초대로74길 11",
                        "삼성전자 본관",
                        "02-2053-3000",
                        "info@samsung.com",
                        "글로벌 전자제품 제조업체"
                ),
                new CustomerCompany(
                        null,
                        "CUST-003",
                        "LG화학",
                        "116-81-03698",
                        "신학철",
                        "07795",
                        "서울특별시 강서구 마곡중앙10로 10",
                        "LG사이언스파크",
                        "02-3773-1114",
                        "webmaster@lgchem.com",
                        "배터리 및 화학 소재 전문업체"
                ),
                new CustomerCompany(
                        null,
                        "CUST-004",
                        "SK하이닉스",
                        "120-81-02521",
                        "곽노정",
                        "13558",
                        "경기도 성남시 분당구 대왕판교로 645번길 86",
                        "SK하이닉스 본사",
                        "031-5185-4114",
                        "contact@skhynix.com",
                        "반도체 메모리 제조업체"
                ),
                new CustomerCompany(
                        null,
                        "CUST-005",
                        "포스코",
                        "220-81-02382",
                        "장인화",
                        "06194",
                        "서울특별시 강남구 테헤란로 440",
                        "포스코센터",
                        "02-3457-0114",
                        "webmaster@posco.com",
                        "철강 제조 및 유통업체"
                )
        };

        for (CustomerCompany company : companies) {
            customerCompanyRepository.save(company);
            log.info("고객사 생성: {} ({})", company.getCompanyName(), company.getCompanyCode());
        }

        log.info("총 {}개의 고객사 생성 완료", companies.length);
    }

    /**
     * 내부 직원 데이터 생성
     */
    private void loadInternelUsers() {
        if (internelUserRepository.count() > 0) {
            log.info("내부 직원 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("내부 직원 데이터 생성 중...");

        // Position 조회 (사원, 대리, 과장 사용)
        Position position1 = positionRepository.findByPositionCode("POS-001")
                .orElseThrow(() -> new RuntimeException("Position not found"));
        Position position2 = positionRepository.findByPositionCode("POS-003")
                .orElseThrow(() -> new RuntimeException("Position not found"));
        Position position3 = positionRepository.findByPositionCode("POS-004")
                .orElseThrow(() -> new RuntimeException("Position not found"));

        InternelUser[] internelUsers = {
                new InternelUser(
                        "internel1",                        // id
                        1001L,                              // userId
                        "internel1",                        // name
                        "EMP-001",                          // employeeCode
                        position1,                          // position (사원)
                        Gender.MALE,                        // gender
                        LocalDateTime.of(1995, 3, 15, 0, 0),  // birthDate
                        LocalDateTime.of(2023, 1, 1, 0, 0),   // hireDate
                        "서울특별시 강남구",                  // address
                        "internel1@gmail.com",              // email
                        "010-1111-1111",                    // phoneNumber
                        LocalDateTime.of(2023, 1, 1, 0, 0),   // departmentStartAt
                        "학사",                              // education
                        "신입"                               // career
                ),
                new InternelUser(
                        "internel2",
                        1002L,
                        "internel2",
                        "EMP-002",
                        position2,                          // position (대리)
                        Gender.FEMALE,
                        LocalDateTime.of(1992, 7, 22, 0, 0),
                        LocalDateTime.of(2021, 3, 1, 0, 0),
                        "서울특별시 서초구",
                        "internel2@gmail.com",
                        "010-2222-2222",
                        LocalDateTime.of(2021, 3, 1, 0, 0),
                        "석사",
                        "경력 2년"
                ),
                new InternelUser(
                        "internel3",
                        1003L,
                        "internel3",
                        "EMP-003",
                        position3,                          // position (과장)
                        Gender.MALE,
                        LocalDateTime.of(1988, 11, 5, 0, 0),
                        LocalDateTime.of(2018, 6, 1, 0, 0),
                        "서울특별시 송파구",
                        "internel3@gmail.com",
                        "010-3333-3333",
                        LocalDateTime.of(2018, 6, 1, 0, 0),
                        "학사",
                        "경력 5년"
                )
        };

        for (InternelUser user : internelUsers) {
            internelUserRepository.save(user);
            log.info("내부 직원 생성: {} ({})", user.getName(), user.getEmployeeCode());

            // Employee 엔티티 생성
            Employee employee = new Employee(
                    user,
                    15L,  // 연차 15일
                    LocalDateTime.now().minusMonths(6)  // 6개월 전 교육 수료
            );
            employeeRepository.save(employee);
            log.info("Employee 엔티티 생성: {}", user.getName());
        }

        log.info("총 {}개의 내부 직원 생성 완료", internelUsers.length);
    }

    /**
     * 고객사 담당자 데이터 생성
     */
    private void loadCustomerUsers() {
        if (customerUserRepository.count() > 0) {
            log.info("고객사 담당자 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("고객사 담당자 데이터 생성 중...");

        // 기존 고객사 5개 조회
        CustomerCompany company1 = customerCompanyRepository.findByCompanyCode("CUST-001")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company2 = customerCompanyRepository.findByCompanyCode("CUST-002")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company3 = customerCompanyRepository.findByCompanyCode("CUST-003")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company4 = customerCompanyRepository.findByCompanyCode("CUST-004")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company5 = customerCompanyRepository.findByCompanyCode("CUST-005")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));

        CustomerUser[] customerUsers = {
                new CustomerUser(
                        "customer1",                    // id
                        2001L,                          // userId
                        101L,                           // customerId
                        "customer1",                    // customerName
                        company1,                       // customerCompany (현대자동차)
                        "CUST-USER-001",                // customerUserCode
                        "customer1@gmail.com",          // email
                        "010-1001-1001"                 // phoneNumber
                ),
                new CustomerUser(
                        "customer2",
                        2002L,
                        102L,
                        "customer2",
                        company2,                       // customerCompany (삼성전자)
                        "CUST-USER-002",
                        "customer2@gmail.com",
                        "010-2002-2002"
                ),
                new CustomerUser(
                        "customer3",
                        2003L,
                        103L,
                        "customer3",
                        company3,                       // customerCompany (LG화학)
                        "CUST-USER-003",
                        "customer3@gmail.com",
                        "010-3003-3003"
                ),
                new CustomerUser(
                        "customer4",
                        2004L,
                        104L,
                        "customer4",
                        company4,                       // customerCompany (SK하이닉스)
                        "CUST-USER-004",
                        "customer4@gmail.com",
                        "010-4004-4004"
                ),
                new CustomerUser(
                        "customer5",
                        2005L,
                        105L,
                        "customer5",
                        company5,                       // customerCompany (포스코)
                        "CUST-USER-005",
                        "customer5@gmail.com",
                        "010-5005-5005"
                )
        };

        for (CustomerUser user : customerUsers) {
            customerUserRepository.save(user);
            log.info("고객사 담당자 생성: {} ({})", user.getCustomerName(), user.getCustomerUserCode());
        }

        log.info("총 {}개의 고객사 담당자 생성 완료", customerUsers.length);
    }

    /**
     * 교육 프로그램 데이터 생성
     */
    private void loadTrainings() {
        if (trainingRepository.count() > 0) {
            log.info("교육 프로그램 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("교육 프로그램 데이터 생성 중...");

        Training[] trainings = {
                new Training(
                        "Spring Boot 심화 과정",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.TECHNICAL,
                        40L,
                        "온라인",
                        15L,
                        30,
                        "Spring Boot 프레임워크를 활용한 백엔드 개발 심화",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.RECRUITING
                ),
                new Training(
                        "리더십 역량 강화",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.LEADERSHIP,
                        24L,
                        "오프라인",
                        20L,
                        25,
                        "중간 관리자를 위한 리더십 및 팀 관리 스킬",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.IN_PROGRESS
                ),
                new Training(
                        "개인정보보호법 준수 교육",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.COMPLIANCE,
                        4L,
                        "온라인",
                        45L,
                        50,
                        "개인정보보호법 및 정보보안 의무 교육",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.IN_PROGRESS
                ),
                new Training(
                        "비즈니스 영어 회화",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.LANGUAGE,
                        60L,
                        "오프라인",
                        12L,
                        20,
                        "업무 상황에서 활용 가능한 실전 영어 회화",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.RECRUITING
                ),
                new Training(
                        "데이터 분석 기초",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.COURSE,
                        32L,
                        "온라인",
                        25L,
                        40,
                        "Python 기반 데이터 분석 및 시각화 기초 과정",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.RECRUITING
                ),
                new Training(
                        "산업안전보건교육",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.SAFETY,
                        8L,
                        "오프라인",
                        30L,
                        50,
                        "산업안전보건법에 따른 필수 안전 교육",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.COMPLETED
                )
        };

        for (Training training : trainings) {
            trainingRepository.save(training);
            log.info("교육 프로그램 생성: {} ({}) - {}시간, 신청: {}/{}, 상태: {}",
                    training.getTrainingName(),
                    training.getCategory(),
                    training.getDurationHours(),
                    training.getEnrolled(),
                    training.getCapacity(),
                    training.getTrainingStatus());
        }

        log.info("총 {}개의 교육 프로그램 생성 완료", trainings.length);
    }
}
