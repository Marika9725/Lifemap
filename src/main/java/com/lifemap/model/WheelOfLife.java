package com.lifemap.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "wheels_of_life")
@Getter @Setter
@NoArgsConstructor
public class WheelOfLife {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "wheelOfLife")
    private User user;

    @OneToMany(mappedBy = "wheelOfLife", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LifeArea> lifeAreas;
}
