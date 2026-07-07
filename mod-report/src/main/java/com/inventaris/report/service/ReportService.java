package com.inventaris.report.service;

import com.inventaris.core.util.DatabaseConnection;
import com.inventaris.report.domain.LaporanItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    /**
     * Mengambil laporan perkembangan stok barang (stok awal, masuk, keluar, stok akhir)
     * untuk bulan dan tahun tertentu, opsional difilter berdasarkan kategori.
     */
    public List<LaporanItem> getLaporanPerkembanganStok(String kategoriId, int tahun, int bulan) throws SQLException {
        List<LaporanItem> list = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.id, b.nama, b.stok, k.nama AS nama_kategori, ")
           // Stok Awal = b.stok - (Semua Masuk >= Target) + (Semua Keluar >= Target)
           .append("b.stok ")
           .append("- COALESCE(SUM(CASE WHEN DATE(t.created_at) >= ? AND t.tipe = 'masuk' THEN t.jumlah ELSE 0 END), 0) ")
           .append("+ COALESCE(SUM(CASE WHEN DATE(t.created_at) >= ? AND t.tipe = 'keluar' THEN t.jumlah ELSE 0 END), 0) AS stok_awal, ")
           // Transaksi Bulan Ini
           .append("COALESCE(SUM(CASE WHEN YEAR(t.created_at) = ? AND MONTH(t.created_at) = ? AND t.tipe = 'masuk' THEN t.jumlah ELSE 0 END), 0) AS total_masuk, ")
           .append("COALESCE(SUM(CASE WHEN YEAR(t.created_at) = ? AND MONTH(t.created_at) = ? AND t.tipe = 'keluar' THEN t.jumlah ELSE 0 END), 0) AS total_keluar ")
           .append("FROM barang b ")
           .append("INNER JOIN kategori k ON b.id_kategori = k.id ")
           .append("LEFT JOIN transaksi t ON b.id = t.id_barang ");
        
        boolean hasCategoryFilter = kategoriId != null && !kategoriId.isEmpty() && !kategoriId.equalsIgnoreCase("ALL");
        if (hasCategoryFilter) {
            sql.append("WHERE b.id_kategori = ? ");
        }
        
        sql.append("GROUP BY b.id, b.nama, b.stok, k.nama ")
           .append("ORDER BY b.nama ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            String targetDate = String.format("%04d-%02d-01", tahun, bulan);
            ps.setString(1, targetDate); // Untuk perhitungan stok_awal (masuk)
            ps.setString(2, targetDate); // Untuk perhitungan stok_awal (keluar)
            ps.setInt(3, tahun);
            ps.setInt(4, bulan);
            ps.setInt(5, tahun);
            ps.setInt(6, bulan);
            if (hasCategoryFilter) {
                ps.setString(7, kategoriId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LaporanItem item = new LaporanItem(
                        rs.getString("id"),
                        rs.getString("nama"),
                        rs.getString("nama_kategori"),
                        rs.getInt("stok_awal"),
                        rs.getInt("total_masuk"),
                        rs.getInt("total_keluar")
                    );
                    list.add(item);
                }
            }
        }
        return list;
    }
}
