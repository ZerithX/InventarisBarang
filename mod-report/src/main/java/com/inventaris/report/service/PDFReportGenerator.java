package com.inventaris.report.service;

import com.inventaris.report.domain.LaporanItem;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFReportGenerator {

    /**
     * Menghasilkan file PDF untuk Laporan Stok Barang.
     *
     * @param items        Daftar data barang beserta mutasi stoknya.
     * @param periodeText  Teks periode laporan (misal: "Juli 2026").
     * @param kategoriText Teks filter kategori (misal: "Makanan" atau "Semua Kategori").
     * @param file         File destinasi tempat PDF disimpan.
     * @throws DocumentException jika terjadi error dalam pemrosesan struktur dokumen PDF.
     * @throws java.io.IOException jika terjadi error saat menulis ke file.
     */
    public static void generateStokReport(List<LaporanItem> items, String periodeText, String kategoriText, File file) 
            throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        FileOutputStream fos = new FileOutputStream(file);
        PdfWriter.getInstance(document, fos);
        
        document.open();
        
        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(25, 118, 210)); // Royal Blue
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
        Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        Font cellBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
        
        // Document Header / Title
        Paragraph title = new Paragraph("LAPORAN STOK BARANG", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph subtitle = new Paragraph("Periode: " + periodeText, subTitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        
        Paragraph kategori = new Paragraph("Kategori: " + kategoriText, subTitleFont);
        kategori.setAlignment(Element.ALIGN_CENTER);
        document.add(kategori);
        
        // Metadata (Printed at, etc.)
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        Paragraph meta = new Paragraph("Dicetak pada: " + now.format(formatter), metaFont);
        meta.setAlignment(Element.ALIGN_LEFT);
        meta.setSpacingAfter(20);
        document.add(meta);

        // Summary
        Paragraph summary = new Paragraph("\nTotal Barang Terdata: " + items.size(), cellBoldFont);
        summary.setAlignment(Element.ALIGN_LEFT);
        document.add(summary);
        
        // Create Table
        // Columns: No, ID, Nama Barang, Kategori, Stok Awal, Masuk, Keluar, Stok Akhir
        float[] columnWidths = {3f, 8f, 15f, 10f, 6f, 6f, 6f, 6f};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        
        // Table Headers
        String[] headers = {"No", "ID", "Nama Barang", "Kategori", "Awal", "Masuk", "Keluar", "Akhir"};
        Color headerBgColor = new Color(25, 118, 210);
        for (String headerText : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(headerText, headerFont));
            cell.setBackgroundColor(headerBgColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(6);
            table.addCell(cell);
        }
        
        // Table Data
        int no = 1;
        for (LaporanItem item : items) {
            Color rowBg = (no % 2 == 0) ? new Color(245, 247, 250) : Color.WHITE;
            
            // No
            PdfPCell cNo = new PdfPCell(new Phrase(String.valueOf(no++), cellFont));
            cNo.setBackgroundColor(rowBg);
            cNo.setHorizontalAlignment(Element.ALIGN_CENTER);
            cNo.setPadding(5);
            table.addCell(cNo);
            
            // ID
            PdfPCell cId = new PdfPCell(new Phrase(item.getIdBarang(), cellFont));
            cId.setBackgroundColor(rowBg);
            cId.setHorizontalAlignment(Element.ALIGN_CENTER);
            cId.setPadding(5);
            table.addCell(cId);
            
            // Nama Barang
            PdfPCell cNama = new PdfPCell(new Phrase(item.getNamaBarang(), cellFont));
            cNama.setBackgroundColor(rowBg);
            cNama.setHorizontalAlignment(Element.ALIGN_LEFT);
            cNama.setPadding(5);
            table.addCell(cNama);
            
            // Kategori
            PdfPCell cKat = new PdfPCell(new Phrase(item.getNamaKategori(), cellFont));
            cKat.setBackgroundColor(rowBg);
            cKat.setHorizontalAlignment(Element.ALIGN_LEFT);
            cKat.setPadding(5);
            table.addCell(cKat);
            
            // Stok Awal
            PdfPCell cAwal = new PdfPCell(new Phrase(String.valueOf(item.getStokAwal()), cellFont));
            cAwal.setBackgroundColor(rowBg);
            cAwal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cAwal.setPadding(5);
            table.addCell(cAwal);
            
            // Masuk
            String masukVal = item.getMasuk() > 0 ? "+" + item.getMasuk() : "0";
            Font greenFont = item.getMasuk() > 0 ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(46, 125, 50)) : cellFont;
            PdfPCell cMasuk = new PdfPCell(new Phrase(masukVal, greenFont));
            cMasuk.setBackgroundColor(rowBg);
            cMasuk.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cMasuk.setPadding(5);
            table.addCell(cMasuk);
            
            // Keluar
            String keluarVal = item.getKeluar() > 0 ? "-" + item.getKeluar() : "0";
            Font redFont = item.getKeluar() > 0 ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(198, 40, 40)) : cellFont;
            PdfPCell cKeluar = new PdfPCell(new Phrase(keluarVal, redFont));
            cKeluar.setBackgroundColor(rowBg);
            cKeluar.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cKeluar.setPadding(5);
            table.addCell(cKeluar);
            
            // Stok Akhir
            PdfPCell cAkhir = new PdfPCell(new Phrase(String.valueOf(item.getStokAkhir()), cellBoldFont));
            cAkhir.setBackgroundColor(rowBg);
            cAkhir.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cAkhir.setPadding(5);
            table.addCell(cAkhir);
        }
        
        document.add(table);
        
        document.close();
        fos.close();
    }
}
