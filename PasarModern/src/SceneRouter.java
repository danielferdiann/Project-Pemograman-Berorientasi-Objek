import javafx.stage.Stage;

public interface SceneRouter {
    void showLoginScene();
    void showRegisterScene();
    void showMarketScene();
    void showShopScene();
    void showCartScene();
    void showPaymentScene(boolean isDelivery);
    void showHistoryScene();
    Stage getPrimaryStage();
}