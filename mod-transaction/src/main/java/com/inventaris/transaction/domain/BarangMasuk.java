package com.inventaris.transaction.domain;

import com.inventaris.auth.domain.User;
import com.inventaris.core.exception.StockException;
import com.inventaris.inventory.domain.Barang;
import java.time.LocalDateTime;

public class BarangMasuk extends Transaksi {

    // Constructor for existing data (e.g. from database)
    public BarangMasuk(String id, Barang barang, int jumlah, User user, LocalDateTime createdAt, String keterangan) {
        super(id, barang, jumlah, TipeTransaksi.MASUK, user, createdAt, keterangan);
    }

    // Constructor for new data
    public BarangMasuk(Barang barang, int jumlah, User user, String keterangan) {
        super(barang, jumlah, TipeTransaksi.MASUK, user, keterangan);
    }

    @Override
    public void prosesStok() throws StockException {
        getBarang().tambahStok(getJumlah());
    }
}
