package com.inventaris.transaction.repository;

import com.inventaris.core.util.DatabaseConnection;
import com.inventaris.transaction.domain.TipeTransaksi;
import com.inventaris.transaction.domain.Transaksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransaksiRepository {

    public int countToday() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transaksi WHERE DATE(created_at) = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Transaksi> findAll() throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT id, id_barang, jumlah, tipe, created_by, created_at, keterangan FROM transaksi";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("created_at");
                LocalDateTime ldt = ts != null ? ts.toLocalDateTime() : null;

                Transaksi t = new Transaksi(
                        rs.getString("id"),
                        rs.getString("id_barang"),
                        rs.getInt("jumlah"),
                        TipeTransaksi.valueOf(rs.getString("tipe").toUpperCase()),
                        rs.getString("created_by"),
                        ldt,
                        rs.getString("keterangan")
                );
                list.add(t);
            }
        }
        return list;
    }
}
