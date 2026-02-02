package com.insurance.assignment;

import com.insurance.assignment.common.CONSTANTS;
import com.insurance.assignment.common.config.I18N;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.enumvalue.ClaimType;
import com.insurance.assignment.common.exception.customexception.RecordNotFoundException;
import com.insurance.assignment.common.object.DataTable;
import com.insurance.assignment.controller.ClaimController;
import com.insurance.assignment.model.dto.ClaimResponse;
import com.insurance.assignment.model.dto.UpdateClaimRequest;
import com.insurance.assignment.service.ClaimService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

        when(claimService.getClaimById(123L))
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
        when(claimService.getClaimById(claimId))
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

        when(claimService.getListClaims(
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

    @Test
    void updateStatus_success() throws Exception {
        Long claimId = 1L;

        ClaimResponse response = ClaimResponse.builder()
                .claimId(claimId)
                .claimStatus(ClaimStatus.APPROVED)
                .approvedAmount(BigDecimal.valueOf(4_500_000))
                .build();
        UpdateClaimRequest req = new UpdateClaimRequest();
        req.setNewStatus(ClaimStatus.APPROVED);
        req.setApprovedAmount(BigDecimal.valueOf(4_500_000));
        req.setNote("unit test controller");

        when(claimService.updateStatus(eq(claimId), any(UpdateClaimRequest.class)))
                .thenReturn(response);

        String body = """
        {
          "newStatus": "APPROVED",
          "approvedAmount": 4500000,
          "note": "unit test controller"
        }
        """;

        mockMvc.perform(patch("/api/claims/{claimId}", claimId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(CONSTANTS.HTTP_RESPONSE.STATUS_SUCCESS))
                .andExpect(jsonPath("$.data.claimId").value(1))
                .andExpect(jsonPath("$.data.claimStatus").value("APPROVED"))
                .andExpect(jsonPath("$.data.approvedAmount").value(4500000));

        verify(claimService)
                .updateStatus(eq(claimId), any(UpdateClaimRequest.class));
    }

    @Test
    void updateStatus_validationError() throws Exception {
        String body = """
        {
          "newStatus": "APPROVED",
          "note": "Approved by underwriter"
        }
        """;

        mockMvc.perform(patch("/api/claims/{claimId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages").isNotEmpty());

        verify(claimService, never()).updateStatus(any(), any());
    }

    @Test
    void updateStatus_claimNotFound() throws Exception {
        String claimNotFound = messageSource.getMessage("error.claim.notfound",new Object[]{1L}, Locale.getDefault());
        when(claimService.updateStatus(eq(1L), any()))
                .thenThrow(new RecordNotFoundException(claimNotFound));

        String body = """
        {
          "newStatus": "APPROVED",
          "approvedAmount": 4500000
        }
        """;

        mockMvc.perform(patch("/api/claims/{claimId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL))
                .andExpect(jsonPath("$.message").value(claimNotFound));
    }
}