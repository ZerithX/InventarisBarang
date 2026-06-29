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
        String sql = "SELECT b.id, b.nama, b.id_kategori, k.nama AS nama_kategori, b.stok " +
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
                        rs.getInt("stok")
                );
                list.add(b);
            }
        }
        return list;
    }

    public List<Barang> findByNameLike(String name) throws SQLException {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT b.id, b.nama, b.id_kategori, k.nama AS nama_kategori, b.stok " +
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
                            rs.getInt("stok")
                    );
                    list.add(b);
                }
            }
        }
        return list;
    }

    public Optional<Barang> findById(String id) throws SQLException {
        String sql = "SELECT b.id, b.nama, b.id_kategori, k.nama AS nama_kategori, b.stok " +
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
                            rs.getInt("stok")
                    );
                    return Optional.of(b);
                }
            }
        }
        return Optional.empty();
    }
}
