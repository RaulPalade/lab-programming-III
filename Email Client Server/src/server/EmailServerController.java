package server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
    private Button spegni;

    private DataModel dataModel;
    private ThreadPoolServer threadPoolServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        dataModel = new DataModel();
        socketConnector();

        log.setEditable(false);
        log.setItems(dataModel.getLog());

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
    public void spegniServer(ActionEvent actionEvent) {
        ButtonType si = new ButtonType("Si", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        alert.setHeaderText("Sei sicuro di spegnere il server?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(no) == si) {
            Stage stage = (Stage) spegni.getScene().getWindow();
            stage.close();
        }
    }

    public void socketConnector() {
        threadPoolServer = new ThreadPoolServer(dataModel);
        threadPoolServer.start();
    }
}