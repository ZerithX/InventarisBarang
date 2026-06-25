package com.inventaris.inventory.service;

import com.inventaris.inventory.domain.Barang;
import com.inventaris.inventory.domain.Kategori;
import com.inventaris.inventory.repository.BarangRepository;
import com.inventaris.inventory.repository.KategoriRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventoryService {
    private final BarangRepository barangRepository;
    private final KategoriRepository kategoriRepository;

    public InventoryService(BarangRepository barangRepository, KategoriRepository kategoriRepository) {
        this.barangRepository = barangRepository;
        this.kategoriRepository = kategoriRepository;
    }

    public int getTotalKategori() throws SQLException {
        return kategoriRepository.count();
    }

    public int getTotalBarang() throws SQLException {
        return barangRepository.count();
    }

    public int getTotalLowStockBarang(int threshold) throws SQLException {
        return barangRepository.countLowStock(threshold);
    }

    public List<Barang> getAllBarang() throws SQLException {
        return barangRepository.findAll();
    }

    public List<Barang> searchBarangByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return getAllBarang();
        }
        return barangRepository.findByNameLike(name.trim());
    }

    public Optional<Kategori> getKategoriById(String id) throws SQLException {
        return kategoriRepository.findById(id);
    }

    public Map<String, String> getAllKategoriMap() throws SQLException {
        List<Kategori> kategoris = kategoriRepository.findAll();
        Map<String, String> map = new HashMap<>();
        for (Kategori kat : kategoris) {
            map.put(kat.getId(), kat.getNama());
        }
        return map;
    }
}
