CREATE TABLE wheels_of_life (
    id INT AUTO_INCREMENT PRIMARY KEY
);

ALTER TABLE users ADD COLUMN wheel_of_life_id INT;
ALTER TABLE users ADD FOREIGN KEY (wheel_of_life_id) REFERENCES wheels_of_life(id);