import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {
    private SceneRouter router;

    public LoginView(SceneRouter router) {
        this.router = router;
    }

    public Parent getView() {
        // Layout Utama (Background Abu-abu)
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #F4F6F8;");

        // --- KARTU LOGIN (Kotak Putih di Tengah) ---
        VBox card = new VBox(15); // Jarak antar elemen vertikal 15
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400); // Lebar kartu dibatasi
        
        // Styling Kartu: Putih + Sudut Membulat + Bayangan
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10;");
        card.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.1)));

        // 1. Judul
        Label lblTitle = new Label("Pasar Jaten");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.web("#2E7D32")); // Hijau Tema

        Label lblSub = new Label("Silakan login untuk mulai belanja");
        lblSub.setTextFill(Color.GRAY);

        // 2. Form Input
        TextField userTxt = new TextField();
        userTxt.setPromptText("Username");
        userTxt.setPrefHeight(40);
        userTxt.setStyle("-fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        PasswordField passTxt = new PasswordField();
        passTxt.setPromptText("Password");
        passTxt.setPrefHeight(40);
        passTxt.setStyle("-fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        // 3. Tombol Login (Hijau Penuh)
        Button btnLogin = new Button("LOGIN");
        btnLogin.setPrefWidth(Double.MAX_VALUE); // Lebar tombol full
        btnLogin.setPrefHeight(45);
        btnLogin.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        // 4. Link Register (Teks Biasa)
        Label lblReg = new Label("Belum punya akun?");
        Hyperlink linkReg = new Hyperlink("Daftar disini");
        linkReg.setStyle("-fx-text-fill: #1976D2;");
        
        HBox regBox = new HBox(5, lblReg, linkReg);
        regBox.setAlignment(Pos.CENTER);

        // --- LOGIKA TOMBOL ---
        btnLogin.setOnAction(e -> {
            String u = userTxt.getText();
            String p = passTxt.getText();
            if (AppData.users.containsKey(u) && AppData.users.get(u).password.equals(p)) {
                AppData.currentUser = AppData.users.get(u);
                router.showMarketScene();
            } else {
                showAlert("Login Gagal", "Username atau Password salah!");
            }
        });

        linkReg.setOnAction(e -> router.showRegisterScene());

        // Masukkan semua elemen ke dalam kartu
        card.getChildren().addAll(lblTitle, lblSub, new Separator(), userTxt, passTxt, btnLogin, regBox);
        
        // Masukkan kartu ke root
        root.getChildren().add(card);

        return root;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(router.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}