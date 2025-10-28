package org.ever._4ever_be_business.fcm.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.mock.MockDataProvider;
import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompaniesResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompanyResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.SupplierCompanyServicePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SupplierCompanyServicePort의 Mock 구현체
 * dev 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockSupplierCompanyServiceAdapter implements SupplierCompanyServicePort {

    private final MockDataProvider mockDataProvider;

    @Override
    public SupplierCompanyResponseDto getSupplierCompanyById(String supplierCompanyId) {
        log.info("[MOCK ADAPTER] getSupplierCompanyById 호출 - supplierCompanyId: {}", supplierCompanyId);
        return mockDataProvider.createMockSupplierCompany(supplierCompanyId);
    }

    @Override
    public SupplierCompaniesResponseDto getSupplierCompaniesByIds(List<String> supplierCompanyIds) {
        log.info("[MOCK ADAPTER] getSupplierCompaniesByIds 호출 - supplierCompanyIds: {}", supplierCompanyIds);
        return mockDataProvider.createMockSupplierCompanies(supplierCompanyIds);
    }
}
