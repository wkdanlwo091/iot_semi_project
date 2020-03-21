package top.makery.gas;

//�𵨰� ���� �θ� Ŭ���� 
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import top.makery.gas.view.GasOverviewController;

public class MainApp extends Application {
    private Stage primaryStage;// gas�� �����ִ� �κ� 
    private BorderPane rootLayout;// gas�� �����ִ� �κ��� ���� ���̾�

    @Override
    public void start(Stage primaryStage) {
    	//code�� ���ۺκ� 
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Gas Client IoT");
        initRootLayout();
        showGasOverview();
    }
    /**
     * ���� ���̾ƿ��� �ʱ�ȭ�Ѵ�.
     * @return 
     */
    
    public void initRootLayout() {
        try {
            // fxml ���Ͽ��� ���� ���̾ƿ��� �����´�.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            // ���� ���̾ƿ��� �����ϴ� scene�� �����ش�.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
    * ���� ���̾ƿ� �ȿ� ����ó ���(person overview)�� �����ش�.
    */
    public void showGasOverview() {
    	//Client client = new Client("70.12.231.248", 9999, "gas");//������ ���� �е��� ������ , ���� �е��� ��Ʈ 
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/gasOverview.fxml"));
            AnchorPane gasOverview = (AnchorPane) loader.load();
            // ����ó ����� ���� ���̾ƿ� ����� �����Ѵ�.
            rootLayout.setCenter(gasOverview);
            // ���� ���ø����̼��� ��Ʈ�ѷ��� �̿��� �� �ְ� �Ѵ�.
            GasOverviewController controller = loader.getController();
            controller.setMainApp(this, 0, "stopped");//���⼭ ���� refresh ���ش�. 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public static void main(String[] args) {
        launch(args);
    }
}