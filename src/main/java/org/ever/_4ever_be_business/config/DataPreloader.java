package org.ever._4ever_be_business.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.company.repository.CustomerCompanyRepository;
import org.ever._4ever_be_business.hr.entity.Department;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.repository.DepartmentRepository;
import org.ever._4ever_be_business.hr.repository.PositionRepository;
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

    @PostConstruct
    @Transactional
    public void preloadData() {
        log.info("========================================");
        log.info("초기 데이터 로딩 시작");
        log.info("========================================");

        loadDepartments();
        loadPositions();
        loadCustomerCompanies();

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
}
