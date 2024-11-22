package com.SolarTracker.repository;

import com.SolarTracker.model.Residence;
import com.SolarTracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidenceRepository extends JpaRepository<Residence, Long> {
    Page<Residence> findAllByUser(User user, Pageable pageable);
}