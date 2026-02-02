package com.insurance.assignment;

import com.insurance.assignment.common.CONSTANTS;
import com.insurance.assignment.common.config.I18N;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.enumvalue.ClaimType;
import com.insurance.assignment.common.exception.customexception.RecordNotFoundException;
import com.insurance.assignment.common.object.DataTable;
import com.insurance.assignment.controller.ClaimController;
import com.insurance.assignment.model.dto.ClaimResponse;
import com.insurance.assignment.service.ClaimService;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.eq;
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

    @Autowired
    MessageSource messageSource;

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

    @Test
    void listClaims_success() throws Exception {
        ClaimResponse response = ClaimResponse.builder()
                .claimId(1L)
                .policyId(10L)
                .claimDate(LocalDate.of(2025, 1, 29))
                .claimAmount(BigDecimal.valueOf(5_000_000))
                .claimStatus(ClaimStatus.SUBMITTED)
                .claimType(ClaimType.HOSPITALIZATION)
                .description("Emergency surgery")
                .createdAt(Instant.parse("2025-01-29T10:30:00Z"))
                .claimNumber("CLM-001")
                .claimStatus(ClaimStatus.SUBMITTED)
                .build();

        DataTable dataTable = new DataTable(List.of(response), 1L, 10, 0);

        Mockito.when(claimService.getListClaims(
                eq(10L),
                eq(ClaimStatus.SUBMITTED),
                eq(0),
                eq(10)
        )).thenReturn(dataTable);

        mockMvc.perform(get("/api/claims")
                        .param("policyId", "10")
                        .param("status", "SUBMITTED")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.message").value(I18N.getMessage("action.success")))
                .andExpect(jsonPath("$.dataTable.total").value(1))
                .andExpect(jsonPath("$.dataTable.limit").value(10))
                .andExpect(jsonPath("$.dataTable.offset").value(0))
                .andExpect(jsonPath("$.dataTable.content").isArray())
                .andExpect(jsonPath("$.dataTable.content.length()").value(1))
                .andExpect(jsonPath("$.dataTable.content[0].claimId").value(1))
                .andExpect(jsonPath("$.dataTable.content[0].policyId").value(10))
                .andExpect(jsonPath("$.dataTable.content[0].claimNumber").value("CLM-001"))
                .andExpect(jsonPath("$.dataTable.content[0].claimStatus").value("SUBMITTED"));
    }

    @Test
    void listClaims_validateLimitOffset() throws Exception {
        String limitNegativeMessage = messageSource.getMessage("error.claim.limit.not.negative",null,Locale.getDefault());
        String offsetNegativeMessage = messageSource.getMessage("error.claim.offset.not.negative",null, Locale.getDefault());
        mockMvc.perform(get("/api/claims")
                .param("policyId", "10")
                .param("status", "SUBMITTED")
                .param("limit", "0")
                .param("offset", "-10"))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem(limitNegativeMessage)))
                .andExpect(jsonPath("$.messages", hasItem(offsetNegativeMessage)));
    }
}