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
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Size(min = 3, max = 20, message = "{lifeArea.invalid.size}")
    @NotBlank(message = "{lifeArea.invalid.name}")
    private String name;

    @Min(value = 0, message = "{lifeArea.invalid.rate}")
    @Max(value = 10, message = "{lifeArea.invalid.rate}")
    private int rate;

    @ManyToOne
    @JoinColumn(name = "wheel_of_life_id")
    private WheelOfLife wheelOfLife;
}