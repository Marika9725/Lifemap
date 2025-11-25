CREATE TABLE lifeAreas(
    id INT AUTO-INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE CHECK(char_length(name) >= 3 and char_length(name) <= 20),
    rate INT CHECK(rate >= 0 and rate <= 10),
    FOREIGN KEY (user_id) REFERENCES users(id)
)