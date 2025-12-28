import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter; // Import baru

public class PaymentView {
    private SceneRouter router;
    private boolean isDelivery;
    private String isiResiFinal;

    public PaymentView(SceneRouter router, boolean isDelivery) {
        this.router = router;
        this.isDelivery = isDelivery;
        lakukanTransaksi();
    }

    public Parent getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("PESANAN DITERIMA");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setStyle("-fx-text-fill: #2E7D32;"); 

        Label lblSubTitle = new Label("Silakan lakukan pembayaran ke Kasir / Admin.");
        lblSubTitle.setFont(Font.font("Segoe UI", 14));

        TextArea txtResi = new TextArea();
        txtResi.setText(isiResiFinal); 
        txtResi.setEditable(false);
        txtResi.setPrefHeight(400);
        txtResi.setMaxWidth(400);
        txtResi.setStyle("-fx-font-family: 'Monospaced';"); 

        Button btnSelesai = new Button("Selesai & Kembali ke Menu");
        btnSelesai.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSelesai.setMinHeight(40);
        btnSelesai.setMinWidth(250);

        btnSelesai.setOnAction(e -> {
            AppData.resetSession();
            router.showMarketScene();
        });

        root.getChildren().addAll(lblTitle, lblSubTitle, txtResi, btnSelesai);
        return root;
    }

    private void lakukanTransaksi() {
        // 1. Kurangi Stok
        for(ItemKeranjang item : AppData.keranjang) {
            int stokBaru = item.produk.getStok() - item.qty;
            item.produk.setStok(stokBaru);
        }

        // 2. Siapkan Data Transaksi
        boolean statusAwal = false; // Default: Belum Dibayar
        java.time.LocalDateTime waktuSekarang = java.time.LocalDateTime.now();
        
        // Format Tanggal untuk Tampilan Struk
        DateTimeFormatter showFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String tglStr = waktuSekarang.format(showFormat);
        
        StringBuilder sb = new StringBuilder();
        sb.append("     RESI PASAR JATEN    \n");
        sb.append("Jl. Raya Solo-Tawangmangu, \nJaten, Karanganyar, 57731\n");
        sb.append("--------------------------------\n");
        sb.append("Tanggal : ").append(tglStr).append("\n");
        sb.append("Customer: ").append(AppData.currentUser.namaLengkap).append("\n");
        sb.append("Metode   : ").append(isDelivery ? "Delivery (+Ongkir)":"Pick Up").append("\n");
        for(ItemKeranjang item : AppData.keranjang) {
             sb.append(item.produk.getNama())
               .append(" x").append(item.qty)
               .append("\n   Rp ").append((long)item.getSubtotal()).append("\n");
        }
        sb.append("\n");
        sb.append("Total    : Rp ").append((long)AppData.grandTotal).append("\n");
        sb.append("Status   : MENUNGGU PEMBAYARAN\n"); 
        sb.append("--------------------------------\n");
        sb.append("Simpan resi ini sebagai bukti.");

        this.isiResiFinal = sb.toString();

        // 4. Simpan ke Database (RAM & TXT)
        Transaksi trxBaru = new Transaksi(AppData.currentUser.username, isiResiFinal, AppData.grandTotal, statusAwal);
        AppData.currentUser.tambahTransaksi(trxBaru);
        AppData.saveTransaksiToDatabase(trxBaru);

        // 5. EXPORT KE FILE TXT (Folder 'struk')
        try {
            File folder = new File("struk");
            if (!folder.exists()) folder.mkdir(); 

            // Gunakan format nama file yang KONSISTEN agar bisa dipindahkan nanti
            // Contoh: Resi_04122025_1230.txt
            DateTimeFormatter fileFormat = DateTimeFormatter.ofPattern("ddMMyyyy_HHmm");
            String timeStamp = waktuSekarang.format(fileFormat);
            String fileName = "Resi_" + timeStamp + ".txt";
            
            File file = new File(folder, fileName);
            FileWriter writer = new FileWriter(file);
            writer.write(isiResiFinal);
            writer.close();
            
        } catch (IOException ioEx) {
            System.out.println("Gagal menyimpan struk: " + ioEx.getMessage());
        }
    }
}