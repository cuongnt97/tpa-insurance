package com.insurance.assignment.repository;

import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.model.entity.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    @Query("""
        SELECT c FROM claim c
        WHERE (:policyId IS NULL OR c.policyId = :policyId)
          AND (:claimStatus IS NULL OR c.claimStatus = :claimStatus)
    """)
    Page<Claim> findClaims(
            @Param("policyId") Long policyId,
            @Param("claimStatus") ClaimStatus claimStatus,
            Pageable pageable
    );
}
