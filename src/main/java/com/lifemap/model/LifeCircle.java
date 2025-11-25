package com.lifemap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "life_circles")
@Getter @Setter
@NoArgsConstructor
public class LifeCircle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "lifeCircle")
    private User user;
}
