-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 15, 2026 at 11:04 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vcba_inventory`
--

-- --------------------------------------------------------

--
-- Table structure for table `borrow_history`
--

CREATE TABLE `borrow_history` (
  `history_id` int(11) NOT NULL,
  `borrower_name` varchar(30) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `date_borrowed` date DEFAULT NULL,
  `date_to_return` date DEFAULT NULL,
  `date_returned` date DEFAULT NULL,
  `received_by` varchar(30) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `id` int(11) NOT NULL,
  `item_name` varchar(30) NOT NULL,
  `category` varchar(20) DEFAULT NULL,
  `quantity` int(11) DEFAULT 0,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `supplier` varchar(30) DEFAULT NULL,
  `date_added` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `monitoring`
--

CREATE TABLE `monitoring` (
  `borrow_id` int(11) NOT NULL,
  `borrower_name` varchar(30) NOT NULL,
  `item_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `date_borrowed` date NOT NULL,
  `date_to_return` date NOT NULL,
  `date_returned` date DEFAULT NULL,
  `received_by` varchar(30) DEFAULT NULL,
  `status` varchar(10) GENERATED ALWAYS AS (case when `date_returned` is not null then 'RETURNED' when curdate() > `date_to_return` and `date_returned` is null then 'OVERDUE' else 'ONGOING' end) VIRTUAL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stock_in_history`
--

CREATE TABLE `stock_in_history` (
  `id` int(11) NOT NULL,
  `item_id` int(11) DEFAULT NULL,
  `item_name` varchar(30) DEFAULT NULL,
  `quantity_before` int(11) DEFAULT NULL,
  `quantity_added` int(11) DEFAULT NULL,
  `quantity_total` int(11) DEFAULT NULL,
  `received_by` varchar(30) DEFAULT NULL,
  `transaction_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stock_out_history`
--

CREATE TABLE `stock_out_history` (
  `id` int(11) NOT NULL,
  `item_id` int(11) DEFAULT NULL,
  `item_name` varchar(30) DEFAULT NULL,
  `quantity_before` int(11) DEFAULT NULL,
  `quantity_removed` int(11) DEFAULT NULL,
  `quantity_total` int(11) DEFAULT NULL,
  `department` varchar(30) DEFAULT NULL,
  `transaction_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `pass` varchar(25) NOT NULL,
  `role` varchar(20) DEFAULT 'STAFF',
  `fullname` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `borrow_history`
--
ALTER TABLE `borrow_history`
  ADD PRIMARY KEY (`history_id`);

--
-- Indexes for table `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `monitoring`
--
ALTER TABLE `monitoring`
  ADD PRIMARY KEY (`borrow_id`),
  ADD KEY `fk_item` (`item_id`);

--
-- Indexes for table `stock_in_history`
--
ALTER TABLE `stock_in_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `stock_out_history`
--
ALTER TABLE `stock_out_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `borrow_history`
--
ALTER TABLE `borrow_history`
  MODIFY `history_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `items`
--
ALTER TABLE `items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `monitoring`
--
ALTER TABLE `monitoring`
  MODIFY `borrow_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `stock_in_history`
--
ALTER TABLE `stock_in_history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `stock_out_history`
--
ALTER TABLE `stock_out_history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `monitoring`
--
ALTER TABLE `monitoring`
  ADD CONSTRAINT `fk_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `stock_in_history`
--
ALTER TABLE `stock_in_history`
  ADD CONSTRAINT `stock_in_history_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `stock_out_history`
--
ALTER TABLE `stock_out_history`
  ADD CONSTRAINT `stock_out_history_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
