import controller.EmailClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DataModel;


/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmailClient.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Email Client");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        EmailClientController emailClientController = loader.getController();
        emailClientController.initModel(new DataModel());
    }

    public static void main(String[] args) {
        launch(args);
    }
}