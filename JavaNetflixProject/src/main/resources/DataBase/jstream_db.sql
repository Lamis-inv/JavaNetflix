-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 11, 2026 at 02:43 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `jstream_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`) VALUES
(1, 'Action'),
(7, 'Animation'),
(10, 'Aventure'),
(2, 'Comédie'),
(8, 'Documentaire'),
(4, 'Drame'),
(6, 'Horreur'),
(9, 'Romance'),
(3, 'Science-Fiction'),
(5, 'Thriller');

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

CREATE TABLE `comments` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content_id` int(11) NOT NULL,
  `body` text NOT NULL,
  `flagged` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`id`, `user_id`, `content_id`, `body`, `flagged`, `created_at`) VALUES
(2, 3, 1, 'Un chef-d\'oeuvre du cinéma moderne. Nolan est un génie.', 0, '2026-02-23 14:49:09'),
(3, 4, 2, 'Heath Ledger dans le rôle du Joker est inoubliable.', 0, '2026-02-23 14:49:09'),
(4, 2, 13, 'La meilleure série de tous les temps, sans aucun doute.', 0, '2026-02-23 14:49:09'),
(5, 5, 14, 'Stranger Things me donne des frissons à chaque épisode!', 0, '2026-02-23 14:49:09'),
(6, 7, 1, 'woww', 0, '2026-02-23 15:30:43');

-- --------------------------------------------------------

--
-- Table structure for table `content`
--

CREATE TABLE `content` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `type` enum('FILM','SERIE') NOT NULL,
  `synopsis` text DEFAULT NULL,
  `release_year` year(4) DEFAULT NULL,
  `cover_url` varchar(500) DEFAULT NULL,
  `trailer_url` varchar(500) DEFAULT NULL,
  `video_url` varchar(500) DEFAULT NULL,
  `duration_min` int(11) DEFAULT NULL,
  `casting` text DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `avg_rating` decimal(3,2) DEFAULT 0.00,
  `view_count` int(11) DEFAULT 0,
  `is_featured` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `content`
--

INSERT INTO `content` (`id`, `title`, `type`, `synopsis`, `release_year`, `cover_url`, `trailer_url`, `video_url`, `duration_min`, `casting`, `category_id`, `avg_rating`, `view_count`, `is_featured`, `created_at`) VALUES
(1, 'Inception', 'FILM', 'Un voleur spécialisé dans l\'art de s\'infiltrer dans les rêves des autres.', '2010', 'https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 148, 'Leonardo DiCaprio, Joseph Gordon-Levitt', 3, 4.33, 1540, 1, '2026-02-23 14:49:09'),
(2, 'The Dark Knight', 'FILM', 'Batman affronte le Joker, un criminel au génie du chaos.', '2008', 'https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 152, 'Christian Bale, Heath Ledger', 1, 5.00, 2110, 1, '2026-02-23 14:49:09'),
(3, 'Interstellar', 'FILM', 'Un groupe d\'astronautes voyage à travers un trou de ver à la recherche d\'une nouvelle demeure.', '2014', 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 169, 'Matthew McConaughey, Anne Hathaway', 3, 4.00, 1892, 1, '2026-02-23 14:49:09'),
(4, 'The Matrix', 'FILM', 'Un hacker découvre que la réalité telle qu\'il la connaît est une simulation.', '1999', 'https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 136, 'Keanu Reeves, Laurence Fishburne', 3, 5.00, 1751, 0, '2026-02-23 14:49:09'),
(5, 'Parasite', 'FILM', 'Toute la famille Ki-taek est au chômage et s\'intéresse à la vie de la riche famille Park.', '2019', 'https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 132, 'Song Kang-ho, Lee Sun-kyun', 4, 5.00, 1301, 1, '2026-02-23 14:49:09'),
(6, 'Avengers: Endgame', 'FILM', 'Les Avengers restants s\'unissent pour annuler les actions de Thanos.', '2019', 'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 181, 'Robert Downey Jr., Chris Evans', 1, 4.00, 2505, 1, '2026-02-23 14:49:09'),
(7, 'Forrest Gump', 'FILM', 'La présidence de Kennedy à la guerre du Vietnam, Forrest Gump y était.', '1994', 'https://image.tmdb.org/t/p/w500/saHP97rTPS5eLmrLQEcANmKrsFl.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 142, 'Tom Hanks, Robin Wright', 4, 5.00, 1680, 0, '2026-02-23 14:49:09'),
(8, 'The Lion King', 'FILM', 'Un jeune lion doit accepter la responsabilité de régner sur sa terre natale.', '1994', 'https://image.tmdb.org/t/p/w500/sKCr78MXSLixwmZ8DyJLrpMsd15.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 88, 'Matthew Broderick, Jeremy Irons', 7, 4.00, 1400, 0, '2026-02-23 14:49:09'),
(9, 'Get Out', 'FILM', 'Un jeune afro-américain rencontre la famille libérale de sa petite amie blanche.', '2017', 'https://image.tmdb.org/t/p/w500/tFXcEccSQMf3lfhfXKSU9iRBpa3.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 104, 'Daniel Kaluuya, Allison Williams', 6, 0.00, 981, 0, '2026-02-23 14:49:09'),
(10, 'Spirited Away', 'FILM', 'Une jeune fille se retrouve dans un monde d\'esprits et de dieux japonais.', '2001', 'https://image.tmdb.org/t/p/w500/Ab8mkHmkYADjU7wQiOkia9BzGvS.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 125, 'Daveigh Chase, Suzanne Pleshette', 7, 0.00, 1203, 0, '2026-02-23 14:49:09'),
(11, 'Joker', 'FILM', 'Arthur Fleck, un comédien raté, descend dans la folie et devient le Joker.', '2019', 'https://image.tmdb.org/t/p/w500/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 122, 'Joaquin Phoenix, Robert De Niro', 5, 0.00, 1600, 0, '2026-02-23 14:49:09'),
(13, 'Breaking Bad', 'SERIE', 'Un professeur de chimie atteint d\'un cancer du poumon se tourne vers la fabrication de méthamphétamine.', '2008', 'https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', NULL, 'Bryan Cranston, Aaron Paul', 5, 5.00, 3216, 1, '2026-02-23 14:49:09'),
(14, 'Stranger Things', 'SERIE', 'Dans les années 80, un groupe d\'enfants tente de résoudre le mystère de la disparition de leur ami.', '2016', 'https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg', NULL, '', NULL, 'Millie Bobby Brown, Finn Wolfhard', 3, 5.00, 2833, 1, '2026-02-23 14:49:09'),
(15, 'Money Heist', 'SERIE', 'Un mystérieux professeur planifie le braquage parfait de la Monnaie royale d\'Espagne.', '2017', 'https://image.tmdb.org/t/p/w500/reEMJA1uzscCbkpeRJeTT2bjqUp.jpg', NULL, '', NULL, 'Álvaro Morte, Úrsula Corberó', 5, 0.00, 2404, 0, '2026-02-23 14:49:09'),
(16, 'Shrek 4', 'FILM', 'A midlife-crisis burdened Shrek, longing for the days when he felt like a real ogre, makes a pact with magic deal-maker Rumpelstiltskin. But when he\'s duped and sent to a twisted version of Far Far Away—where Rumpelstiltskin is king, ogres are hunted, and he and Fiona have never met—he sets out to restore his world and reclaim his true love.', '2010', 'https://www.themoviedb.org/t/p/w600_and_h900_face/6HrfPZtKcGmX2tUWW3cnciZTaSD.jpg', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 93, 'Ted Elliott,Andrew Adamson', 7, 5.00, 68, 1, '2026-02-23 15:41:35'),
(37, 'Bumble Bee', 'FILM', 'A young Autobot named Bumblebee finds his way on Earth and forms a special bond with a teenager to protect humanity from the Decepticons.', '2018', '/images/bumblebee.png', '/videos/trailer_bumblebee.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 114, 'Hailee Steinfeld, John Cena, Angela Bassett', 1, 4.30, 1208, 1, '2026-03-31 22:13:28'),
(38, 'Kung Fu Panda 4', 'FILM', 'Po returns for more adventures and faces a new villain threatening the Valley of Peace, while learning more about his true destiny.', '2024', '/images/kungfupanda4.jpg', '/videos/trailer_kungfupanda4.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 105, 'Jack Black, Angelina Jolie, Dustin Hoffman', 2, 4.10, 950, 1, '2026-03-31 22:13:28'),
(39, 'Five Nights at Freddy\'s 2', 'FILM', 'A terrifying journey as a night security guard uncovers deadly animatronics that come alive after dark, threatening the lives of anyone who stays late.', '2023', '/images/fnaf2.png', '/videos/trailer_fnaf2.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 97, 'Josh Hutcherson, Emma Roberts', 6, 3.80, 872, 1, '2026-03-31 22:13:28'),
(40, 'Damsel', 'FILM', 'A courageous princess must navigate treacherous lands and face dangerous enemies to save her kingdom and prove her worth.', '2024', '/images/damsel.jpg', '/videos/trailer_damsel.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 120, 'Millie Bobby Brown,Saoirse Ronan, Timothée Chalamet', 10, 4.00, 433, 0, '2026-03-31 22:13:28'),
(41, 'Free Guy', 'FILM', 'A non-player character in a video game gains self-awareness and embarks on a mission to become the hero of his own story.', '2021', '/images/freeguy.jpg', '/videos/trailer_freeguy.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 115, 'Ryan Reynolds, Jodie Comer, Taika Waititi', 2, 4.20, 1500, 1, '2026-03-31 22:13:28'),
(42, 'Mortal Engines', 'FILM', 'In a post-apocalyptic world, giant moving cities hunt and consume smaller ones, while heroes try to prevent a catastrophic war.', '2018', '/images/mortalengines.png', '/videos/trailer_mortalengines.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 128, 'Hera Hilmar, Hugo Weaving, Robert Sheehan', 1, 3.90, 658, 0, '2026-03-31 22:13:28'),
(43, 'Sing', 'FILM', 'A koala named Buster Moon organizes a singing competition to save his theater, attracting unique performers with dreams of stardom.', '2016', '/images/sing.jpg', '/videos/trailer_sing.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 108, 'Matthew McConaughey, Reese Witherspoon, Scarlett Johansson', 2, 4.50, 1401, 1, '2026-03-31 22:13:28'),
(44, 'Zootopia 2', 'FILM', 'Detective Judy Hopps and Nick Wilde tackle a new mystery in the bustling city of Zootopia, where animals of all kinds live together.', '2025', '/images/zootopia2.jpg', '/videos/trailer_zootopia2.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 110, 'Ginnifer Goodwin, Jason Bateman, Shakira', 7, 4.60, 901, 0, '2026-03-31 22:13:28'),
(45, 'Tetris', 'FILM', 'A gripping look at the creation and global impact of the iconic puzzle game, exploring its developers and the challenges they faced.', '2023', '/images/tetris.jpg', '/videos/trailer_tetris.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 102, 'Taron Egerton, Thomas Middleditch', 4, 4.00, 320, 0, '2026-03-31 22:13:28'),
(46, 'Uncharted', 'FILM', 'Treasure hunter Nathan Drake embarks on a globe-trotting adventure to uncover historical mysteries and retrieve priceless artifacts.', '2022', '/images/uncharted.jpg', '/videos/trailer_uncharted.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 116, 'Tom Holland, Mark Wahlberg, Antonio Banderas', 1, 4.00, 881, 1, '2026-03-31 22:13:28'),
(47, 'Antman', 'FILM', 'Scott Lang becomes Ant-Man, using his shrinking technology to pull off heists and fight evil forces threatening the world.', '2015', '/images/antman.jpg', '/videos/trailer_antman.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 117, 'Paul Rudd, Michael Douglas, Evangeline Lilly', 1, 4.20, 1350, 1, '2026-03-31 22:13:28'),
(48, 'Guardians of the Galaxy', 'FILM', 'A band of misfit heroes must save the galaxy from an all-powerful villain while forming unlikely friendships.', '2014', '/images/guardiansofthegalaxy.jpg', '/videos/trailer_guardians.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 121, 'Chris Pratt, Zoe Saldana, Dave Bautista', 3, 4.50, 2100, 1, '2026-03-31 22:13:28'),
(49, 'Spiderman Far From Home', 'FILM', 'Peter Parker goes on a school trip across Europe and faces new threats, balancing his life as a student and a superhero.', '2019', '/images/spidermanfarfromhome.jpg', '/videos/trailer_spidermanffh.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 129, 'Tom Holland, Samuel L. Jackson, Jake Gyllenhaal', 1, 4.40, 1801, 1, '2026-03-31 22:13:28'),
(50, 'Spiderman Homecoming', 'FILM', 'Peter Parker tries to balance high school life with being Spider-Man as he faces a new villain in New York.', '2017', '/images/spidermanhomecoming.jpg', '/videos/trailer_spidermanhc.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 133, 'Tom Holland, Robert Downey Jr., Marisa Tomei', 1, 4.30, 1650, 1, '2026-03-31 22:13:28'),
(51, 'Spiderman No Way Home', 'FILM', 'Multiverse chaos begins as Peter Parker seeks help from friends old and new to confront a dangerous threat.', '2021', '/images/spidermannowayhome.jpg', '/videos/trailer_spidermannwh.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 148, 'Tom Holland, Zendaya, Benedict Cumberbatch', 1, 4.80, 2202, 1, '2026-03-31 22:13:28'),
(52, 'Ready Player One', 'FILM', 'In a dystopian future, a young gamer embarks on a quest to find an Easter egg that will change his life forever.', '2018', '/images/readyplayerone.jpg', '/videos/trailer_readyplayerone.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 140, 'Tye Sheridan, Olivia Cooke, Ben Mendelsohn', 1, 4.30, 1300, 1, '2026-03-31 22:13:28'),
(53, 'KPop Demon Hunters', 'FILM', 'A group of idols fights supernatural demons while discovering their inner strength and teamwork.', '2024', '/images/kpopdemonhunters.jpg', '/videos/trailer_kpopdemonhunters.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 112, 'Lee Dong-wook, IU, BTS', 10, 4.00, 600, 0, '2026-03-31 22:13:28'),
(54, 'Iron Man 3', 'FILM', 'Tony Stark must face new threats and save the world while confronting his past and his personal demons.', '2013', '/images/ironman3.jpeg', '/videos/trailer_ironman3.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 130, 'Robert Downey Jr., Gwyneth Paltrow, Guy Pearce', 1, 4.40, 2400, 1, '2026-03-31 22:13:28'),
(55, 'Lego Movie Batman', 'FILM', 'Batman learns that teamwork and friendship are more important than working alone while saving Gotham City.', '2017', '/images/legomoviebatman.jpg', '/videos/trailer_legobatman.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 104, 'Will Arnett, Michael Cera, Rosario Dawson', 2, 4.20, 1152, 0, '2026-03-31 22:13:28'),
(56, 'Barbie as the Island Princess', 'FILM', 'A young princess discovers her destiny, overcomes challenges, and finds friendship and love on a magical island.', '2007', '/images/barbieastheislandprincess.jpeg', '/videos/trailer_barbieisland.mp4', '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 90, 'Kelly Sheridan, Mark Hildreth, Tabitha St. Germain', 10, 4.10, 520, 0, '2026-03-31 22:13:28');

-- --------------------------------------------------------

--
-- Table structure for table `episodes`
--

CREATE TABLE `episodes` (
  `id` int(11) NOT NULL,
  `season_id` int(11) NOT NULL,
  `episode_num` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `synopsis` text DEFAULT NULL,
  `duration_min` int(11) DEFAULT NULL,
  `video_url` varchar(500) DEFAULT NULL,
  `thumbnail_url` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `episodes`
--

INSERT INTO `episodes` (`id`, `season_id`, `episode_num`, `title`, `synopsis`, `duration_min`, `video_url`, `thumbnail_url`) VALUES
(1, 1, 1, 'Pilot', 'Walter White, professeur de chimie, apprend qu\'il a un cancer.', 58, '/videos/shrek4.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg'),
(2, 1, 2, 'Cat\'s in the Bag', 'Walt et Jesse doivent se débarrasser de deux corps.', 48, '/src/main/resources/videos/shrek4.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg'),
(3, 1, 3, 'And the Bag\'s in the River', 'Walt doit décider du sort de Krazy-8.', 48, '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg'),
(4, 2, 1, 'Seven Thirty-Seven', 'Walt et Jesse cherchent 737,000$ pour rembourser Tuco.', 47, '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg'),
(5, 2, 2, 'Grilled', 'Walt et Jesse sont retenus en otage par Tuco.', 47, '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 'https://image.tmdb.org/t/p/w300/ggFHVNu6YYI5L9pCfOacjizRGt.jpg'),
(6, 3, 1, 'The Vanishing of Will Byers', 'Will Byers disparaît mystérieusement.', 47, '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 'https://image.tmdb.org/t/p/w300/49WJfeN0moxb9IPfGn8AIqMGskD.jpg'),
(7, 3, 2, 'The Weirdo on Maple Street', 'Les garçons trouvent une fille mystérieuse nommée Eleven.', 55, '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 'https://image.tmdb.org/t/p/w300/49WJfeN0moxb9IPfGn8AIqMGskD.jpg'),
(8, 4, 1, 'MADMAX', 'Une nouvelle recrue à Hawkins Middle suscite la jalousie d\'Eleven.', 46, '/Users/asus/git/JavaNetflix/JavaNetflixProject/src/main/resources/videos/shrek4.mp4', 'https://image.tmdb.org/t/p/w300/49WJfeN0moxb9IPfGn8AIqMGskD.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `ratings`
--

CREATE TABLE `ratings` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content_id` int(11) NOT NULL,
  `stars` tinyint(4) NOT NULL CHECK (`stars` between 1 and 5),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `ratings`
