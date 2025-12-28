import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class ShopView {
    private SceneRouter router;

    // Style Constants
    private final String BTN_GREEN   = "-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
    private final String BTN_ORANGE  = "-fx-background-color: #EF6C00; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
    private final String BTN_OUTLINE = "-fx-background-color: white; -fx-border-color: #7f8c8d; -fx-border-radius: 5; -fx-text-fill: #333; -fx-cursor: hand;";
    private final String BTN_BLUE    = "-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
    private final String BTN_RED     = "-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";

    public ShopView(SceneRouter router) {
        this.router = router;
    }

    @SuppressWarnings("unchecked")
    public Parent getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F4F6F8;");

        // 1. HEADER
        VBox header = new VBox(5);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        Label title = new Label(AppData.selectedToko.namaToko);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2c3e50"));
        
        Label subTitle = new Label("Silakan pilih barang kebutuhan Anda");
        subTitle.setFont(Font.font("Segoe UI", 14));
        subTitle.setTextFill(Color.web("#7f8c8d"));

        header.getChildren().addAll(title, subTitle);
        root.setTop(header);

        // 2. TABEL
        VBox centerBox = new VBox();
        centerBox.setPadding(new Insets(20));
        
        TableView<Produk> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);");

        TableColumn<Produk, String> nameCol = new TableColumn<>("Nama Barang");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nama"));
        
        TableColumn<Produk, Double> priceCol = new TableColumn<>("Harga Satuan");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("harga"));
        priceCol.setCellFactory(column -> new TableCell<Produk, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    Locale id = new Locale("id", "ID");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(id);
                    String s = fmt.format(price).replace("Rp", "Rp ").replace(",00", "");
                    setText(s);
                    setAlignment(Pos.CENTER_RIGHT);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #2E7D32;");
                }
            }
        });

        TableColumn<Produk, Integer> stokCol = new TableColumn<>("Sisa Stok");
        stokCol.setCellValueFactory(new PropertyValueFactory<>("stok"));
        stokCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(nameCol, priceCol, stokCol);
        table.setItems(AppData.selectedToko.daftarProduk);

        centerBox.getChildren().add(table);
        root.setCenter(centerBox);

        // 3. BAGIAN BAWAH (TOMBOL)
        VBox bottomContainer = new VBox(15);
        bottomContainer.setPadding(new Insets(15, 20, 20, 20));
        bottomContainer.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        HBox userButtons = new HBox(15);
        userButtons.setAlignment(Pos.CENTER_RIGHT);

        Button btnBack = new Button("Kembali");
        btnBack.setPrefHeight(40); btnBack.setPrefWidth(100);
        btnBack.setStyle(BTN_OUTLINE);
        
        Button btnAdd = new Button("+ Keranjang");
        btnAdd.setPrefHeight(40); btnAdd.setPrefWidth(140);
        btnAdd.setStyle(BTN_GREEN);

        Button btnCheckout = new Button("Checkout \u2794");
        btnCheckout.setPrefHeight(40); btnCheckout.setPrefWidth(140);
        btnCheckout.setStyle(BTN_ORANGE);

        btnBack.setOnAction(e -> {
            AppData.keranjang.clear(); 
            router.showMarketScene();
        });

        btnCheckout.setOnAction(e -> {
            if(AppData.keranjang.isEmpty()) showAlert(Alert.AlertType.WARNING,"Keranjang Kosong","Belum ada barang yang dipilih.");
            else router.showCartScene();
        });

        btnAdd.setOnAction(e -> {
            Produk selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                 showAlert(Alert.AlertType.WARNING, "Pilih Barang", "Klik salah satu barang di tabel dulu.");
                 return;
            }
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Jumlah Beli");
            dialog.setHeaderText("Beli: " + selected.getNama());
            dialog.setContentText("Masukkan jumlah:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(qtyStr -> {
                try {
                    int qtyInput = Integer.parseInt(qtyStr);
                    int inCart = 0;
                    ItemKeranjang existing = null;
                    for(ItemKeranjang item : AppData.keranjang) {
                        if(item.produk == selected) { inCart = item.qty; existing = item; break; }
                    }
                    int sisaReal = selected.getStok() - inCart;

                    if (qtyInput > 0 && qtyInput <= sisaReal) {
                        if(existing != null) existing.qty += qtyInput;
                        else AppData.keranjang.add(new ItemKeranjang(selected, qtyInput));
                        showAlert(Alert.AlertType.INFORMATION, "Sukses", "Masuk keranjang!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Stok Kurang", "Sisa stok yang bisa diambil: " + sisaReal);
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Masukkan angka yang benar.");
                }
            });
        });

        userButtons.getChildren().addAll(btnBack, btnAdd, btnCheckout);

        // --- TOMBOL ADMIN ---
        if (AppData.currentUser.username.equals("admin")) {
            HBox adminButtons = new HBox(15);
            adminButtons.setAlignment(Pos.CENTER_LEFT);
            adminButtons.setPadding(new Insets(0, 0, 10, 0));

            Label lblAdmin = new Label("Menu Admin:");
            lblAdmin.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
            
            Button btnAdmAdd = new Button("+ Tambah Produk");
            btnAdmAdd.setStyle(BTN_BLUE);
            
            Button btnAdmDel = new Button("Hapus Produk");
            btnAdmDel.setStyle(BTN_RED);

            // LOGIKA HAPUS (Hanya hapus dari RAM sementara, belum hapus permanen di txt - fitur advanced)
            btnAdmDel.setOnAction(e -> {
                Produk p = table.getSelectionModel().getSelectedItem();
                if(p != null) AppData.selectedToko.daftarProduk.remove(p);
            });

            // LOGIKA TAMBAH (DENGAN SIMPAN KE DATABASE)
            btnAdmAdd.setOnAction(e -> {
                Dialog<Produk> d = new Dialog<>();
                d.setTitle("Tambah Produk");
                d.setHeaderText("Menambah barang ke: " + AppData.selectedToko.namaToko);
                d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                
                GridPane grid = new GridPane(); 
                grid.setHgap(10); grid.setVgap(10);
                
                TextField tNama = new TextField(); 
                TextField tHarga = new TextField(); 
                TextField tStok = new TextField();
                
                grid.addRow(0, new Label("Nama:"), tNama);
                grid.addRow(1, new Label("Harga:"), tHarga);
                grid.addRow(2, new Label("Stok:"), tStok);
                
                d.getDialogPane().setContent(grid);
                
                d.setResultConverter(btn -> {
                    if(btn == ButtonType.OK) {
                        try { 
                            return new Produk(tNama.getText(), Double.parseDouble(tHarga.getText()), Integer.parseInt(tStok.getText())); 
                        } catch(Exception ex) { 
                            return null; 
                        }
                    } return null;
                });
                
                d.showAndWait().ifPresent(produkBaru -> {
                    // 1. TAMBAH KE LAYAR (RAM)
                    AppData.selectedToko.tambahProduk(produkBaru);
                    
                    // 2. SIMPAN KE DATABASE FILE (WAJIB ADA METHOD INI DI AppData)
                    AppData.saveProdukToDatabase(AppData.selectedToko.namaToko, produkBaru);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Produk berhasil disimpan ke database!");
                });
            });

            adminButtons.getChildren().addAll(lblAdmin, btnAdmAdd, btnAdmDel);
            bottomContainer.getChildren().addAll(adminButtons, new Separator(), userButtons);
        } else {
            bottomContainer.getChildren().add(userButtons);
        }

        root.setBottom(bottomContainer);
        return root;
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