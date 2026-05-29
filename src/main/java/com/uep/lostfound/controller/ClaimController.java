package com.uep.lostfound.controller;

import com.uep.lostfound.model.Claim;
import com.uep.lostfound.model.Item;
import com.uep.lostfound.model.User;
import com.uep.lostfound.repository.ClaimRepository;
import com.uep.lostfound.repository.ItemRepository;
import com.uep.lostfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "*")
public class ClaimController {

    @Autowired
    private ClaimRepository claimRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createClaim(@RequestBody Map<String, Object> data) {
        Long itemId = Long.valueOf(data.get("itemId").toString());
        Long userId = Long.valueOf(data.get("userId").toString());
        String message = data.get("message").toString();
        
        Item item = itemRepository.findById(itemId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        
        if (item == null || user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Item or User not found"));
        }
        
        Claim claim = new Claim();
        claim.setItem(item);
        claim.setUser(user);
        claim.setMessage(message);
        claimRepository.save(claim);
        
        return ResponseEntity.ok(Map.of("message", "Claim submitted successfully"));
    }

    @GetMapping("/item/{itemId}")
    public List<Claim> getItemClaims(@PathVariable Long itemId) {
        return claimRepository.findByItemId(itemId);
    }

    @GetMapping("/user/{userId}")
    public List<Claim> getUserClaims(@PathVariable Long userId) {
        return claimRepository.findByUserId(userId);
    }

    @GetMapping("/pending")
    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatus("PENDING");
    }

    @PutMapping("/approve/{claimId}")
    public ResponseEntity<?> approveClaim(@PathVariable Long claimId) {
        Claim claim = claimRepository.findById(claimId).orElse(null);
        if (claim == null) return ResponseEntity.badRequest().body(Map.of("message", "Claim not found"));
        
        claim.setStatus("APPROVED");
        claimRepository.save(claim);
        
        Item item = claim.getItem();
        item.setStatus("CLAIMED");
        itemRepository.save(item);
        
        return ResponseEntity.ok(Map.of("message", "Claim approved and item marked as claimed"));
    }

    @PutMapping("/reject/{claimId}")
    public ResponseEntity<?> rejectClaim(@PathVariable Long claimId) {
        Claim claim = claimRepository.findById(claimId).orElse(null);
        if (claim == null) return ResponseEntity.badRequest().body(Map.of("message", "Claim not found"));
        
        claim.setStatus("REJECTED");
        claimRepository.save(claim);
        return ResponseEntity.ok(Map.of("message", "Claim rejected"));
    }
}