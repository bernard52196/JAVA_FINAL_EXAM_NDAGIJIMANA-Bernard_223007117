-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 19, 2025 at 08:44 PM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `healthpo`
--

-- --------------------------------------------------------

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
CREATE TABLE IF NOT EXISTS `appointment` (
  `AppointmentID` int NOT NULL AUTO_INCREMENT,
  `OrderNumber` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `PatientID` int NOT NULL,
  `DoctorID` int NOT NULL,
  `AppointmentDate` datetime DEFAULT NULL,
  `Status` varchar(50) COLLATE utf8mb4_general_ci DEFAULT 'Pending',
  `TotalAmount` decimal(10,2) DEFAULT '0.00',
  `PaymentMethod` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Notes` text COLLATE utf8mb4_general_ci,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AppointmentID`),
  UNIQUE KEY `OrderNumber` (`OrderNumber`),
  KEY `PatientID` (`PatientID`),
  KEY `DoctorID` (`DoctorID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointment`
--

INSERT INTO `appointment` (`AppointmentID`, `OrderNumber`, `PatientID`, `DoctorID`, `AppointmentDate`, `Status`, `TotalAmount`, `PaymentMethod`, `Notes`, `CreatedAt`) VALUES
(3, '4', 2, 3, '2025-12-01 16:00:00', 'Completed', 0.00, '', 'Consltuation', '2025-10-28 17:53:52'),
(4, '3', 3, 2, '2025-12-12 04:00:00', 'Approved', 0.00, '', 'High fever,somache', '2025-10-28 20:58:11'),
(6, '2', 2, 2, '2025-10-30 08:00:00', 'Scheduled', 0.00, 'Cash', 'consultation', '2025-10-31 17:04:39'),
(8, NULL, 1, 3, '2025-12-30 05:00:00', 'Approved', 0.00, NULL, 'Consultation', '2025-11-07 12:43:30');

-- --------------------------------------------------------

--
-- Table structure for table `billing`
--

DROP TABLE IF EXISTS `billing`;
CREATE TABLE IF NOT EXISTS `billing` (
  `BillingID` int NOT NULL AUTO_INCREMENT,
  `PatientID` int DEFAULT NULL,
  `PatientName` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Amount` decimal(10,2) DEFAULT NULL,
  `PaymentMethod` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `MedicalRecordID` int DEFAULT NULL,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`BillingID`),
  KEY `PatientID` (`PatientID`),
  KEY `MedicalRecordID` (`MedicalRecordID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `billing`
--

INSERT INTO `billing` (`BillingID`, `PatientID`, `PatientName`, `Amount`, `PaymentMethod`, `MedicalRecordID`, `CreatedAt`) VALUES
(4, 2, 'Kabebe ISHIMWE MUTUYI', 2700.00, 'Mobile Money', NULL, '2025-10-30 13:55:28'),
(5, 1, 'TUYISHIMIRE Vedast', 5000.00, 'MOMO', NULL, '2025-10-31 16:24:34');

-- --------------------------------------------------------

--
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
CREATE TABLE IF NOT EXISTS `doctor` (
  `DoctorID` int NOT NULL AUTO_INCREMENT,
  `UserID` int NOT NULL,
  `Name` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `Identifier` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Status` varchar(50) COLLATE utf8mb4_general_ci DEFAULT 'Active',
  `Location` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Contact` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `AssignedSince` date DEFAULT NULL,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`DoctorID`),
  KEY `UserID` (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `doctor`
--

INSERT INTO `doctor` (`DoctorID`, `UserID`, `Name`, `Identifier`, `Status`, `Location`, `Contact`, `AssignedSince`, `CreatedAt`) VALUES
(2, 3, 'Adolphe NAYITURIKI', 'Adolphe', 'Active', 'CHUK', 'adolphe@gmail.vom', '2025-10-28', '2025-10-28 17:13:25'),
(3, 4, 'MUYISINGIZEMWESE Evode', 'Evode', 'Active', 'CHUB', 'evode@gmail.com', '2025-10-28', '2025-10-28 17:50:26'),
(7, 9, 'TUYUBAHE Nicodeme', 'Okecho', 'Active', 'FAISAL', 'nicodemetuyubahe7@gmail.com', NULL, '2025-11-01 15:05:51'),
(9, 10, 'Kamanayo Emmanuel', 'kamanayo', 'Active', 'KANOMBE', 'kamanayo@gmail.com', '2025-11-04', '2025-11-04 08:59:32');

-- --------------------------------------------------------

--
-- Table structure for table `medicalrecord`
--

DROP TABLE IF EXISTS `medicalrecord`;
CREATE TABLE IF NOT EXISTS `medicalrecord` (
  `MedicalRecordID` int NOT NULL AUTO_INCREMENT,
  `PatientID` int DEFAULT NULL,
  `Attribute1` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Attribute2` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Attribute3` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MedicalRecordID`),
  KEY `fk_medicalrecord_patient` (`PatientID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `medicalrecord`
--

INSERT INTO `medicalrecord` (`MedicalRecordID`, `PatientID`, `Attribute1`, `Attribute2`, `Attribute3`, `CreatedAt`) VALUES
(9, 2, 'Malaria', 'Positive', 'High fever, chills; treatment recommended', '2025-11-06 15:56:25'),
(12, 1, 'Typhoid Infection', 'Positive', 'Patient shows abdominal pain and fatigue', '2025-11-15 15:57:21');

-- --------------------------------------------------------

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
CREATE TABLE IF NOT EXISTS `patient` (
  `PatientID` int NOT NULL AUTO_INCREMENT,
  `UserID` int NOT NULL,
  `Gender` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `DOB` date DEFAULT NULL,
  `FullName` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`PatientID`),
  KEY `UserID` (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `patient`
--

INSERT INTO `patient` (`PatientID`, `UserID`, `Gender`, `DOB`, `FullName`, `CreatedAt`) VALUES
(1, 2, 'Male', '2003-10-02', 'Vedaste TUYISHIMIRE', '2025-10-28 17:10:08'),
(2, 5, 'Male', '2009-10-10', 'Kabebe ISHIMWE MUTUYI', '2025-10-28 17:52:31'),
(3, 6, 'Male', '2003-10-10', 'UWIMANA Ngabo', '2025-10-28 20:56:21'),
(9, 7, 'Male', '2000-01-01', 'NIYOMUGABO Eric', '2025-10-31 17:19:03'),
(10, 8, 'Male', '2000-10-12', 'KAMANA Egide', '2025-11-01 14:29:59');

-- --------------------------------------------------------

--
-- Table structure for table `prescription`
--

DROP TABLE IF EXISTS `prescription`;
CREATE TABLE IF NOT EXISTS `prescription` (
  `PrescriptionID` int NOT NULL AUTO_INCREMENT,
  `AppointmentID` int DEFAULT NULL,
  `Attribute1` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Attribute2` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Attribute3` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`PrescriptionID`),
  KEY `AppointmentID` (`AppointmentID`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prescription`
--

INSERT INTO `prescription` (`PrescriptionID`, `AppointmentID`, `Attribute1`, `Attribute2`, `Attribute3`, `CreatedAt`) VALUES
(18, 3, 'Artemisinin-based Combination Therapy (ACT)', '1 dose twice daily for 3 days', 'Take after meals; drink plenty of water', '2025-11-15 15:55:39'),
(19, 8, 'Ciprofloxacin 500mg', 'Twice daily for 7 days', 'Avoid spicy food; take with a full glass of water', '2025-11-15 16:00:26');

-- --------------------------------------------------------

--
-- Table structure for table `prescription_medicalrecord`
--

DROP TABLE IF EXISTS `prescription_medicalrecord`;
CREATE TABLE IF NOT EXISTS `prescription_medicalrecord` (
  `PrescriptionID` int NOT NULL,
  `MedicalRecordID` int NOT NULL,
  PRIMARY KEY (`PrescriptionID`,`MedicalRecordID`),
  KEY `MedicalRecordID` (`MedicalRecordID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `system_settings`
--

DROP TABLE IF EXISTS `system_settings`;
CREATE TABLE IF NOT EXISTS `system_settings` (
  `SettingKey` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `SettingValue` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`SettingKey`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `system_settings`
--

INSERT INTO `system_settings` (`SettingKey`, `SettingValue`) VALUES
('DoctorCode', 'DOC01');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `UserID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `PasswordHash` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `Email` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `FullName` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Role` enum('Admin','Doctor','Patient') COLLATE utf8mb4_general_ci NOT NULL,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastLogin` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`UserID`, `Username`, `PasswordHash`, `Email`, `FullName`, `Role`, `CreatedAt`, `LastLogin`) VALUES
(1, 'Bernard', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@healthpo.com', 'System Administrator', 'Admin', '2025-10-28 16:59:56', '2025-12-13 12:00:22'),
(2, 'Veda', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'veda@gmail.com', 'Vedaste TUYISHIMIRE', 'Patient', '2025-10-28 17:10:08', '2025-12-11 13:34:36'),
(3, 'Adolphe', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'adolphe@gmail.vom', 'Adolphe NAYITURIKI', 'Doctor', '2025-10-28 17:13:25', '2025-11-04 09:11:49'),
(4, 'Evode', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'evode@gmail.com', 'MUYISINGIZEMWESE Evode', 'Doctor', '2025-10-28 17:50:26', '2025-12-13 12:28:14'),
(5, 'Kabebe', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'kabebe@gmail.com', 'Kabebe ISHIMWE MUTUYI', 'Patient', '2025-10-28 17:52:31', '2025-12-13 12:43:55'),
(6, 'Ngabo', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'ngabo@gmail.com', 'UWIMANA Ngabo', 'Patient', '2025-10-28 20:56:21', '2025-10-28 20:56:43'),
(7, 'Eric', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'eric@gmail.com', 'NIYOMUGABO Eric', 'Patient', '2025-10-31 17:19:03', NULL),
(8, 'Kamana', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'kamana@gmail.com', 'KAMANA Egide', 'Patient', '2025-11-01 14:29:59', '2025-11-01 14:30:19'),
(9, 'Okecho', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'nicodemetuyubahe7@gmail.com', 'TUYUBAHE Nicodeme', 'Doctor', '2025-11-01 15:05:51', NULL),
(10, 'Kamanayo', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'kamanayo@gmail.com', 'Kamanayo Emmanuel', 'Doctor', '2025-11-04 08:59:32', NULL);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `appointment`
--
ALTER TABLE `appointment`
  ADD CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`PatientID`) REFERENCES `patient` (`PatientID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`DoctorID`) REFERENCES `doctor` (`DoctorID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `billing`
--
ALTER TABLE `billing`
  ADD CONSTRAINT `billing_ibfk_1` FOREIGN KEY (`PatientID`) REFERENCES `patient` (`PatientID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `billing_ibfk_2` FOREIGN KEY (`MedicalRecordID`) REFERENCES `medicalrecord` (`MedicalRecordID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `doctor`
--
ALTER TABLE `doctor`
  ADD CONSTRAINT `doctor_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `user` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `medicalrecord`
--
ALTER TABLE `medicalrecord`
  ADD CONSTRAINT `fk_medicalrecord_patient` FOREIGN KEY (`PatientID`) REFERENCES `patient` (`PatientID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `patient`
--
ALTER TABLE `patient`
  ADD CONSTRAINT `patient_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `user` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `prescription`
--
ALTER TABLE `prescription`
  ADD CONSTRAINT `prescription_ibfk_1` FOREIGN KEY (`AppointmentID`) REFERENCES `appointment` (`AppointmentID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `prescription_medicalrecord`
--
ALTER TABLE `prescription_medicalrecord`
  ADD CONSTRAINT `prescription_medicalrecord_ibfk_1` FOREIGN KEY (`PrescriptionID`) REFERENCES `prescription` (`PrescriptionID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `prescription_medicalrecord_ibfk_2` FOREIGN KEY (`MedicalRecordID`) REFERENCES `medicalrecord` (`MedicalRecordID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
