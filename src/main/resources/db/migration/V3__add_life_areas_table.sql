CREATE TABLE life_areas(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    rate INT NOT NULL,
    wheel_of_life_id INT,
    CONSTRAINT fk_wheel_of_life FOREIGN KEY (wheel_of_life_id) REFERENCES wheels_of_life(id)
);