--

INSERT INTO `ratings` (`id`, `user_id`, `content_id`, `stars`, `created_at`) VALUES
(1, 2, 1, 5, '2026-02-23 14:49:09'),
(2, 2, 2, 5, '2026-02-23 14:49:09'),
(3, 2, 3, 4, '2026-02-23 14:49:09'),
(4, 3, 1, 4, '2026-02-23 14:49:09'),
(5, 3, 4, 5, '2026-02-23 14:49:09'),
(6, 4, 5, 5, '2026-02-23 14:49:09'),
(7, 4, 6, 4, '2026-02-23 14:49:09'),
(8, 5, 7, 5, '2026-02-23 14:49:09'),
(9, 5, 8, 4, '2026-02-23 14:49:09'),
(10, 3, 13, 5, '2026-02-23 14:49:09'),
(11, 4, 14, 5, '2026-02-23 14:49:09'),
(12, 6, 1, 4, '2026-02-23 15:12:02'),
(13, 7, 16, 5, '2026-02-23 15:42:16'),
(14, 8, 16, 5, '2026-03-31 21:22:56'),
(17, 8, 46, 4, '2026-04-08 18:43:56');

-- --------------------------------------------------------

--
-- Table structure for table `seasons`
--

CREATE TABLE `seasons` (
  `id` int(11) NOT NULL,
  `serie_id` int(11) NOT NULL,
  `number` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `seasons`
--

INSERT INTO `seasons` (`id`, `serie_id`, `number`, `title`) VALUES
(1, 13, 1, 'Saison 1'),
(2, 13, 2, 'Saison 2'),
(3, 14, 1, 'Saison 1'),
(4, 14, 2, 'Saison 2');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('USER','ADMIN') DEFAULT 'USER',
  `avatar_url` varchar(500) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `role`, `avatar_url`, `created_at`) VALUES
