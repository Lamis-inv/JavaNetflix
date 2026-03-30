
CREATE DATABASE IF NOT EXISTS jstream_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE jstream_db;

CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,  -- BCrypt hashed
    role        ENUM('USER','ADMIN') DEFAULT 'USER',
    avatar_url  VARCHAR(500),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS content (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    type          ENUM('FILM','SERIE') NOT NULL,
    synopsis      TEXT,
    release_year  YEAR,
    cover_url     VARCHAR(500),
    trailer_url   VARCHAR(500),
    video_url     VARCHAR(500),        -- For films only
    duration_min  INT,                 -- For films only
    casting       TEXT,
    category_id   INT,
    avg_rating    DECIMAL(3,2) DEFAULT 0,
    view_count    INT DEFAULT 0,
    is_featured   TINYINT(1) DEFAULT 0,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS seasons (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    serie_id   INT NOT NULL,
    number     INT NOT NULL,
    title      VARCHAR(255),
    FOREIGN KEY (serie_id) REFERENCES content(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS episodes (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    season_id     INT NOT NULL,
    episode_num   INT NOT NULL,
    title         VARCHAR(255) NOT NULL,
    synopsis      TEXT,
    duration_min  INT,
    video_url     VARCHAR(500),
    thumbnail_url VARCHAR(500),
    FOREIGN KEY (season_id) REFERENCES seasons(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ratings (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    content_id  INT NOT NULL,
    stars       TINYINT NOT NULL CHECK (stars BETWEEN 1 AND 5),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_content (user_id, content_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    content_id  INT NOT NULL,
    body        TEXT NOT NULL,
    flagged     TINYINT(1) DEFAULT 0,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS watchlist (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    content_id  INT NOT NULL,
    added_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_watchlist (user_id, content_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS watch_history (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    content_id    INT,
    episode_id    INT,
    progress_sec  INT DEFAULT 0,        -- seconds watched
    completed     TINYINT(1) DEFAULT 0,
    watched_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE,
    FOREIGN KEY (episode_id) REFERENCES episodes(id) ON DELETE CASCADE
);

INSERT INTO categories (name) VALUES
('Action'),('Comédie'),('Science-Fiction'),('Drame'),('Thriller'),
('Horreur'),('Animation'),('Documentaire'),('Romance'),('Aventure');

-- Users (passwords all = "password123" BCrypt hashed)
-- Hash: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi is "password"
INSERT INTO users (name, email, password, role) VALUES
('Admin User',  'admin@jstream.com',  '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'ADMIN'),
('Alice Martin','alice@example.com',  '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'USER'),
('Bob Dupont',  'bob@example.com',    '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'USER'),
('Clara Petit',  'clara@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'USER'),
('David Moreau','david@example.com',  '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'USER');

INSERT INTO content (title, type, synopsis, release_year, cover_url, video_url, duration_min, casting, category_id, avg_rating, view_count, is_featured) VALUES
('Inception',          'FILM', 'Un voleur spécialisé dans l''art de s''infiltrer dans les rêves des autres.', 2010, 'https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 148, 'Leonardo DiCaprio, Joseph Gordon-Levitt', 3, 4.8, 1520, 1),
('The Dark Knight',    'FILM', 'Batman affronte le Joker, un criminel au génie du chaos.', 2008, 'https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 152, 'Christian Bale, Heath Ledger', 1, 4.9, 2100, 1),
('Interstellar',       'FILM', 'Un groupe d''astronautes voyage à travers un trou de ver à la recherche d''une nouvelle demeure.', 2014, 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 169, 'Matthew McConaughey, Anne Hathaway', 3, 4.7, 1890, 1),
('The Matrix',         'FILM', 'Un hacker découvre que la réalité telle qu''il la connaît est une simulation.', 1999, 'https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 136, 'Keanu Reeves, Laurence Fishburne', 3, 4.6, 1750, 0),
('Parasite',           'FILM', 'Toute la famille Ki-taek est au chômage et s''intéresse à la vie de la riche famille Park.', 2019, 'https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 132, 'Song Kang-ho, Lee Sun-kyun', 4, 4.8, 1300, 1),
('Avengers: Endgame',  'FILM', 'Les Avengers restants s''unissent pour annuler les actions de Thanos.', 2019, 'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 181, 'Robert Downey Jr., Chris Evans', 1, 4.7, 2500, 1),
('Forrest Gump',       'FILM', 'La présidence de Kennedy à la guerre du Vietnam, Forrest Gump y était.', 1994, 'https://image.tmdb.org/t/p/w500/saHP97rTPS5eLmrLQEcANmKrsFl.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 142, 'Tom Hanks, Robin Wright', 4, 4.8, 1680, 0),
('The Lion King',      'FILM', 'Un jeune lion doit accepter la responsabilité de régner sur sa terre natale.', 1994, 'https://image.tmdb.org/t/p/w500/sKCr78MXSLixwmZ8DyJLrpMsd15.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 88,  'Matthew Broderick, Jeremy Irons', 7, 4.6, 1400, 0),
('Get Out',            'FILM', 'Un jeune afro-américain rencontre la famille libérale de sa petite amie blanche.', 2017, 'https://image.tmdb.org/t/p/w500/tFXcEccSQMf3lfhfXKSU9iRBpa3.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 104, 'Daniel Kaluuya, Allison Williams', 6, 4.5, 980, 0),
('Spirited Away',      'FILM', 'Une jeune fille se retrouve dans un monde d''esprits et de dieux japonais.', 2001, 'https://image.tmdb.org/t/p/w500/Ab8mkHmkYADjU7wQiOkia9BzGvS.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 125, 'Daveigh Chase, Suzanne Pleshette', 7, 4.9, 1200, 0),
('Joker',              'FILM', 'Arthur Fleck, un comédien raté, descend dans la folie et devient le Joker.', 2019, 'https://image.tmdb.org/t/p/w500/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 122, 'Joaquin Phoenix, Robert De Niro', 5, 4.6, 1600, 0),
('1917',               'FILM', 'Deux soldats britanniques doivent traverser le territoire ennemi pour délivrer un message crucial.', 2019, 'https://image.tmdb.org/t/p/w500/iZf0KyrE25z1sage4SYQLMjKNAI.jpg', 'https://www.w3schools.com/html/mov_bbb.mp4', 119, 'George MacKay, Dean-Charles Chapman', 1, 4.7, 1100, 0);

INSERT INTO content (title, type, synopsis, release_year, cover_url, casting, category_id, avg_rating, view_count, is_featured) VALUES
('Breaking Bad',  'SERIE', 'Un professeur de chimie atteint d''un cancer du poumon se tourne vers la fabrication de méthamphétamine.', 2008, 'https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg', 'Bryan Cranston, Aaron Paul', 5, 4.9, 3200, 1),
('Stranger Things','SERIE','Dans les années 80, un groupe d''enfants tente de résoudre le mystère de la disparition de leur ami.', 2016, 'https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg', 'Millie Bobby Brown, Finn Wolfhard', 3, 4.8, 2800, 1),
('Money Heist',   'SERIE', 'Un mystérieux professeur planifie le braquage parfait de la Monnaie royale d''Espagne.', 2017, 'https://image.tmdb.org/t/p/w500/reEMJA1uzscCbkpeRJeTT2bjqUp.jpg', 'Álvaro Morte, Úrsula Corberó', 5, 4.7, 2400, 0);

INSERT INTO seasons (serie_id, number, title) VALUES
(13, 1, 'Saison 1'),
(13, 2, 'Saison 2');

INSERT INTO episodes (season_id, episode_num, title, synopsis, duration_min, video_url, thumbnail_url) VALUES
(1, 1, 'Pilot', 'Walter White, professeur de chimie, apprend qu''il a un cancer.', 58, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg'),
(1, 2, 'Cat''s in the Bag', 'Walt et Jesse doivent se débarrasser de deux corps.', 48, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg'),
(1, 3, 'And the Bag''s in the River', 'Walt doit décider du sort de Krazy-8.', 48, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg'),
(2, 1, 'Seven Thirty-Seven', 'Walt et Jesse cherchent 737,000$ pour rembourser Tuco.', 47, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg'),
(2, 2, 'Grilled', 'Walt et Jesse sont retenus en otage par Tuco.', 47, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg');

-- Seasons for Stranger Things (id=14)
INSERT INTO seasons (serie_id, number, title) VALUES
(14, 1, 'Saison 1'),
(14, 2, 'Saison 2');

INSERT INTO episodes (season_id, episode_num, title, synopsis, duration_min, video_url, thumbnail_url) VALUES
(3, 1, 'The Vanishing of Will Byers', 'Will Byers disparaît mystérieusement.', 47, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://image.tmdb.org/t/p/w300/49WJfeN0moxb9IPfGn8AIqMGskD.jpg'),
(3, 2, 'The Weirdo on Maple Street', 'Les garçons trouvent une fille mystérieuse nommée Eleven.', 55, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://image.tmdb.org/t/p/w300/49WJfeN0moxb9IPfGn8AIqMGskD.jpg'),
(4, 1, 'MADMAX', 'Une nouvelle recrue à Hawkins Middle suscite la jalousie d''Eleven.', 46, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://image.tmdb.org/t/p/w300/49WJfeN0moxb9IPfGn8AIqMGskD.jpg');

-- Sample ratings
INSERT INTO ratings (user_id, content_id, stars) VALUES
(2,1,5),(2,2,5),(2,3,4),(3,1,4),(3,4,5),(4,5,5),(4,6,4),(5,7,5),(5,8,4),(3,13,5),(4,14,5);

INSERT INTO comments (user_id, content_id, body) VALUES
(2,1,'Film absolument incroyable, les effets spéciaux sont époustouflants!'),
(3,1,'Un chef-d''oeuvre du cinéma moderne. Nolan est un génie.'),
(4,2,'Heath Ledger dans le rôle du Joker est inoubliable.'),
(2,13,'La meilleure série de tous les temps, sans aucun doute.'),
(5,14,'Stranger Things me donne des frissons à chaque épisode!');

INSERT INTO watchlist (user_id, content_id) VALUES (2,3),(2,5),(3,6),(4,13),(5,14);


INSERT INTO watch_history (user_id, content_id, episode_id, progress_sec, completed) VALUES
(2, 13, 1, 3480, 1),
(2, 13, 2, 1200, 0),
(3, 14, 5, 2820, 1),
(3, 14, 6, 0, 0);

UPDATE content c SET avg_rating = (
    SELECT IFNULL(AVG(stars),0) FROM ratings WHERE content_id = c.id
);
