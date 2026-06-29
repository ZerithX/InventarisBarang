package com.inventaris.inventory.repository;

import com.inventaris.core.util.DatabaseConnection;
import com.inventaris.inventory.domain.Kategori;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KategoriRepository {

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM kategori";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Kategori> findAll() throws SQLException {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT id, nama FROM kategori";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Kategori kat = new Kategori(
                        rs.getString("id"),
                        rs.getString("nama")
                );
                list.add(kat);
            }
        }
        return list;
    }

    public Optional<Kategori> findById(String id) throws SQLException {
        String sql = "SELECT id, nama FROM kategori WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Kategori kat = new Kategori(
                            rs.getString("id"),
                            rs.getString("nama")
                    );
                    return Optional.of(kat);
                }
            }
        }
        return Optional.empty();
    }

    public void save(Kategori kat) throws SQLException {
        String sql = "INSERT INTO kategori (id, nama) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kat.getId());
            ps.setString(2, kat.getNama());
            ps.executeUpdate();
        }
    }

    public void update(Kategori kat) throws SQLException {
        String sql = "UPDATE kategori SET nama = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kat.getNama());
            ps.setString(2, kat.getId());
            ps.executeUpdate();
        }
    }
}
