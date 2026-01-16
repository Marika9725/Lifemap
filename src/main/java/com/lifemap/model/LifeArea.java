package com.lifemap.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "life_areas")
@NoArgsConstructor
@Getter @Setter
public class LifeArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private byte rate;

    @ManyToOne
    @JoinColumn(name = "wheel_of_life_id")
    private WheelOfLife wheelOfLife;
}