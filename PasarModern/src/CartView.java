import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.text.NumberFormat;
import java.util.Locale;

public class CartView {
    private SceneRouter router;
    private VBox itemContainer; // Wadah untuk kartu-kartu barang
    private Label lblSubtotal, lblOngkir, lblGrandTotal;
    private RadioButton rbPickup, rbDelivery;

    // Warna Tema
    private final String BG_COLOR = "#F4F6F8";
    private final String PRIMARY_COLOR = "#2E7D32";

    public CartView(SceneRouter router) {
        this.router = router;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");
        VBox header = new VBox(5);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        Label title = new Label("Keranjang Belanja");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2c3e50"));
        
        Label subtitle = new Label("Periksa kembali pesanan Anda sebelum membayar");
        subtitle.setTextFill(Color.GRAY);

        header.getChildren().addAll(title, subtitle);
        root.setTop(header);

        itemContainer = new VBox(15);
        itemContainer.setPadding(new Insets(20));
        refreshCartItems();

        ScrollPane scrollPane = new ScrollPane(itemContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BG_COLOR + "; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        VBox footer = new VBox(15);
        footer.setPadding(new Insets(20));
        footer.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 0, -5, 10, 0);");

        // --- A. Opsi Pengiriman ---
        Label lblMetode = new Label("Metode Pengiriman:");
        lblMetode.setStyle("-fx-font-weight: bold;");

        rbPickup = new RadioButton("Pick Up (Ambil Sendiri - Gratis)");
        rbDelivery = new RadioButton("Delivery (Diantar - Rp 15.000)");
        
        ToggleGroup group = new ToggleGroup();
        rbPickup.setToggleGroup(group);
        rbDelivery.setToggleGroup(group);
        rbPickup.setSelected(true); // Default

        VBox deliveryBox = new VBox(10, lblMetode, rbPickup, rbDelivery);
        deliveryBox.setPadding(new Insets(15));
        deliveryBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 5; -fx-border-color: #eee; -fx-border-radius: 5;");

        // Listener jika ganti metode pengiriman -> Update Total
        group.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateCalculations());

        // --- B. Ringkasan Harga ---
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(20); summaryGrid.setVgap(5);
        
        lblSubtotal = new Label("Rp 0");
        lblOngkir = new Label("Rp 0");
        lblGrandTotal = new Label("Rp 0");
        lblGrandTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblGrandTotal.setTextFill(Color.web(PRIMARY_COLOR));

        summaryGrid.addRow(0, new Label("Subtotal Produk:"), lblSubtotal);
        summaryGrid.addRow(1, new Label("Ongkos Kirim:"), lblOngkir);
        summaryGrid.addRow(2, new Label("TOTAL:"), lblGrandTotal);

        // --- C. Tombol Aksi ---
        HBox btnBox = new HBox(15);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnBack = new Button("Kembali Belanja");
        btnBack.setPrefHeight(40);
        btnBack.setStyle("-fx-background-color: white; -fx-border-color: #aaa; -fx-border-radius: 5; -fx-text-fill: #333; -fx-cursor: hand;");
        
        Button btnPay = new Button("Lanjut Pembayaran \u2794");
        btnPay.setPrefHeight(40);
        btnPay.setPrefWidth(200);
        btnPay.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        btnBack.setOnAction(e -> router.showMarketScene());
        
        btnPay.setOnAction(e -> {
            boolean isDeliv = rbDelivery.isSelected();
            // Update data global sebelum pindah scene
            AppData.ongkir = isDeliv ? 15000 : 0;
            AppData.grandTotal = AppData.totalBelanja + AppData.ongkir;
            
            router.showPaymentScene(isDeliv);
        });

        btnBox.getChildren().addAll(btnBack, btnPay);

        footer.getChildren().addAll(deliveryBox, new Separator(), summaryGrid, btnBox);
        root.setBottom(footer);

        // Hitung awal saat halaman dibuka
        updateCalculations();

        return root;
    }

    // --- LOGIKA MEMBUAT KARTU ITEM ---
    private void refreshCartItems() {
        itemContainer.getChildren().clear();

        if (AppData.keranjang.isEmpty()) {
            Label empty = new Label("Keranjang masih kosong.");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
            itemContainer.setAlignment(Pos.CENTER);
            itemContainer.getChildren().add(empty);
            return;
        }

        itemContainer.setAlignment(Pos.TOP_LEFT);
        
        for (ItemKeranjang item : AppData.keranjang) {
            HBox card = new HBox(15);
            card.setPadding(new Insets(15));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

            // Info Barang
            VBox infoBox = new VBox(5);
            Label nameLbl = new Label(item.produk.getNama());
            nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            
            Label priceLbl = new Label(formatRupiah(item.produk.getHarga()) + " x " + item.qty);
            priceLbl.setTextFill(Color.GRAY);
            
            infoBox.getChildren().addAll(nameLbl, priceLbl);
            HBox.setHgrow(infoBox, Priority.ALWAYS); // Biar infoBox ambil sisa ruang

            // Subtotal Barang
            Label subTotalLbl = new Label(formatRupiah(item.getSubtotal()));
            subTotalLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            subTotalLbl.setTextFill(Color.web(PRIMARY_COLOR));

            // Tombol Hapus Kecil (Bonus UX)
            Button btnDel = new Button("X");
            btnDel.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-background-radius: 20; -fx-cursor: hand;");
            btnDel.setOnAction(e -> {
                AppData.keranjang.remove(item);
                refreshCartItems(); // Render ulang list
                updateCalculations(); // Hitung ulang total
            });

            card.getChildren().addAll(infoBox, subTotalLbl, btnDel);
            itemContainer.getChildren().add(card);
        }
    }

    // --- LOGIKA HITUNG TOTAL ---
    private void updateCalculations() {
        double subtotal = 0;
        for (ItemKeranjang item : AppData.keranjang) {
            subtotal += item.getSubtotal();
        }
        
        double ongkir = rbDelivery.isSelected() ? 15000 : 0;
        double total = subtotal + ongkir;

        // Update Text
        lblSubtotal.setText(formatRupiah(subtotal));
        lblOngkir.setText(formatRupiah(ongkir));
        lblGrandTotal.setText(formatRupiah(total));

        // Update Global Data
        AppData.totalBelanja = subtotal;
        AppData.ongkir = ongkir;
        AppData.grandTotal = total;
    }

    // Helper format rupiah lokal biar tidak error kalau belum ada FormatUtil
    private String formatRupiah(double number) {
        Locale id = new Locale("id", "ID");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(id);
        return fmt.format(number).replace("Rp", "Rp ").replace(",00", "");
    }
}