package com.inventaris.transaction.service;

import com.inventaris.inventory.repository.BarangRepository;
import com.inventaris.transaction.domain.Transaksi;
import com.inventaris.transaction.repository.TransaksiRepository;

import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private final TransaksiRepository transaksiRepository;
    private final BarangRepository barangRepository;

    public TransactionService(TransaksiRepository transaksiRepository, BarangRepository barangRepository) {
        this.transaksiRepository = transaksiRepository;
        this.barangRepository = barangRepository;
    }

    public int getTransactionsCountToday() throws SQLException {
        return transaksiRepository.countToday();
    }

    public List<Transaksi> getAllTransactions() throws SQLException {
        return transaksiRepository.findAll();
    }

    /**
     * Mengeksekusi mutasi transaksi (BarangMasuk / BarangKeluar) secara polimorfik,
     * memperbarui stok di database, dan menyimpan log transaksi.
     */
    public void executeTransaction(Transaksi transaksi) throws Exception {
        // 1. Eksekusi mutasi stok di level objek (melempar StockException jika tidak valid)
        transaksi.prosesStok();

        try (java.sql.Connection conn = com.inventaris.core.util.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Mulai transaksi database
            try {
                // 2. Simpan data transaksi baru ke database
                transaksiRepository.save(conn, transaksi);

                // 3. Sinkronisasikan stok barang terbaru ke database
                barangRepository.updateStok(conn, transaksi.getBarang().getId(), transaksi.getBarang().getStok());
                
                conn.commit(); // Commit transaksi jika keduanya sukses
            } catch (Exception ex) {
                conn.rollback(); // Rollback jika ada yang gagal
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Transaksi> getTransactionsByUser(String userId) throws SQLException {
        return transaksiRepository.findByUserId(userId);
    }
}
