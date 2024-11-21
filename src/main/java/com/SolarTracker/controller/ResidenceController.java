package com.SolarTracker.controller;

import com.SolarTracker.model.Residence;
import com.SolarTracker.model.User;
import com.SolarTracker.service.ResidenceService;
import com.SolarTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/residences")
@RequiredArgsConstructor
public class ResidenceController {

    private final ResidenceService residenceService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Residence> addResidence(@RequestBody Residence residence, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(residenceService.addResidence(residence, user));
    }

    @GetMapping
    public ResponseEntity<Page<Residence>> getAllResidences(Pageable pageable, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(residenceService.findAllByUser(user, pageable));
    }
}
