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

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Residence> addResidence(@RequestBody Residence residence, Authentication authentication) {
        // Recupera o email do usuário autenticado a partir do token
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        // Associa a residência ao usuário e salva
        Residence createdResidence = residenceService.addResidence(residence, user);
        return ResponseEntity.ok(createdResidence);
    }

    // Endpoint para listar residências do usuário autenticado
    @GetMapping
    public ResponseEntity<Page<Residence>> getAllResidences(Pageable pageable, Authentication authentication) {
        // Recupera o email do usuário autenticado
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        // Busca todas as residências associadas ao usuário
        return ResponseEntity.ok(residenceService.findAllByUser(user, pageable));
    }
}
