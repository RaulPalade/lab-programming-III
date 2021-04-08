package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestServer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader serverLoader = new FXMLLoader(getClass().getResource("/server/EmailServer.fxml"));
        Parent root = serverLoader.load();
        primaryStage.setTitle("Email Server");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}