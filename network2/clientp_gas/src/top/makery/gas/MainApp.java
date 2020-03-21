package top.makery.gas;

//모델과 뷰의 부모 클래스 
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
    private Stage primaryStage;// gas를 보여주는 부분 
    private BorderPane rootLayout;// gas를 보여주는 부분의 상위 레이어

    @Override
    public void start(Stage primaryStage) {
    	//code의 시작부분 
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Gas Client IoT");
        initRootLayout();
        showGasOverview();
    }
    /**
     * 상위 레이아웃을 초기화한다.
     * @return 
     */
    
    public void initRootLayout() {
        try {
            // fxml 파일에서 상위 레이아웃을 가져온다.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            // 상위 레이아웃을 포함하는 scene을 보여준다.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
    * 상위 레이아웃 안에 연락처 요약(person overview)을 보여준다.
    */
    public void showGasOverview() {
    	//Client client = new Client("70.12.231.248", 9999, "gas");//연결할 서버 패드의 아이피 , 서버 패드의 포트 
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/gasOverview.fxml"));
            AnchorPane gasOverview = (AnchorPane) loader.load();
            // 연락처 요약을 상위 레이아웃 가운데로 설정한다.
            rootLayout.setCenter(gasOverview);
            // 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
            GasOverviewController controller = loader.getController();
            controller.setMainApp(this, 0, "stopped");//여기서 값을 refresh 해준다. 
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