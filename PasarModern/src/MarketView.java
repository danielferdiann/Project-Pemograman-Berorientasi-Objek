import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow; // Efek Bayangan
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MarketView {
    private SceneRouter router;

    // Warna Tema (Hardcoded constants)
    private final String PRIMARY_COLOR = "#2E7D32"; // Hijau Tua
    private final String BG_COLOR = "#F4F6F8";      // Abu-abu muda (Background)
    private final String CARD_BG = "#FFFFFF";       // Putih (Kartu)

    public MarketView(SceneRouter router) {
        this.router = router;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        // Set background utama aplikasi
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- 1. HEADER (Bagian Atas) ---
        VBox header = new VBox(5);
        header.setPadding(new Insets(25));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        
        Label lblWelcome = new Label("Halo, " + AppData.currentUser.namaLengkap);
        lblWelcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        lblWelcome.setTextFill(Color.web("#333"));
        
        Label lblSub = new Label("Selamat datang di Pasar Jaten Apps");
        lblSub.setFont(Font.font("Segoe UI", 14));
        lblSub.setTextFill(Color.web("#777"));

        header.getChildren().addAll(lblWelcome, lblSub);
        root.setTop(header);

        // --- 2. KONTEN TENGAH (Daftar Toko Grid/Flow) ---
        FlowPane containerToko = new FlowPane();
        containerToko.setPadding(new Insets(30));
        containerToko.setHgap(12); // Jarak horizontal antar kartu
        containerToko.setVgap(20); // Jarak vertikal antar kartu
        containerToko.setAlignment(Pos.TOP_LEFT);
        containerToko.setPrefWrapLength(600); // Agar kartu turun ke bawah jika layar sempit

        // Loop untuk membuat "Kartu" setiap toko
        for (Toko t : AppData.daftarToko) {
            VBox card = createShopCard(t);
            containerToko.getChildren().add(card);
        }

        // Agar bisa discroll jika tokonya banyak
        ScrollPane scrollPane = new ScrollPane(containerToko);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BG_COLOR + "; -fx-background-color: " + BG_COLOR + ";");
        root.setCenter(scrollPane);

        // --- 3. FOOTER (Menu Bawah) ---
        HBox bottomMenu = new HBox(15);
        bottomMenu.setPadding(new Insets(20));
        bottomMenu.setAlignment(Pos.CENTER);
        bottomMenu.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        // Style Tombol yang seragam
        String btnStyleBase = "-fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 20;";

        Button btnHistory = new Button("Riwayat Belanja");
        btnHistory.setPrefHeight(40);
        btnHistory.setPrefWidth(150);
        btnHistory.setStyle(btnStyleBase + "-fx-background-color: #1976D2; -fx-text-fill: white;");
        btnHistory.setOnAction(e -> router.showHistoryScene());

        Button btnLogout = new Button("Keluar / Logout");
        btnLogout.setPrefHeight(40);
        btnLogout.setPrefWidth(150);
        btnLogout.setStyle(btnStyleBase + "-fx-background-color: #D32F2F; -fx-text-fill: white;");
        btnLogout.setOnAction(e -> router.showLoginScene());

        bottomMenu.getChildren().addAll(btnHistory, btnLogout);
        root.setBottom(bottomMenu);

        return root;
    }

    // Method Helper untuk membuat desain "Kartu Toko" yang cantik
    private VBox createShopCard(Toko t) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(200, 220); // Ukuran kartu
        
        // Styling Kartu (Background Putih + Border Radius + Shadow)
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #eaeaea;");
        
        // Tambahkan efek bayangan agar timbul
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1)); // Bayangan halus
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        card.setEffect(shadow);

        // Icon Toko (Lingkaran dengan inisial)
        StackPane iconPane = new StackPane();
        Circle circle = new Circle(30, Color.web(PRIMARY_COLOR));
        String inisial = t.namaToko.substring(0, 1).toUpperCase();
        Label lblInisial = new Label(inisial);
        lblInisial.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblInisial.setTextFill(Color.WHITE);
        iconPane.getChildren().addAll(circle, lblInisial);

        // Nama Toko
        Label lblNama = new Label(t.namaToko);
        lblNama.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblNama.setTextFill(Color.web("#333"));
        lblNama.setWrapText(true);
        lblNama.setAlignment(Pos.CENTER);

        // Deskripsi Kecil
        Label lblDesc = new Label("Tersedia Aneka Barang");
        lblDesc.setFont(Font.font("Segoe UI", 12));
        lblDesc.setTextFill(Color.web("#888"));

        // Tombol Kunjungi
        Button btnVisit = new Button("Kunjungi");
        btnVisit.setPrefWidth(120);
        btnVisit.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");
        btnVisit.setOnAction(e -> {
            AppData.selectedToko = t;
            router.showShopScene();
        });

        // Efek Hover (Saat mouse di atas kartu)
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f9fdf9; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: " + PRIMARY_COLOR + ";"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #eaeaea;"));

        card.getChildren().addAll(iconPane, lblNama, lblDesc, btnVisit);
        return card;
    }
}