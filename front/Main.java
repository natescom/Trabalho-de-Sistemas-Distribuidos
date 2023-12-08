import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/tela.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Sistemas Distribuidos");

        primaryStage.setScene(new Scene(root));

        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        }); 
        primaryStage.show();
    }
}