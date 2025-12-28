import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HistoryView {
    private SceneRouter router;
    private final String BG_COLOR = "#F4F6F8";
    private final String PRIMARY_COLOR = "#2E7D32";

    public HistoryView(SceneRouter router) {
        this.router = router;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // HEADER
        VBox header = new VBox(5);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        Label title = new Label("Riwayat Transaksi");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2c3e50"));
        
        String subText = AppData.currentUser.username.equals("admin") ? 
                         "Mode Admin: Klik status untuk verifikasi pembayaran." : 
                         "Daftar belanjaan Anda.";
        Label subTitle = new Label(subText);
        subTitle.setTextFill(Color.GRAY);

        header.getChildren().addAll(title, subTitle);
        root.setTop(header);

        // KONTEN
        VBox historyContainer = new VBox(15);
        historyContainer.setPadding(new Insets(20));
        
        List<Transaksi> listTampil = new ArrayList<>();
        if (AppData.currentUser.username.equals("admin")) {
            for (User u : AppData.users.values()) listTampil.addAll(u.getRiwayat());
        } else {
            listTampil.addAll(AppData.currentUser.getRiwayat());
        }

        if (listTampil.isEmpty()) {
            historyContainer.setAlignment(Pos.CENTER);
            Label emptyLbl = new Label("Belum ada data transaksi.");
            emptyLbl.setFont(Font.font("Segoe UI", 16));
            emptyLbl.setTextFill(Color.GRAY);
            historyContainer.getChildren().add(emptyLbl);
        } else {
            Collections.sort(listTampil, (t1, t2) -> t2.getTanggalStr().compareTo(t1.getTanggalStr()));
            for (Transaksi t : listTampil) {
                historyContainer.getChildren().add(createHistoryCard(t));
            }
        }

        ScrollPane scrollPane = new ScrollPane(historyContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BG_COLOR + "; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        // FOOTER
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        Button btnBack = new Button("Kembali ke Menu Utama");
        btnBack.setPrefHeight(40);
        btnBack.setPrefWidth(200);
        btnBack.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;");
        btnBack.setOnAction(e -> router.showMarketScene());
        footer.getChildren().add(btnBack);
        root.setBottom(footer);

        return root;
    }

    private HBox createHistoryCard(Transaksi t) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // Info Kiri
        VBox leftInfo = new VBox(5);
        String headerText = t.getTanggalStr();
        if (AppData.currentUser.username.equals("admin")) {
            headerText += " (" + t.getUsername() + ")";
        }
        
        Label lblDate = new Label(headerText);
        lblDate.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblDate.setTextFill(Color.web("#333"));
        
        Label lblStatus = new Label();
        styleStatusLabel(lblStatus, t.isLunas());

        // LOGIKA ADMIN KLIK STATUS (UPDATE FILE STRUK)
        if (AppData.currentUser.username.equals("admin")) {
            lblStatus.setTooltip(new Tooltip("Klik untuk ubah status"));
            lblStatus.setCursor(javafx.scene.Cursor.HAND);
            
            lblStatus.setOnMouseClicked(e -> {
                boolean newStatus = !t.isLunas();
                t.setLunas(newStatus);
                
                // 1. Update text di struk memori
                String oldStruk = t.getDetailStruk();
                String newStruk;
                if (newStatus) {
                    newStruk = oldStruk.replace("Status   : MENUNGGU PEMBAYARAN", "Status   : LUNAS");
                } else {
                    newStruk = oldStruk.replace("Status   : LUNAS", "Status   : MENUNGGU PEMBAYARAN");
                }
                
                // Update object transaksi (hacky way karena tidak ada setter struk, tapi bisa diakali dgn load ulang atau set field reflection. 
                // Di sini kita anggap Transaksi punya method setDetailStruk() atau kita replace manual di DB Logic)
                // Karena class Transaksi kamu fieldnya private dan tidak ada setter, kita update di Database logic saja nanti.
                // TAPI UNTUK SEKARANG, kita update file fisiknya dulu:
                
                updatePhysicalFile(t, newStatus, newStruk);

                // 2. Update Tampilan
                styleStatusLabel(lblStatus, newStatus);
                
                // 3. Simpan perubahan ke DB
                AppData.updateAllTransaksiDatabase(); 
            });
        }
        
        HBox statusWrapper = new HBox(lblStatus);
        leftInfo.getChildren().addAll(lblDate, statusWrapper);
        HBox.setHgrow(leftInfo, Priority.ALWAYS);

        // Info Kanan
        VBox rightInfo = new VBox(8);
        rightInfo.setAlignment(Pos.CENTER_RIGHT);
        Label lblTotal = new Label(formatRupiah(t.getTotalBayar()));
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblTotal.setTextFill(Color.web(PRIMARY_COLOR));
        Button btnDetail = new Button("Lihat Struk");
        btnDetail.setStyle("-fx-background-color: white; -fx-text-fill: #1976D2; -fx-border-color: #1976D2; -fx-border-radius: 20; -fx-cursor: hand; -fx-font-size: 11px;");
        btnDetail.setOnAction(e -> showStrukDetail(t)); // Show struk akan menampilkan data lama yg di memory
                                                        // Idealnya Transaksi harus diupdate juga textnya.

        rightInfo.getChildren().addAll(lblTotal, btnDetail);
        card.getChildren().addAll(leftInfo, rightInfo);
        return card;
    }

    // --- LOGIKA UPDATE FILE FISIK (PINDAH FOLDER) ---
    private void updatePhysicalFile(Transaksi t, boolean isLunas, String updatedContent) {
        // Generate nama file berdasarkan tanggal transaksi
        // Format di PaymentView: ddMMyyyy_HHmm
        String rawDate = t.getTanggalStr(); // dd-MM-yyyy HH:mm
        String cleanDate = rawDate.replace("-", "").replace(" ", "_").replace(":", "");
        String fileName = "Resi_" + cleanDate + ".txt";

        File folderBelum = new File("struk");
        File folderLunas = new File("struk_lunas");
        if (!folderLunas.exists()) folderLunas.mkdir();
        if (!folderBelum.exists()) folderBelum.mkdir();

        File fileBelum = new File(folderBelum, fileName);
        File fileLunas = new File(folderLunas, fileName);

        try {
            if (isLunas) {
                // KASUS: DIBAYAR (Pindah dari 'struk' ke 'struk_lunas')
                
                // 1. Tulis file baru di folder lunas dengan konten UPDATE
                FileWriter writer = new FileWriter(fileLunas);
                writer.write(updatedContent);
                writer.close();
                
                // 2. Hapus file lama di folder belum bayar (jika ada)
                if (fileBelum.exists()) {
                    fileBelum.delete();
                }
                
                System.out.println("Struk dipindahkan ke folder struk_lunas");

            } else {
                // KASUS: BATAL BAYAR (Kembalikan dari 'struk_lunas' ke 'struk')
                
                // 1. Tulis file baru di folder struk dengan konten UPDATE
                FileWriter writer = new FileWriter(fileBelum);
                writer.write(updatedContent);
                writer.close();
                
                // 2. Hapus file di folder lunas
                if (fileLunas.exists()) {
                    fileLunas.delete();
                }
                System.out.println("Struk dikembalikan ke folder struk");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void styleStatusLabel(Label lbl, boolean isLunas) {
        if (isLunas) {
            lbl.setText("Status: LUNAS");
            lbl.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32; -fx-padding: 3 8 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            lbl.setText("Status: BELUM DIBAYAR");
            lbl.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #C62828; -fx-padding: 3 8 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
    }

    private void showStrukDetail(Transaksi t) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detail Transaksi");
        dialog.setHeaderText("Pelanggan: " + t.getUsername() + "\nWaktu: " + t.getTanggalStr());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Kita coba baca status Lunas/Tidak untuk menampilkan teks yang benar di popup
        // (Sederhana: replace string on the fly saat mau ditampilkan)
        String contentTampil = t.getDetailStruk();
        if (t.isLunas()) {
            contentTampil = contentTampil.replace("Status   : MENUNGGU PEMBAYARAN", "Status   : LUNAS");
        } else {
            contentTampil = contentTampil.replace("Status   : SUDAH DIBAYAR", "Status   : MENUNGGU PEMBAYARAN");
        }

        TextArea area = new TextArea(contentTampil);
        area.setEditable(false);
        area.setWrapText(false);
        area.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12px;");
        area.setPrefSize(400, 300);

        dialog.getDialogPane().setContent(area);
        dialog.showAndWait();
    }

    private String formatRupiah(double number) {
        Locale id = new Locale("id", "ID");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(id);
        return fmt.format(number).replace("Rp", "Rp ").replace(",00", "");
    }
}