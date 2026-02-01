package com.insurance.assignment.service;

import com.insurance.assignment.common.CONSTANTS;
import com.insurance.assignment.common.config.I18N;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.exception.customexception.BusinessException;
import com.insurance.assignment.common.exception.customexception.RecordNotFoundException;
import com.insurance.assignment.model.dto.CreateClaimRequest;
import com.insurance.assignment.model.entity.Claim;
import com.insurance.assignment.model.entity.Policy;
import com.insurance.assignment.repository.ClaimRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final PolicyService policyService;

    @Transactional
    public Claim createClaim(CreateClaimRequest req) {
        Policy policy = policyService.findById(req.getPolicyId());
        if (policy == null) {
            throw new RecordNotFoundException(I18N.getMessage("error.policy.notfound", req.getPolicyId()));
        }
        if (CONSTANTS.POLICY_STATUS.INACTIVE.equals(policy.getStatus())) {
            throw new BusinessException(I18N.getMessage("error.claim.status.inactive"));
        }
        Claim claim = new Claim();
        claim.setPolicyId(req.getPolicyId());
        claim.setClaimNumber(generateClaimNumber());
        claim.setClaimDate(LocalDate.now());
        claim.setClaimAmount(req.getClaimAmount());
        claim.setClaimStatus(ClaimStatus.SUBMITTED);
        claim.setClaimType(req.getClaimType());
        claim.setDescription(req.getDescription());
        claim.setCreatedAt(Instant.now());

        return claimRepository.save(claim);
    }

    private String generateClaimNumber() {
        return "CLM-" + Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