(1, 'Admin User', 'admin@jstream.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'ADMIN', NULL, '2026-02-23 14:49:09'),
(2, 'Alice Martin', 'alice@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'USER', NULL, '2026-02-23 14:49:09'),
(3, 'Bob Dupont', 'bob@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'USER', NULL, '2026-02-23 14:49:09'),
(4, 'Clara Petit', 'clara@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'USER', NULL, '2026-02-23 14:49:09'),
(5, 'David Moreau', 'david@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRwOxO1rSV.xjm.BopPb.bpIJQR5u', 'USER', NULL, '2026-02-23 14:49:09'),
(6, 'lamis', 'lamis@example.com', '$2a$12$NInSfw9dk3CGw4FQrhundeXABE3kCmgjoPOJDBTGl84JsmrL1uRfe', 'ADMIN', NULL, '2026-02-23 15:09:59'),
(7, 'daze', 'daze@gmail.com', '$2a$12$5LIyXsGesmEYoMsZeZQcgOSAA/G6E2JeTmMgjdx9CEM7pkC12CqPi', 'USER', NULL, '2026-02-23 15:20:53'),
(8, 'ari', 'ari@gmail.com', '$2a$12$0zSEs65QyW4CL8OrfnJMYegCVuNw77ReCQydBfiGl1mj6Z/Mxob8e', 'USER', NULL, '2026-03-30 21:57:35');

-- --------------------------------------------------------

--
-- Table structure for table `watchlist`
--

CREATE TABLE `watchlist` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content_id` int(11) NOT NULL,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `watchlist`
--

INSERT INTO `watchlist` (`id`, `user_id`, `content_id`, `added_at`) VALUES
(1, 2, 3, '2026-02-23 14:49:09'),
(2, 2, 5, '2026-02-23 14:49:09'),
(3, 3, 6, '2026-02-23 14:49:09'),
(4, 4, 13, '2026-02-23 14:49:09'),
(5, 5, 14, '2026-02-23 14:49:09'),
(6, 6, 1, '2026-02-23 15:12:03'),
(7, 7, 16, '2026-02-23 15:42:15'),
(8, 8, 6, '2026-03-31 21:10:38'),
(9, 8, 16, '2026-03-31 21:22:57'),
(12, 8, 37, '2026-04-03 23:34:52'),
(13, 8, 14, '2026-04-03 23:36:05'),
(14, 8, 53, '2026-04-03 23:53:19'),
(15, 8, 55, '2026-04-08 16:59:25'),
(16, 8, 40, '2026-04-08 20:45:32');

-- --------------------------------------------------------

--
-- Table structure for table `watch_history`
--

CREATE TABLE `watch_history` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content_id` int(11) DEFAULT NULL,
  `episode_id` int(11) DEFAULT NULL,
  `progress_sec` int(11) DEFAULT 0,
  `completed` tinyint(1) DEFAULT 0,
  `watched_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `watch_history`
--

INSERT INTO `watch_history` (`id`, `user_id`, `content_id`, `episode_id`, `progress_sec`, `completed`, `watched_at`) VALUES
(1, 2, 13, 1, 3480, 1, '2026-02-23 14:49:09'),
(2, 2, 13, 2, 1200, 0, '2026-02-23 14:49:09'),
(3, 3, 14, 5, 2820, 1, '2026-02-23 14:49:09'),
(4, 3, 14, 6, 0, 0, '2026-02-23 14:49:09'),
(5, 6, 2, NULL, 10, 1, '2026-02-23 15:10:51'),
(6, 6, 14, 6, 10, 0, '2026-02-23 15:11:39'),
(7, 7, 10, NULL, 10, 1, '2026-02-23 15:21:21'),
(8, 7, 14, 6, 10, 0, '2026-02-23 15:27:32'),
(9, 7, 14, 7, 10, 1, '2026-02-23 15:27:46'),
(10, 7, 16, NULL, 10, 1, '2026-02-23 15:42:20'),
(11, 8, 14, 6, 81, 1, '2026-03-31 21:27:37'),
(12, 8, 14, 7, 12, 0, '2026-04-03 23:53:00'),
(13, 8, 16, NULL, 81, 1, '2026-04-03 21:50:20'),
(14, 8, 14, 8, 5, 0, '2026-04-08 16:58:38'),
(15, 8, 46, NULL, 81, 1, '2026-04-08 19:12:50');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `content_id` (`content_id`);

--
-- Indexes for table `content`
--
ALTER TABLE `content`
  ADD PRIMARY KEY (`id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `episodes`
--
ALTER TABLE `episodes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `season_id` (`season_id`);

--
-- Indexes for table `ratings`
--
ALTER TABLE `ratings`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_user_content` (`user_id`,`content_id`),
  ADD KEY `content_id` (`content_id`);

--
-- Indexes for table `seasons`
--
ALTER TABLE `seasons`
  ADD PRIMARY KEY (`id`),
  ADD KEY `serie_id` (`serie_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `watchlist`
--
ALTER TABLE `watchlist`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_watchlist` (`user_id`,`content_id`),
  ADD KEY `content_id` (`content_id`);

--
-- Indexes for table `watch_history`
--
ALTER TABLE `watch_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `content_id` (`content_id`),
  ADD KEY `episode_id` (`episode_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `content`
--
ALTER TABLE `content`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;

--
-- AUTO_INCREMENT for table `episodes`
--
ALTER TABLE `episodes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `ratings`
--
ALTER TABLE `ratings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `seasons`
--
ALTER TABLE `seasons`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `watchlist`
--
ALTER TABLE `watchlist`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `watch_history`
--
ALTER TABLE `watch_history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `comments`
--
ALTER TABLE `comments`
  ADD CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `content`
--
ALTER TABLE `content`
  ADD CONSTRAINT `content_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `episodes`
--
ALTER TABLE `episodes`
  ADD CONSTRAINT `episodes_ibfk_1` FOREIGN KEY (`season_id`) REFERENCES `seasons` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `ratings`
--
ALTER TABLE `ratings`
  ADD CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `ratings_ibfk_2` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `seasons`
--
ALTER TABLE `seasons`
  ADD CONSTRAINT `seasons_ibfk_1` FOREIGN KEY (`serie_id`) REFERENCES `content` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `watchlist`
--
ALTER TABLE `watchlist`
  ADD CONSTRAINT `watchlist_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `watchlist_ibfk_2` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `watch_history`
--
ALTER TABLE `watch_history`
  ADD CONSTRAINT `watch_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `watch_history_ibfk_2` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `watch_history_ibfk_3` FOREIGN KEY (`episode_id`) REFERENCES `episodes` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
