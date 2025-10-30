package org.ever._4ever_be_business.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.company.repository.CustomerCompanyRepository;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerUserInitializer implements CommandLineRunner {

    private final CustomerUserRepository customerUserRepository;
    private final CustomerCompanyRepository customerCompanyRepository;

    private static final List<SeedCustomerUser> CUSTOMER_USERS = List.of(
        new SeedCustomerUser(
            "019a357e-bd3f-90d0-ab33-eba47eedb4f8", // customer-user@everp.com
            "customer-user@everp.com",
            "고객 사용자",
            "CUST-USER-001",
            "CUST-001"
        ),
        new SeedCustomerUser(
            "019a357e-bd3f-6f9b-5b64-20e2abea5672", // customer-admin@everp.com
            "customer-admin@everp.com",
            "고객 관리자",
            "CUST-USER-ADMIN-001",
            "CUST-002"
        )
    );

    @Override
    @Transactional
    public void run(String... args) {
        log.info("[Initializer] 고객사 사용자 기본 데이터 점검 시작");

        for (SeedCustomerUser seed : CUSTOMER_USERS) {
            customerUserRepository.findByUserId(seed.userId()).ifPresentOrElse(
                existing -> log.debug("[Initializer] 고객사 사용자 이미 존재: {}", existing.getUserId()),
                () -> createCustomerUser(seed)
            );
        }

        log.info("[Initializer] 고객사 사용자 기본 데이터 점검 완료");
    }

    private void createCustomerUser(SeedCustomerUser seed) {
        CustomerCompany company = customerCompanyRepository.findByCompanyCode(seed.companyCode())
            .orElseThrow(() -> new IllegalStateException("필수 고객사 정보를 찾을 수 없습니다: " + seed.companyCode()));

        CustomerUser customerUser = new CustomerUser(
            seed.userId(),
            seed.userId(),
            seed.displayName(),
            company,
            seed.customerUserCode(),
            seed.loginEmail(),
            "010-9000-0000"
        );
        customerUserRepository.save(customerUser);

        log.info("[Initializer] 고객사 사용자 생성 - userId: {}, name: {}", seed.userId(), seed.displayName());
    }

    private record SeedCustomerUser(
        String userId,
        String loginEmail,
        String displayName,
        String customerUserCode,
        String companyCode
    ) {
    }
}
