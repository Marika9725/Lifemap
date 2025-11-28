package com.lifemap.model.projection;

import com.lifemap.model.LifeArea;
import jakarta.validation.constraints.*;
import lombok.*;

//TODO: test it!
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class LifeAreaDTO {
    @Size(min = 3, max = 20, message = "{lifeArea.invalid.size}")
    @NotBlank(message = "{lifeArea.invalid.name}")
    private String name;

    @Min(value = 0, message = "{lifeArea.invalid.rate}")
    @Max(value = 10, message = "{lifeArea.invalid.rate}")
    private byte rate;

    public LifeAreaDTO(LifeArea lifeArea) {
        this.name = lifeArea.getName();
        this.rate = lifeArea.getRate();
    }

    //TODO: test it!
    public LifeArea toLifeArea() {
        var lifeArea = new LifeArea();
        lifeArea.setName(this.name);
        lifeArea.setRate(this.rate);

        return lifeArea;
    }

}
