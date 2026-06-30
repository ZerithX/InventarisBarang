package com.inventaris.transaction.domain;

import com.inventaris.auth.domain.User;
import com.inventaris.core.exception.StockException;
import com.inventaris.inventory.domain.Barang;
import java.time.LocalDateTime;

public class BarangKeluar extends Transaksi {

    // Constructor for existing data (e.g. from database)
    public BarangKeluar(String id, Barang barang, int jumlah, User user, LocalDateTime createdAt, String keterangan) {
        super(id, barang, jumlah, TipeTransaksi.KELUAR, user, createdAt, keterangan);
    }

    // Constructor for new data
    public BarangKeluar(Barang barang, int jumlah, User user, String keterangan) {
        super(barang, jumlah, TipeTransaksi.KELUAR, user, keterangan);
    }

    @Override
    public void prosesStok() throws StockException {
        getBarang().kurangiStok(getJumlah());
    }
}
