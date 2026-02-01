package com.insurance.assignment;

import com.insurance.assignment.common.CONSTANTS;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.enumvalue.ClaimType;
import com.insurance.assignment.common.exception.customexception.BusinessException;
import com.insurance.assignment.common.exception.customexception.RecordNotFoundException;
import com.insurance.assignment.model.dto.CreateClaimRequest;
import com.insurance.assignment.model.entity.Claim;
import com.insurance.assignment.model.entity.Policy;
import com.insurance.assignment.repository.ClaimRepository;
import com.insurance.assignment.service.ClaimService;
import com.insurance.assignment.service.PolicyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        Claim result = claimService.createClaim(req);

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

        assertThrows(BusinessException.class,
                () -> claimService.createClaim(req));
    }
}
