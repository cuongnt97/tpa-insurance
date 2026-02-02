package com.insurance.assignment;

import com.insurance.assignment.common.CONSTANTS;
import com.insurance.assignment.common.config.I18N;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.enumvalue.ClaimType;
import com.insurance.assignment.common.exception.customexception.RecordNotFoundException;
import com.insurance.assignment.controller.ClaimController;
import com.insurance.assignment.model.dto.ClaimResponse;
import com.insurance.assignment.service.ClaimService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ClaimController.class)
@Import(ClaimControllerTest.TestConfig.class)
class ClaimControllerTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        ClaimService claimService() {
            return Mockito.mock(ClaimService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimService claimService;

    @Test
    void getClaimById_success() throws Exception {

        ClaimResponse response = ClaimResponse.builder()
                .claimId(123L)
                .policyId(1L)
                .claimNumber("CLM-2025-000123")
                .claimDate(LocalDate.of(2025, 1, 29))
                .claimAmount(BigDecimal.valueOf(5_000_000))
                .claimStatus(ClaimStatus.SUBMITTED)
                .claimType(ClaimType.HOSPITALIZATION)
                .description("Emergency surgery")
                .createdAt(Instant.parse("2025-01-29T10:30:00Z"))
                .build();

        Mockito.when(claimService.getClaimById(123L))
                .thenReturn(response);

        mockMvc.perform(get("/api/claims/{id}", 123))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(CONSTANTS.HTTP_RESPONSE.STATUS_SUCCESS))
                .andExpect(jsonPath("$.message").value(I18N.getMessage("action.success")))
                .andExpect(jsonPath("$.data.claimId").value(123))
                .andExpect(jsonPath("$.data.policyId").value(1))
                .andExpect(jsonPath("$.data.claimNumber").value("CLM-2025-000123"))
                .andExpect(jsonPath("$.data.claimStatus").value("SUBMITTED"))
                .andExpect(jsonPath("$.data.claimType").value("HOSPITALIZATION"));
    }

    @Test
    void getClaimById_notFound() throws Exception {

        Long claimId = 123L;
        Mockito.when(claimService.getClaimById(claimId))
                .thenThrow(new RecordNotFoundException(I18N.getMessage("error.claim.notfound", claimId)));

        mockMvc.perform(get("/api/claims/{id}", claimId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL))
                .andExpect(jsonPath("$.message")
                        .value(I18N.getMessage("error.claim.notfound", claimId)));
    }
}