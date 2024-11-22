package com.SolarTracker.controller;

import com.SolarTracker.exception.ResourceNotFoundException;
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

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Residence> addResidence(@RequestBody Residence residence, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);

            if (user == null) {
                throw new ResourceNotFoundException("Usuário autenticado não encontrado.");
            }

            Residence createdResidence = residenceService.addResidence(residence, user);
            return ResponseEntity.ok(createdResidence);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Residence>> getAllResidences(Pageable pageable, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);

            if (user == null) {
                throw new ResourceNotFoundException("Usuário autenticado não encontrado.");
            }

            Page<Residence> residences = residenceService.findAllByUser(user, pageable);
            return ResponseEntity.ok(residences);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
