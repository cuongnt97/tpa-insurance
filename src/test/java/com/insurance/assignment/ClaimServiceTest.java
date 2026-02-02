package com.insurance.assignment;

import com.insurance.assignment.common.CONSTANTS;
import com.insurance.assignment.common.config.I18N;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.enumvalue.ClaimType;
import com.insurance.assignment.common.exception.customexception.BusinessException;
import com.insurance.assignment.common.exception.customexception.InactiveException;
import com.insurance.assignment.common.exception.customexception.RecordNotFoundException;
import com.insurance.assignment.common.object.DataTable;
import com.insurance.assignment.model.dto.ClaimResponse;
import com.insurance.assignment.model.dto.CreateClaimRequest;
import com.insurance.assignment.model.dto.UpdateClaimRequest;
import com.insurance.assignment.model.entity.Claim;
import com.insurance.assignment.model.entity.Policy;
import com.insurance.assignment.repository.ClaimRepository;
import com.insurance.assignment.service.ClaimService;
import com.insurance.assignment.service.PolicyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private ClaimService claimService;

    @Test
    void createClaimSuccess() {
        CreateClaimRequest req = new CreateClaimRequest();
        req.setPolicyId(2L);
        req.setClaimAmount(BigDecimal.valueOf(123534));
        req.setClaimType(ClaimType.HOSPITALIZATION);

        Policy policy = new Policy();
        policy.setId(2L);
        policy.setStatus(CONSTANTS.POLICY_STATUS.ACTIVE);

        when(policyService.findById(2L)).thenReturn(policy);
        when(claimRepository.save(any(Claim.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ClaimResponse result = claimService.createClaim(req);

        assertNotNull(result);
        assertEquals(ClaimStatus.SUBMITTED, result.getClaimStatus());
        verify(claimRepository).save(any(Claim.class));
    }

    @Test
    void createClaimPolicyNotFound_throwException() {
        when(policyService.findById(200L)).thenReturn(null);

        CreateClaimRequest req = new CreateClaimRequest();
        req.setPolicyId(200L);
        req.setClaimAmount(BigDecimal.TEN);
        req.setClaimType(ClaimType.ACCIDENT);

        assertThrows(RecordNotFoundException.class,
                () -> claimService.createClaim(req));
    }

    @Test
    void createClaim_policyInactive_throwBusinessException() {
        Policy policy = new Policy();
        policy.setStatus(CONSTANTS.POLICY_STATUS.INACTIVE);

        when(policyService.findById(1L)).thenReturn(policy);

        CreateClaimRequest req = new CreateClaimRequest();
        req.setPolicyId(1L);
        req.setClaimAmount(BigDecimal.TEN);
        req.setClaimType(ClaimType.DENTAL);

        assertThrows(InactiveException.class,
                () -> claimService.createClaim(req));
    }

    @Test
    void getClaimById_success() {
        Long claimId = 123L;

        Claim entity = Claim.builder()
                .id(claimId)
                .policyId(1L)
                .claimNumber("CLM-2025-000123")
                .claimDate(LocalDate.of(2025, 1, 29))
                .claimAmount(BigDecimal.valueOf(50000))
                .approvedAmount(null)
                .claimStatus(ClaimStatus.SUBMITTED)
                .claimType(ClaimType.HOSPITALIZATION)
                .description("Emergency surgery")
                .createdAt(Instant.parse("2025-01-29T10:30:00Z"))
                .build();

        Mockito.when(claimRepository.findById(claimId))
                .thenReturn(Optional.of(entity));

        ClaimResponse response = claimService.getClaimById(claimId);

        assertThat(response).isNotNull();
        assertThat(response.getClaimId()).isEqualTo(claimId);
        assertThat(response.getPolicyId()).isEqualTo(1L);
        assertThat(response.getClaimNumber()).isEqualTo("CLM-2025-000123");
        assertThat(response.getClaimStatus()).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(response.getClaimType()).isEqualTo(ClaimType.HOSPITALIZATION);

        Mockito.verify(claimRepository).findById(claimId);
    }

    @Test
    void getClaimById_notFound_shouldThrowException() {
        Long claimId = 999L;

        Mockito.when(claimRepository.findById(claimId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> claimService.getClaimById(claimId))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining(I18N.getMessage("error.claim.notfound", claimId));

        Mockito.verify(claimRepository).findById(claimId);
    }

    @Test
    void listClaims_success() {
        Claim claim = Claim.builder()
                .id(1L)
                .policyId(10L)
                .claimNumber("CLM-001")
                .claimDate(LocalDate.now())
                .claimAmount(BigDecimal.valueOf(1_000))
                .claimStatus(ClaimStatus.SUBMITTED)
                .claimType(ClaimType.HOSPITALIZATION)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Claim> page = new PageImpl<>(List.of(claim), pageable, 1);
        Mockito.when(claimRepository.findClaims(eq(10L), eq(ClaimStatus.SUBMITTED), any(Pageable.class))).thenReturn(page);
        DataTable result = claimService.getListClaims(10L, ClaimStatus.SUBMITTED, 0, 10);

        assertEquals(1, result.getTotal());
        ClaimResponse resp = (ClaimResponse) result.getContent().get(0);
        assertEquals(1L, resp.getClaimId());
        assertEquals("CLM-001", resp.getClaimNumber());
    }

    @Test
    void updateStatus_submittedToApproved_success() {
        Long claimId = 1L;

        Claim claim = Claim.builder()
                .id(claimId)
                .policyId(10L)
                .claimNumber("CLM-2025-000123")
                .claimStatus(ClaimStatus.SUBMITTED)
                .claimAmount(BigDecimal.valueOf(5_000_000))
                .build();

        UpdateClaimRequest req = new UpdateClaimRequest();
        req.setNewStatus(ClaimStatus.APPROVED);
        req.setApprovedAmount(BigDecimal.valueOf(4_500_000));
        req.setNote("approve test");

        when(claimRepository.findById(claimId))
                .thenReturn(Optional.of(claim));

        when(claimRepository.save(any(Claim.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ClaimResponse response = claimService.updateStatus(claimId, req);

        assertNotNull(response);
        assertEquals(claimId, response.getClaimId());
        assertEquals(ClaimStatus.APPROVED, response.getClaimStatus());
        assertEquals(BigDecimal.valueOf(4_500_000), response.getApprovedAmount());

        verify(claimRepository).findById(claimId);
        verify(claimRepository).save(claim);
    }

    @Test
    void updateStatus_claimNotFound() {
        Long claimId = 1L;
        UpdateClaimRequest req = new UpdateClaimRequest();
        req.setNewStatus(ClaimStatus.APPROVED);

        when(claimRepository.findById(claimId))
                .thenReturn(Optional.empty());

        RecordNotFoundException ex = assertThrows(
                RecordNotFoundException.class,
                () -> claimService.updateStatus(claimId, req)
        );

        assertEquals(I18N.getMessage("error.claim.notfound", claimId), ex.getMessage());
    }

    @Test
    void updateStatus_claimAlreadyApproved() {
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setClaimStatus(ClaimStatus.APPROVED);

        UpdateClaimRequest req = new UpdateClaimRequest();
        req.setNewStatus(ClaimStatus.APPROVED);
        req.setApprovedAmount(BigDecimal.valueOf(1000));

        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

        InactiveException ex = assertThrows(
                InactiveException.class,
                () -> claimService.updateStatus(1L, req)
        );

        assertEquals(I18N.getMessage("error.claim.status.terminal"), ex.getMessage());
    }

    @Test
    void updateStatus_claimAlreadyRejected() {
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setClaimStatus(ClaimStatus.REJECTED);

        UpdateClaimRequest req = new UpdateClaimRequest();
        req.setNewStatus(ClaimStatus.APPROVED);
        req.setApprovedAmount(BigDecimal.valueOf(1000));

        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

        InactiveException ex = assertThrows(
                InactiveException.class,
                () -> claimService.updateStatus(1L, req)
        );

        assertEquals(I18N.getMessage("error.claim.status.terminal"), ex.getMessage());
    }

    @Test
    void updateStatus_invalidTransition_throw400() {
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setClaimStatus(ClaimStatus.SUBMITTED);

        UpdateClaimRequest req = new UpdateClaimRequest();
        req.setNewStatus(ClaimStatus.SUBMITTED);

        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(claim));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> claimService.updateStatus(1L, req)
        );

        assertEquals(I18N.getMessage("error.claim.status.transaction.invalid"), ex.getMessage());
    }

    @Test
    void updateStatus_approvedAmountGreaterThanClaimAmount_throw400() {
        // given
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setClaimStatus(ClaimStatus.SUBMITTED);
        claim.setClaimAmount(BigDecimal.valueOf(5_000_000));

        UpdateClaimRequest req = new UpdateClaimRequest();
        req.setNewStatus(ClaimStatus.APPROVED);
        req.setApprovedAmount(BigDecimal.valueOf(6_000_000));

        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(claim));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> claimService.updateStatus(1L, req)
        );

        assertEquals(I18N.getMessage("error.claim.approvedAmount.exceeds"), ex.getMessage()
        );

    }
}
