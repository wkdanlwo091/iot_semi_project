package ringtutorial;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;  
import javafx.scene.control.ProgressIndicator;  
import javafx.scene.layout.StackPane;  
import javafx.stage.Stage;  
public class RingTutorial extends Application{

	private int percent = 0;
	ProgressIndicator PI;
	
	public void print(int percent) {
		PI.setProgress(percent*0.01);
	}
	
	@Override  
    public void start(Stage primaryStage) throws Exception {  
        // TODO Auto-generated method stub  
        PI=new ProgressIndicator();  
          
        StackPane root = new StackPane();  
        root.getChildren().add(PI);  
        Scene scene = new Scene(root,300,200);  
        primaryStage.setScene(scene);  
        primaryStage.setTitle("Progress Indicator Example");  
        primaryStage.show();  

        Thread thread = new Thread() {
            @Override
            public void run() { // 기존대로 쓰면 예외발생 (FX등록 스레드만 쓸 것)
                while (percent < 100) {
                    percent++;
                    Platform.runLater(() -> {    // Lambda Expression
                    	print(percent);    
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                /* 내 풀이
                while(true) {
                    plus();
                    if(percent == 100) {
                        break;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        
                    }
                }*/
            }
            
        };
        
        thread.start();
          
    }  
    public static void main(String[] args) {  
        launch(args);  
    }  

}
