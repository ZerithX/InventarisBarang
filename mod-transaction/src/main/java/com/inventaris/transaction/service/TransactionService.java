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

        // 2. Simpan data transaksi baru ke database
        transaksiRepository.save(transaksi);

        // 3. Sinkronisasikan stok barang terbaru ke database
        barangRepository.updateStok(transaksi.getBarang().getId(), transaksi.getBarang().getStok());
    }
}
