package com.insurance.assignment.controller;

import com.insurance.assignment.common.config.I18N;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.object.DataTable;
import com.insurance.assignment.common.response.DataTableResponse;
import com.insurance.assignment.common.response.ObjectResponse;
import com.insurance.assignment.model.dto.ClaimResponse;
import com.insurance.assignment.model.dto.CreateClaimRequest;
import com.insurance.assignment.model.dto.UpdateClaimRequest;
import com.insurance.assignment.model.entity.Claim;
import com.insurance.assignment.service.ClaimService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("")
    public ResponseEntity createClaim(@Valid @RequestBody CreateClaimRequest req) {
        ClaimResponse claim = claimService.createClaim(req);
        ObjectResponse obj = new ObjectResponse(claim, I18N.getMessage("claim.created"));
        return new ResponseEntity(obj, HttpStatus.CREATED);
    }

    @GetMapping("/{claimId}")
    public ResponseEntity getClaimById(@PathVariable Long claimId) {
        ClaimResponse claimResponse = claimService.getClaimById(claimId);
        ObjectResponse obj = new ObjectResponse(claimResponse, I18N.getMessage("action.success"));
        return new ResponseEntity(obj, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity getAllClaims(@RequestParam(required = false) Long policyId,
                                       @RequestParam(required = false) ClaimStatus status,
                                       @RequestParam(defaultValue = "20") @Min(value = 1, message = "{error.claim.limit.not.negative}") @Max(value = 100, message = "{error.claim.limit.max100}") int limit,
                                       @RequestParam(defaultValue = "0") @Min(value = 0, message = "{error.claim.offset.not.negative}") int offset) {
        DataTable dataTable = claimService.getListClaims(policyId, status, offset, limit);
        DataTableResponse response = new DataTableResponse(dataTable, I18N.getMessage("action.success"));
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PatchMapping("/{claimId}")
    public ResponseEntity updateStatus(@PathVariable Long claimId, @Valid @RequestBody UpdateClaimRequest req) {
        ClaimResponse claimResponse = claimService.updateStatus(claimId, req);
        ObjectResponse obj = new ObjectResponse(claimResponse, I18N.getMessage("action.success"));
        return new ResponseEntity(obj, HttpStatus.OK);
    }
}
