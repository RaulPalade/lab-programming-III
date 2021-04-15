package server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Raul Palade
 * @project Email Server
 * @date 05/03/2021
 */
public class EmailServerController implements Initializable {
    @FXML
    private ListView<String> log;

    @FXML
    private Button startStopServerButton;

    private ServerModel serverModel;
    private ThreadPoolServer threadPoolServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (this.serverModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        serverModel = new ServerModel();
        socketConnector();

        log.setEditable(false);
        log.setItems(serverModel.getLog());

        log.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    Platform.runLater(() -> setText(null));
                } else {
                    Platform.runLater(() -> setText(item));
                }
            }
        });
    }

    @FXML
    public void startStopServer() throws IOException {
        ButtonType si = new ButtonType("Si", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        if (startStopServerButton.getText().equalsIgnoreCase("SPEGNI SERVER")) {
            alert.setHeaderText("Sei sicuro di voler spegnere il server?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(no) == si) {
                startStopServerButton.setText("AVVIA SERVER");
                threadPoolServer.stopServer();
            }
        } else if (startStopServerButton.getText().equalsIgnoreCase("AVVIA SERVER")) {
            alert.setHeaderText("Sei sicuro di voler avviare il server?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(no) == si) {
                startStopServerButton.setText("SPEGNI SERVER");
                socketConnector();
            }
        }
    }

    public void socketConnector() {
        threadPoolServer = new ThreadPoolServer(serverModel);
        threadPoolServer.start();
    }
}