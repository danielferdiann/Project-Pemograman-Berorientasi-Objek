import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// MainApp bertindak sebagai Controller Navigasi (Router)
public class MainApp extends Application implements SceneRouter {

    private Stage primaryStage;

    @Override
    public void init() {
        // Inisialisasi Data Dummy sebelum aplikasi mulai
        AppData.initDummyData();
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Pasar Jaten Apps");
        
        // Mulai aplikasi dengan menampilkan halaman Login
        showLoginScene();
        primaryStage.show();
    }
    
    // Helper private untuk mengganti scene
    private void switchScene(Parent root) {
        // Jika scene belum ada, buat baru. Jika sudah ada, ganti root-nya.
        if (primaryStage.getScene() == null) {
            Scene scene = new Scene(root, 900, 640);
            primaryStage.setScene(scene);
        } else {
            primaryStage.getScene().setRoot(root);
        }
    }

    @Override
    public void showLoginScene() {
        // Membuat objek View dan meminta tampilan (Parent) nya
        LoginView view = new LoginView(this);
        switchScene(view.getView());
    }

    @Override
    public void showRegisterScene() {
        RegisterView view = new RegisterView(this);
        switchScene(view.getView());
    }

    @Override
    public void showMarketScene() {
        MarketView view = new MarketView(this);
        switchScene(view.getView());
    }

    @Override
    public void showShopScene() {
        ShopView view = new ShopView(this);
        switchScene(view.getView());
    }

    @Override
    public void showCartScene() {
        CartView view = new CartView(this);
        switchScene(view.getView());
    }

    @Override
    public void showPaymentScene(boolean isDelivery) {
        // PaymentView butuh data tambahan (status delivery)
        PaymentView view = new PaymentView(this, isDelivery);
        switchScene(view.getView());
    }

    @Override
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    @Override
    public void showHistoryScene() {
    HistoryView view = new HistoryView(this);
    switchScene(view.getView());
}

    public static void main(String[] args) {
        launch(args);
    }
}
