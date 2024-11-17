CREATE TABLE review
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id     INT NOT NULL,
    rating      INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
