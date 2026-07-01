package com.inventaris.inventory.repository;

import com.inventaris.core.util.DatabaseConnection;
import com.inventaris.inventory.domain.Barang;
import com.inventaris.inventory.domain.Kategori;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BarangRepository {

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM barang";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countLowStock(int threshold) throws SQLException {
        String sql = "SELECT COUNT(*) FROM barang WHERE stok < ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<Barang> findAll() throws SQLException {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT b.id, b.nama, b.id_kategori, k.nama AS nama_kategori, b.stok, b.deskripsi " +
                     "FROM barang b " +
                     "INNER JOIN kategori k ON b.id_kategori = k.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Kategori kat = new Kategori(
                        rs.getString("id_kategori"),
                        rs.getString("nama_kategori")
                );
                Barang b = new Barang(
                        rs.getString("id"),
                        rs.getString("nama"),
                        kat,
                        rs.getInt("stok"),
                        rs.getString("deskripsi")
                );
                list.add(b);
            }
        }
        return list;
    }

    public List<Barang> findByNameLike(String name) throws SQLException {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT b.id, b.nama, b.id_kategori, k.nama AS nama_kategori, b.stok, b.deskripsi " +
                     "FROM barang b " +
                     "INNER JOIN kategori k ON b.id_kategori = k.id " +
                     "WHERE b.nama LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Kategori kat = new Kategori(
                            rs.getString("id_kategori"),
                            rs.getString("nama_kategori")
                    );
                    Barang b = new Barang(
                            rs.getString("id"),
                            rs.getString("nama"),
                            kat,
                            rs.getInt("stok"),
                            rs.getString("deskripsi")
                    );
                    list.add(b);
                }
            }
        }
        return list;
    }

    public Optional<Barang> findById(String id) throws SQLException {
        String sql = "SELECT b.id, b.nama, b.id_kategori, k.nama AS nama_kategori, b.stok, b.deskripsi " +
                     "FROM barang b " +
                     "INNER JOIN kategori k ON b.id_kategori = k.id " +
                     "WHERE b.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Kategori kat = new Kategori(
                            rs.getString("id_kategori"),
                            rs.getString("nama_kategori")
                    );
                    Barang b = new Barang(
                            rs.getString("id"),
                            rs.getString("nama"),
                            kat,
                            rs.getInt("stok"),
                            rs.getString("deskripsi")
                    );
                    return Optional.of(b);
                }
            }
        }
        return Optional.empty();
    }

    public void updateStok(String id, int newStok) throws SQLException {
        String sql = "UPDATE barang SET stok = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStok);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    public void save(Barang b) throws SQLException {
        String sql = "INSERT INTO barang (id, nama, id_kategori, stok, deskripsi) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getId());
            ps.setString(2, b.getNama());
            ps.setString(3, b.getKategori() != null ? b.getKategori().getId() : null);
            ps.setInt(4, b.getStok());
            ps.setString(5, b.getDeskripsi());
            ps.executeUpdate();
        }
    }

    public void update(Barang b) throws SQLException {
        String sql = "UPDATE barang SET nama = ?, id_kategori = ?, stok = ?, deskripsi = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getNama());
            ps.setString(2, b.getKategori() != null ? b.getKategori().getId() : null);
            ps.setInt(3, b.getStok());
            ps.setString(4, b.getDeskripsi());
            ps.setString(5, b.getId());
            ps.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sqlDeleteTransactions = "DELETE FROM transaksi WHERE id_barang = ?";
        String sqlDeleteBarang = "DELETE FROM barang WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Mulai transaksi database
            try (PreparedStatement psTrans = conn.prepareStatement(sqlDeleteTransactions);
                 PreparedStatement psBarang = conn.prepareStatement(sqlDeleteBarang)) {
                
                // 1. Hapus riwayat transaksi barang
                psTrans.setString(1, id);
                psTrans.executeUpdate();
                
                // 2. Hapus barang
                psBarang.setString(1, id);
                psBarang.executeUpdate();
                
                conn.commit(); // Commit transaksi jika keduanya sukses
            } catch (SQLException ex) {
                conn.rollback(); // Rollback jika ada yang gagal
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
