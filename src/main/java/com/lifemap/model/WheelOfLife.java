package com.lifemap.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "wheels_of_life")
@Getter @Setter
@NoArgsConstructor
public class WheelOfLife {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "wheelOfLife")
    private User user;

    @OneToMany(mappedBy = "wheelOfLife", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LifeArea> lifeAreas = new HashSet<>();
}
