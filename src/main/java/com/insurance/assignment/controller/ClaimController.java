package com.insurance.assignment.controller;

import com.insurance.assignment.common.config.I18N;
import com.insurance.assignment.common.response.ObjectResponse;
import com.insurance.assignment.model.dto.CreateClaimRequest;
import com.insurance.assignment.model.entity.Claim;
import com.insurance.assignment.service.ClaimService;
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
        Claim claim = claimService.createClaim(req);
        ObjectResponse obj = new ObjectResponse(claim, I18N.getMessage("claim.created"));
        return new ResponseEntity(obj, HttpStatus.CREATED);
    }
}
