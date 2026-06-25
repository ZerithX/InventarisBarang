package com.inventaris.transaction.service;

import com.inventaris.transaction.domain.Transaksi;
import com.inventaris.transaction.repository.TransaksiRepository;

import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private final TransaksiRepository transaksiRepository;

    public TransactionService(TransaksiRepository transaksiRepository) {
        this.transaksiRepository = transaksiRepository;
    }

    public int getTransactionsCountToday() throws SQLException {
        return transaksiRepository.countToday();
    }

    public List<Transaksi> getAllTransactions() throws SQLException {
        return transaksiRepository.findAll();
    }
}
