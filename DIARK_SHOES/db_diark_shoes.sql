-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Jul 02, 2025 at 10:05 PM
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
-- Database: `db_diark_shoes`
--

-- --------------------------------------------------------

--
-- Table structure for table `data_admin`
--

CREATE TABLE `data_admin` (
  `id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(3000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `data_admin`
--

INSERT INTO `data_admin` (`id`, `username`, `password`) VALUES
(1, 'admin', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3');

-- --------------------------------------------------------

--
-- Table structure for table `data_sepatu`
--

CREATE TABLE `data_sepatu` (
  `Kode_sepatu` varchar(50) NOT NULL DEFAULT current_timestamp(),
  `Nama_sepatu` varchar(50) NOT NULL,
  `Stok` int(11) NOT NULL,
  `Tipe` text NOT NULL,
  `Harga` int(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `data_sepatu`
--

INSERT INTO `data_sepatu` (`Kode_sepatu`, `Nama_sepatu`, `Stok`, `Tipe`, `Harga`) VALUES
('SP001', 'Vans', 18, 'Skate', 180000),
('SP002', 'Adidas', 19, 'Casual', 200000),
('SP003', 'Onitsuka', 19, 'Skate', 150000),
('SP004', 'Docmart', 20, 'Formal', 190000),
('SP005', 'Nike', 20, 'Sport', 170000),
('SP006', 'Kodachi', 20, 'Casual', 160000),
('SP007', 'Mizuno', 20, 'Sport', 185000),
('SP008', 'Ortus Seight', 20, 'Sport', 175000);

-- --------------------------------------------------------

--
-- Table structure for table `detail_barang`
--

CREATE TABLE `detail_barang` (
  `Kode_Transaksi` varchar(50) NOT NULL,
  `Kode_Detail` varchar(50) NOT NULL,
  `Kode_sepatu` varchar(50) NOT NULL,
  `Nama_sepatu` varchar(50) NOT NULL,
  `Harga` int(11) NOT NULL,
  `Jumlah` int(11) NOT NULL,
  `Discount` int(11) DEFAULT 0,
  `Subtotal` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `struk_pembelian`
--

CREATE TABLE `struk_pembelian` (
  `Kode_Transaksi` varchar(50) NOT NULL,
  `Jam` time NOT NULL,
  `Tanggal` date NOT NULL,
  `Total` int(20) NOT NULL,
  `Kembalian` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `data_admin`
--
ALTER TABLE `data_admin`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `data_sepatu`
--
ALTER TABLE `data_sepatu`
  ADD PRIMARY KEY (`Kode_sepatu`);

--
-- Indexes for table `detail_barang`
--
ALTER TABLE `detail_barang`
  ADD PRIMARY KEY (`Kode_Detail`),
  ADD KEY `Kode_Transaksi` (`Kode_Transaksi`);

--
-- Indexes for table `struk_pembelian`
--
ALTER TABLE `struk_pembelian`
  ADD PRIMARY KEY (`Kode_Transaksi`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `data_admin`
--
ALTER TABLE `data_admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detail_barang`
--
ALTER TABLE `detail_barang`
  ADD CONSTRAINT `detail_barang_ibfk_1` FOREIGN KEY (`Kode_Transaksi`) REFERENCES `struk_pembelian` (`Kode_Transaksi`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
