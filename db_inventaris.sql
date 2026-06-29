-- Create database if not exists
CREATE DATABASE IF NOT EXISTS db_inventaris;
USE db_inventaris;

-- Table structure for table `kategori`
CREATE TABLE IF NOT EXISTS `kategori` (
    `id` varchar(36) NOT NULL DEFAULT uuid(),
    `nama` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table structure for table `barang`
CREATE TABLE IF NOT EXISTS `barang` (
    `id` varchar(36) NOT NULL DEFAULT uuid(),
    `nama` varchar(255) NOT NULL,
    `id_kategori` varchar(36) NOT NULL,
    `stok` int(11) NOT NULL DEFAULT 0,
    `deskripsi` text DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_barang_kategori` (`id_kategori`),
    CONSTRAINT `fk_barang_kategori` FOREIGN KEY (`id_kategori`) REFERENCES `kategori` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table structure for table `users`
CREATE TABLE IF NOT EXISTS `users` (
    `id` varchar(36) NOT NULL,
    `name` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `role` enum('ADMIN','STAFF') NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table structure for table `transaksi`
CREATE TABLE IF NOT EXISTS `transaksi` (
    `id` varchar(36) NOT NULL DEFAULT uuid(),
    `id_barang` varchar(36) NOT NULL,
    `jumlah` int(11) NOT NULL DEFAULT 1,
    `tipe` enum('masuk','keluar') NOT NULL,
    `created_by` varchar(36) NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
    `keterangan` text DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_transaksi_barang` (`id_barang`),
    KEY `fk_transaksi_users` (`created_by`),
    CONSTRAINT `fk_transaksi_barang` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id`) ON UPDATE CASCADE,
    CONSTRAINT `fk_transaksi_users` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table `users`
INSERT INTO `users` (`id`, `name`, `password`, `role`) VALUES
('9a6fb44a-6fe6-11f1-aefe-2c1b3ae3ac30', 'admin', 'admin123', 'ADMIN'),
('9a6fca31-6fe6-11f1-aefe-2c1b3ae3ac30', 'staff', 'staff123', 'STAFF')
ON DUPLICATE KEY UPDATE id=id;

-- Dumping data for table `kategori`
INSERT INTO `kategori` (`id`, `nama`) VALUES
('e23a41a6-281b-4b11-97b6-c67b36f7de10', 'Elektronik'),
('f34b52b7-392c-4c22-a8c7-d78c47f8ef21', 'Perabot'),
('045c63c8-4a3d-4d33-b9d8-e89d58f9f032', 'Aksesoris')
ON DUPLICATE KEY UPDATE id=id;

-- Dumping data for table `barang`
INSERT INTO `barang` (`id`, `nama`, `id_kategori`, `stok`, `deskripsi`) VALUES
('156d74d9-5b4e-4e44-cae9-f9ae69fa0143', 'Laptop Pro X', 'e23a41a6-281b-4b11-97b6-c67b36f7de10', 45, 'Laptop performa tinggi untuk kebutuhan programming dan komputasi berat.'),
('267e85ea-6c5f-4f55-dbfa-0abf7afb1254', 'Monitor 27" 4K', 'e23a41a6-281b-4b11-97b6-c67b36f7de10', 2, 'Monitor resolusi tinggi 27 inch 4K UHD, akurasi warna tinggi untuk editing.'),
('378f96fb-7d60-4066-ec0b-1bcd8bcd2365', 'Ergonomic Chair', 'f34b52b7-392c-4c22-a8c7-d78c47f8ef21', 12, 'Kursi kerja ergonomis dengan penyangga punggung dan tangan yang dapat diatur.'),
('4890a7fc-8e71-4177-fd1c-2cde9cde3476', 'USB-C Hub', '045c63c8-4a3d-4d33-b9d8-e89d58f9f032', 3, 'Hub multi-port USB Type-C ke HDMI, USB 3.0, dan pembaca kartu memori.')
ON DUPLICATE KEY UPDATE id=id;

-- Dumping data for table `transaksi`
INSERT INTO `transaksi` (`id`, `id_barang`, `jumlah`, `tipe`, `created_by`, `created_at`, `keterangan`) VALUES
('59a1b80d-9f82-4288-0e2d-3defadef4587', '156d74d9-5b4e-4e44-cae9-f9ae69fa0143', 5, 'masuk', '9a6fb44a-6fe6-11f1-aefe-2c1b3ae3ac30', NOW(), 'Stok awal laptop pro x'),
('6ab2c91e-0a93-4399-1f3e-4ef0bef05698', '267e85ea-6c5f-4f55-dbfa-0abf7afb1254', 2, 'masuk', '9a6fb44a-6fe6-11f1-aefe-2c1b3ae3ac30', NOW(), 'Stok awal monitor 27 4k')
ON DUPLICATE KEY UPDATE id=id;
