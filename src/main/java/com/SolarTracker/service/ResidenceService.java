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

    public Residence addResidence(Residence residence, User user) {
        residence.setUser(user);
        return residenceRepository.save(residence);
    }

    public Page<Residence> findAllByUser(User user, Pageable pageable) {
        return residenceRepository.findAll(pageable)
                .map(residence -> {
                    if (!residence.getUser().equals(user)) {
                        throw new SecurityException("Você não pode acessar esta residência.");
                    }
                    return residence;
                });
    }
}
