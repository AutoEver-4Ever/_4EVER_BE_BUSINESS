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

        // 직급별 연봉 (만원 단위)
        Position[] positions = {
                new Position("POS-001", "사원", false, new BigDecimal("3500")),      // 3,500만원
                new Position("POS-002", "주임", false, new BigDecimal("4000")),      // 4,000만원
                new Position("POS-003", "대리", false, new BigDecimal("4800")),      // 4,800만원
                new Position("POS-004", "과장", true, new BigDecimal("5800")),       // 5,800만원
                new Position("POS-005", "차장", true, new BigDecimal("7000")),       // 7,000만원
                new Position("POS-006", "부장", true, new BigDecimal("8500")),       // 8,500만원
                new Position("POS-007", "이사", true, new BigDecimal("10500")),      // 1억 500만원
                new Position("POS-008", "상무", true, new BigDecimal("13000")),      // 1억 3,000만원
                new Position("POS-009", "전무", true, new BigDecimal("16000")),      // 1억 6,000만원
                new Position("POS-010", "사장", true, new BigDecimal("20000"))       // 2억원
        };

        for (Position pos : positions) {
            positionRepository.save(pos);
            log.info("직급 생성: {} ({}) - 연봉: {}만원, 관리직: {}",
                    pos.getPositionName(),
                    pos.getPositionCode(),
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

        // 기존 고객사 3개 조회
        CustomerCompany company1 = customerCompanyRepository.findByCompanyCode("CUST-001")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company2 = customerCompanyRepository.findByCompanyCode("CUST-002")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company3 = customerCompanyRepository.findByCompanyCode("CUST-003")
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
                )
        };

        for (CustomerUser user : customerUsers) {
            customerUserRepository.save(user);
            log.info("고객사 담당자 생성: {} ({})", user.getCustomerName(), user.getCustomerUserCode());
        }

        log.info("총 {}개의 고객사 담당자 생성 완료", customerUsers.length);
    }
}
