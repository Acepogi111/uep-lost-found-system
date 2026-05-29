package com.uep.lostfound.controller;

import com.uep.lostfound.model.Item;
import com.uep.lostfound.model.User;
import com.uep.lostfound.repository.ItemRepository;
import com.uep.lostfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @PostMapping("/report")
    public ResponseEntity<?> reportItem(
            @RequestParam("type") String type,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("location") String location,
            @RequestParam("date") String date,
            @RequestParam("description") String description,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
            
            Item item = new Item();
            item.setType(type);
            item.setName(name);
            item.setCategory(category);
            item.setLocation(location);
            item.setDate(LocalDate.parse(date));
            item.setDescription(description);
            item.setUser(user);
            
            if (image != null && !image.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                Files.copy(image.getInputStream(), uploadPath.resolve(fileName));
                item.setImageUrl("/uploads/" + fileName);
            }
            
            itemRepository.save(item);
            return ResponseEntity.ok(Map.of("message", "Item reported successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Item> getUserItems(@PathVariable Long userId) {
        return itemRepository.findByUserId(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String query) {
        return itemRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrLocationContainingIgnoreCase(query, query, query);
    }
}