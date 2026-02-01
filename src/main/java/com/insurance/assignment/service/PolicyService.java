package com.insurance.assignment.service;

import com.insurance.assignment.model.entity.Policy;
import com.insurance.assignment.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;

    public Policy findById(Long id) {
        return policyRepository.findById(id).orElse(null);
    }
}
