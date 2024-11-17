-- Insert reviewEntities for bookDto ID 84
INSERT INTO reviewDtoEntity (book_id, rating, review_text)
VALUES (84, 5, 'An outstanding bookDto!'),
       (84, 4, 'Very well written.'),
       (84, 5, 'A masterpiece of literature.');

-- Insert reviewEntities for bookDto ID 2701
INSERT INTO reviewDtoEntity (book_id, rating, review_text)
VALUES (2701, 4, 'A great adventure story.'),
       (2701, 3, 'Good, but not my favorite.'),
       (2701, 4, 'Well worth the read.');

-- Insert reviewEntities for bookDto ID 1513
INSERT INTO reviewDtoEntity (book_id, rating, review_text)
VALUES (1513, 5, 'Absolutely brilliant!'),
       (1513, 4, 'Enjoyed every moment of it.'),
       (1513, 5, 'One of the best bookDtos I have read.');

INSERT INTO reviewDtoEntity (book_id, rating, review_text, created_at)
VALUES (85, 5, 'Great bookDto!', '2023-01-15 10:00:00'),
       (85, 4, 'Enjoyable read.', '2023-01-20 15:30:00'),
       (85, 3, 'Could be better.', '2023-12-05 09:45:00'),
       (85, 4, 'Good bookDto.', '2023-12-10 11:20:00');
