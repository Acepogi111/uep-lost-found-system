package com.uep.lostfound.repository;

import com.uep.lostfound.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByItemId(Long itemId);
    List<Claim> findByUserId(Long userId);
    List<Claim> findByStatus(String status);
}