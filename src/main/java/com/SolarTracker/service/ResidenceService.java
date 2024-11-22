package com.SolarTracker.service;

import com.SolarTracker.model.Residence;
import com.SolarTracker.model.User;
import com.SolarTracker.repository.ResidenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResidenceService {

    private final ResidenceRepository residenceRepository;
    private final MessageProducer messageProducer;

    public Residence addResidence(Residence residence, User user) {
        residence.setUser(user);
        Residence savedResidence = residenceRepository.save(residence);

        String message = "Nova residência adicionada: " + savedResidence.getName() + " (Usuário: " + user.getEmail() + ")";
        messageProducer.sendMessage(message);

        return savedResidence;
    }

    public Page<Residence> findAllByUser(User user, Pageable pageable) {
        return residenceRepository.findAllByUser(user, pageable);
    }
}
