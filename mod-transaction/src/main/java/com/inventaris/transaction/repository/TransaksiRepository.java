package com.inventaris.transaction.repository;

import com.inventaris.core.util.DatabaseConnection;
import com.inventaris.auth.domain.User;
import com.inventaris.auth.domain.Role;
import com.inventaris.auth.domain.Admin;
import com.inventaris.auth.domain.Staff;
import com.inventaris.inventory.domain.Barang;
import com.inventaris.inventory.domain.Kategori;
import com.inventaris.transaction.domain.TipeTransaksi;
import com.inventaris.transaction.domain.Transaksi;
import com.inventaris.transaction.domain.BarangMasuk;
import com.inventaris.transaction.domain.BarangKeluar;

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
        String sql = "SELECT t.id, t.jumlah, t.tipe, t.created_at, t.keterangan, " +
                     "b.id AS id_barang, b.nama AS nama_barang, b.stok AS stok_barang, " +
                     "k.id AS id_kategori, k.nama AS nama_kategori, " +
                     "u.id AS id_user, u.name AS nama_user, u.password AS pwd_user, u.role AS role_user " +
                     "FROM transaksi t " +
                     "INNER JOIN barang b ON t.id_barang = b.id " +
                     "INNER JOIN kategori k ON b.id_kategori = k.id " +
                     "INNER JOIN users u ON t.created_by = u.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("created_at");
                LocalDateTime ldt = ts != null ? ts.toLocalDateTime() : null;

                // Load User
                Role role = Role.valueOf(rs.getString("role_user").toUpperCase());
                User user;
                if (role == Role.ADMIN) {
                    user = new Admin(rs.getString("nama_user"), rs.getString("pwd_user"));
                } else {
                    user = new Staff(rs.getString("nama_user"), rs.getString("pwd_user"));
                }
                user.setId(rs.getString("id_user"));

                // Load Kategori & Barang
                Kategori kat = new Kategori(rs.getString("id_kategori"), rs.getString("nama_kategori"));
                Barang barang = new Barang(rs.getString("id_barang"), rs.getString("nama_barang"), kat, rs.getInt("stok_barang"));

                // Load Transaksi Subclass
                TipeTransaksi tipe = TipeTransaksi.valueOf(rs.getString("tipe").toUpperCase());
                Transaksi t;
                if (tipe == TipeTransaksi.MASUK) {
                    t = new BarangMasuk(
                            rs.getString("id"),
                            barang,
                            rs.getInt("jumlah"),
                            user,
                            ldt,
                            rs.getString("keterangan")
                    );
                } else {
                    t = new BarangKeluar(
                            rs.getString("id"),
                            barang,
                            rs.getInt("jumlah"),
                            user,
                            ldt,
                            rs.getString("keterangan")
                    );
                }
                list.add(t);
            }
        }
        return list;
    }

    public void save(Transaksi transaksi) throws SQLException {
        String sql = "INSERT INTO transaksi (id, id_barang, jumlah, tipe, created_by, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transaksi.getId());
            ps.setString(2, transaksi.getBarang().getId());
            ps.setInt(3, transaksi.getJumlah());
            ps.setString(4, transaksi.getTipeTransaksi().name().toLowerCase());
            ps.setString(5, transaksi.getUser().getId());
            ps.setString(6, transaksi.getKeterangan());
            ps.executeUpdate();
        }
    }
}
