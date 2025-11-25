package com.lifemap.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/*@Entity
@Table(name = "lifeAreas")
@NoArgsConstructor
@Getter @Setter
public class LifeArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    @Size(min = 3, max = 20, message = "{lifeArea.invalid.size}")
    @NotBlank
    private String name;
    @Min(0)
    @Max(10)
    private int rate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}*/