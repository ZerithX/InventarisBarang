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
           .append("COALESCE(SUM(CASE WHEN t.tipe = 'masuk' THEN t.jumlah ELSE 0 END), 0) AS total_masuk, ")
           .append("COALESCE(SUM(CASE WHEN t.tipe = 'keluar' THEN t.jumlah ELSE 0 END), 0) AS total_keluar ")
           .append("FROM barang b ")
           .append("INNER JOIN kategori k ON b.id_kategori = k.id ")
           .append("LEFT JOIN transaksi t ON b.id = t.id_barang AND YEAR(t.created_at) = ? AND MONTH(t.created_at) = ? ");
        
        boolean hasCategoryFilter = kategoriId != null && !kategoriId.isEmpty() && !kategoriId.equalsIgnoreCase("ALL");
        if (hasCategoryFilter) {
            sql.append("WHERE b.id_kategori = ? ");
        }
        
        sql.append("GROUP BY b.id, b.nama, b.stok, k.nama ")
           .append("ORDER BY b.nama ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            ps.setInt(1, tahun);
            ps.setInt(2, bulan);
            if (hasCategoryFilter) {
                ps.setString(3, kategoriId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LaporanItem item = new LaporanItem(
                        rs.getString("id"),
                        rs.getString("nama"),
                        rs.getString("nama_kategori"),
                        rs.getInt("total_masuk"),
                        rs.getInt("total_keluar"),
                        rs.getInt("stok")
                    );
                    list.add(item);
                }
            }
        }
        return list;
    }
}
