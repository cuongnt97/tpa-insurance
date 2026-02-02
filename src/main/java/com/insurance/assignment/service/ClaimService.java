package com.insurance.assignment.service;

import com.insurance.assignment.common.CONSTANTS;
import com.insurance.assignment.common.config.I18N;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.exception.customexception.BusinessException;
import com.insurance.assignment.common.exception.customexception.InactiveException;
import com.insurance.assignment.common.exception.customexception.RecordNotFoundException;
import com.insurance.assignment.common.object.DataTable;
import com.insurance.assignment.model.dto.ClaimResponse;
import com.insurance.assignment.model.dto.CreateClaimRequest;
import com.insurance.assignment.model.entity.Claim;
import com.insurance.assignment.model.entity.Policy;
import com.insurance.assignment.repository.ClaimRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
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
            throw new InactiveException(I18N.getMessage("error.claim.status.inactive"));
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

    public ClaimResponse getClaimById(Long claimId) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RecordNotFoundException(I18N.getMessage("error.claim.notfound", claimId)));

        return mapToResponse(claim);
    }

    private ClaimResponse mapToResponse(Claim entity) {
        return ClaimResponse.builder()
                .claimId(entity.getId())
                .policyId(entity.getPolicyId())
                .claimNumber(entity.getClaimNumber())
                .claimDate(entity.getClaimDate())
                .claimAmount(entity.getClaimAmount())
                .approvedAmount(entity.getApprovedAmount())
                .claimStatus(entity.getClaimStatus())
                .claimType(entity.getClaimType())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public DataTable getListClaims(Long policyId, ClaimStatus status, int offset, int limit) {
        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());

        Page<Claim> pageResult = claimRepository.findClaims(policyId, status, pageable);
        List<ClaimResponse> data = pageResult.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();
        return new DataTable(data, pageResult.getTotalElements(), limit, offset);
    }
}
