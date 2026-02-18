-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 18, 2026 at 01:23 PM
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
-- Database: `database`
--
CREATE DATABASE IF NOT EXISTS `database` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `database`;

-- --------------------------------------------------------

--
-- Table structure for table `episode`
--

CREATE TABLE `episode` (
  `id` int(11) NOT NULL,
  `season_id` int(11) NOT NULL,
  `episode_number` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `video_url` text NOT NULL,
  `duration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `film`
--

CREATE TABLE `film` (
  `id` int(11) NOT NULL,
  `title` varchar(70) NOT NULL,
  `description` text NOT NULL,
  `genre` enum('action','comedy','drama','fantasy','horror','romance','sci-fi','thriller') NOT NULL,
  `date_sortie` date NOT NULL,
  `video_url` varchar(200) NOT NULL,
  `cover_url` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `film`
--

INSERT INTO `film` (`id`, `title`, `description`, `genre`, `date_sortie`, `video_url`, `cover_url`) VALUES
(1, 'Shrek 4', 'Shrek and friends go on an adventure', 'action', '2010-05-21', '/videos/shrek4.mp4', '/images/shrek4.jpg'),
(2, 'Bumble Bee', 'A young Autobot finds his way', 'action', '2018-12-21', '/videos/bumblebee.mp4', '/images/bumblebee.png'),
(3, 'Kung Fu Panda 4', 'Po returns for more adventures', 'comedy', '2024-06-15', '/videos/kungfupanda4.mp4', '/images/kungfupanda4.jpg'),
(4, 'Five Nights at Freddy\'s 2', 'Horror at Freddy\'s', 'horror', '2023-10-31', '/videos/fnaf2.mp4', '/images/fnaf2.png'),
(6, 'Damsel', 'A princess fights for survival.', 'fantasy', '2024-03-08', '/videos/damsel.mp4', '/images/damsel.jpg'),
(9, 'Free Guy', 'A NPC discovers he is in a game.', 'comedy', '2021-08-13', '/videos/freeguy.mp4', '/images/freeguy.jpg'),
(11, 'Mortal Engines', 'Cities hunt cities.', 'action', '2018-12-14', '/videos/mortalengines.mp4', '/images/mortalengines.png'),
(12, 'Sing', 'Animals compete in a singing contest.', 'comedy', '2016-12-21', '/videos/sing.mp4', '/images/sing.jpg'),
(13, 'Zootopia 2', 'New mystery in Zootopia.', 'comedy', '2025-11-26', '/videos/zootopia2.mp4', '/images/zootopia2.jpg'),
(14, 'Tetris', 'The story behind the iconic game.', 'drama', '2023-03-31', '/videos/tetris.mp4', '/images/tetris.jpg'),
(15, 'Uncharted', 'A treasure hunter adventure.', 'action', '2022-02-18', '/videos/uncharted.mp4', '/images/uncharted.jpg'),
(16, 'Antman', 'A hero who can shrink.', 'action', '2015-07-17', '/videos/antman.mp4', '/images/antman.jpg'),
(17, 'Guardians of the Galaxy', 'Misfit heroes save the galaxy.', 'sci-fi', '2014-08-01', '/videos/guardians.mp4', '/images/guardiansofthegalaxy.jpg'),
(18, 'Spiderman Far From Home', 'Peter travels across Europe.', 'action', '2019-07-02', '/videos/spidermanffh.mp4', '/images/spidermanfarfromhome.jpg'),
(19, 'Spiderman Homecoming', 'Peter balances school and hero life.', 'action', '2017-07-07', '/videos/spidermanhc.mp4', '/images/spidermanhomecoming.jpg'),
(20, 'Spiderman No Way Home', 'Multiverse chaos begins.', 'action', '2021-12-17', '/videos/spidermannwh.mp4', '/images/spidermannowayhome.jpg'),
(21, 'Ready Player One', 'A virtual reality treasure hunt.', '', '2018-03-29', '/videos/readyplayerone.mp4', '/images/readyplayerone.jpg'),
(22, 'KPop Demon Hunters', 'Idols fight demons.', 'fantasy', '2024-01-01', '/videos/kpopdemonhunters.mp4', '/images/kpopdemonhunters.jpg'),
(23, 'Iron Man 3', 'Tony Stark faces new threats.', 'action', '2013-05-03', '/videos/ironman3.mp4', '/images/ironman3.jpeg'),
(24, 'Lego Movie Batman', 'Batman learns teamwork.', 'comedy', '2017-02-10', '/videos/legobatman.mp4', '/images/legomoviebatman.jpg'),
(25, 'Barbie as the Island Princess', 'A princess finds her destiny.', 'fantasy', '2007-09-18', '/videos/barbieisland.mp4', '/images/barbieastheislandprincess.jpeg');

-- --------------------------------------------------------

--
-- Table structure for table `season`
--

CREATE TABLE `season` (
  `id` int(11) NOT NULL,
  `series_id` int(11) NOT NULL,
  `season_number` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `series`
--

CREATE TABLE `series` (
  `id` int(11) NOT NULL,
  `title` varchar(70) NOT NULL,
  `genre` enum('action','comedy','drama','fantasy','horror','romance','sci-fi','thriller') NOT NULL,
  `description` varchar(10000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(35) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(260) NOT NULL,
  `role` enum('USER','ADMIN') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `watch_progress`
--

CREATE TABLE `watch_progress` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `episode_id` int(11) NOT NULL,
  `last_position_seconds` int(11) NOT NULL,
  `completed` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `episode`
--
ALTER TABLE `episode`
  ADD PRIMARY KEY (`id`),
  ADD KEY `season_id` (`season_id`);

--
-- Indexes for table `film`
--
ALTER TABLE `film`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `season`
--
ALTER TABLE `season`
  ADD PRIMARY KEY (`id`),
  ADD KEY `series_id` (`series_id`);

--
-- Indexes for table `series`
--
ALTER TABLE `series`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `watch_progress`
--
ALTER TABLE `watch_progress`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `episode_id` (`episode_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `episode`
--
ALTER TABLE `episode`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `film`
--
ALTER TABLE `film`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `season`
--
ALTER TABLE `season`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `series`
--
ALTER TABLE `series`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `watch_progress`
--
ALTER TABLE `watch_progress`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `episode`
--
ALTER TABLE `episode`
  ADD CONSTRAINT `episode_ibfk_1` FOREIGN KEY (`season_id`) REFERENCES `season` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `season`
--
ALTER TABLE `season`
  ADD CONSTRAINT `season_ibfk_1` FOREIGN KEY (`series_id`) REFERENCES `series` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `watch_progress`
--
ALTER TABLE `watch_progress`
  ADD CONSTRAINT `watch_progress_ibfk_1` FOREIGN KEY (`episode_id`) REFERENCES `episode` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
