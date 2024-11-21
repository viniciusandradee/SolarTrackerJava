package com.SolarTracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class Residence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String state;

    private boolean hasSolarPanel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
