package com.inventaris.inventory.repository;

import com.inventaris.core.util.DatabaseConnection;
import com.inventaris.inventory.domain.Barang;

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
        String sql = "SELECT id, nama, id_kategori, stok FROM barang";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Barang b = new Barang(
                        rs.getString("id"),
                        rs.getString("nama"),
                        rs.getString("id_kategori"),
                        rs.getInt("stok")
                );
                list.add(b);
            }
        }
        return list;
    }

    public List<Barang> findByNameLike(String name) throws SQLException {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT id, nama, id_kategori, stok FROM barang WHERE nama LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Barang b = new Barang(
                            rs.getString("id"),
                            rs.getString("nama"),
                            rs.getString("id_kategori"),
                            rs.getInt("stok")
                    );
                    list.add(b);
                }
            }
        }
        return list;
    }

    public Optional<Barang> findById(String id) throws SQLException {
        String sql = "SELECT id, nama, id_kategori, stok FROM barang WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Barang b = new Barang(
                            rs.getString("id"),
                            rs.getString("nama"),
                            rs.getString("id_kategori"),
                            rs.getInt("stok")
                    );
                    return Optional.of(b);
                }
            }
        }
        return Optional.empty();
    }
}
