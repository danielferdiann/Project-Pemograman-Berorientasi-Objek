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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RegisterView {
    private SceneRouter router;

    public RegisterView(SceneRouter router) {
        this.router = router;
    }

    public Parent getView() {
        // Layout Utama
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #F4F6F8;");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F4F6F8; -fx-background-color: transparent;");

        // Kartu Register
        VBox card = new VBox(15);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(450);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10;");
        card.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.1)));

        // Header
        Label lblTitle = new Label("Buat Akun");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.web("#2E7D32"));

        Label lblSub = new Label("Isi data berikut untuk mendaftar akun baru");
        lblSub.setTextFill(Color.GRAY);

        // Form Input
        String fieldStyle = "-fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 8;";

        TextField userTxt = new TextField();
        userTxt.setPromptText("Username");
        userTxt.setStyle(fieldStyle);

        PasswordField passTxt = new PasswordField();
        passTxt.setPromptText("Password");
        passTxt.setStyle(fieldStyle);

        TextField namaTxt = new TextField();
        namaTxt.setPromptText("Nama Lengkap");
        namaTxt.setStyle(fieldStyle);

        TextField telpTxt = new TextField();
        telpTxt.setPromptText("No. Telp");
        telpTxt.setStyle(fieldStyle);

        TextArea alamatTxt = new TextArea();
        alamatTxt.setPromptText("Alamat Lengkap");
        alamatTxt.setPrefHeight(60);
        alamatTxt.setWrapText(true);
        alamatTxt.setStyle("-fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        // Tombol Daftar
        Button btnSimpan = new Button("DAFTAR");
        btnSimpan.setPrefWidth(Double.MAX_VALUE);
        btnSimpan.setPrefHeight(45);
        btnSimpan.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        Label lblLogin = new Label("Sudah punya akun?");
        Hyperlink linkLogin = new Hyperlink("Login disini");
        linkLogin.setStyle("-fx-text-fill: #1976D2;");
        HBox loginBox = new HBox(5, lblLogin, linkLogin);
        loginBox.setAlignment(Pos.CENTER);

        // --- LOGIKA PENYIMPANAN KE DATABASE TXT ---
        btnSimpan.setOnAction(e -> {
            String u = userTxt.getText();
            String p = passTxt.getText();
            String n = namaTxt.getText();
            String t = telpTxt.getText();
            String a = alamatTxt.getText();

            if (!u.isEmpty() && !p.isEmpty() && !n.isEmpty()) {
                // 1. Simpan ke Memori Sementara (agar bisa langsung login tanpa restart)
                User newUser = new User(u, p, n, t, a);
                AppData.users.put(u, newUser);

                // 2. SIMPAN KE FILE (DATABASE TXT)
                saveUserToFile(u, p, n, t, a);
                
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Akun berhasil disimpan ke Database!");
                router.showLoginScene();
            } else {
                showAlert(Alert.AlertType.WARNING, "Gagal", "Username, Password, dan Nama wajib diisi.");
            }
        });

        linkLogin.setOnAction(e -> router.showLoginScene());

        card.getChildren().addAll(lblTitle, lblSub, new Separator(),
            userTxt,
            passTxt,
            namaTxt,
            telpTxt,
            alamatTxt,
            new Separator(), btnSimpan, loginBox
        );

        root.getChildren().add(card);
        return root;
    }

    // --- METHOD KHUSUS UNTUK MENULIS KE FILE ---
    private void saveUserToFile(String u, String p, String n, String t, String a) {
        try {
            // "true" artinya append (menambahkan di baris baru, bukan menimpa file lama)
            FileWriter fw = new FileWriter("users_db.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            
            // Format: username;password;nama;telp;alamat
            // Kita ganti newline (\n) di alamat jadi spasi biar ga merusak format database
            String cleanAlamat = a.replace("\n", " "); 
            
            String dataLine = u + ";" + p + ";" + n + ";" + t + ";" + cleanAlamat;
            
            bw.write(dataLine);
            bw.newLine(); // Pindah baris untuk user berikutnya
            bw.close();
            
            System.out.println("Data user tersimpan di users_db.txt");
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error System", "Gagal menyimpan ke database file.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.initOwner(router.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}