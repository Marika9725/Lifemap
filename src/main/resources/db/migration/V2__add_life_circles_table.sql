CREATE TABLE life_circles (
    id INT AUTO_INCREMENT PRIMARY KEY
);

ALTER TABLE users ADD COLUMN life_circle_id INT;
ALTER TABLE users ADD FOREIGN KEY (life_circle_id) REFERENCES life_circles(id);