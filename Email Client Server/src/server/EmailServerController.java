package server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Raul Palade
 * @project Email Server
 * @date 05/03/2021
 */
public class EmailServerController implements Initializable {
    @FXML
    private TextArea log;

    @FXML
    private Button spegni;

    @FXML
    private Button avvia;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.setEditable(false);
    }

    @FXML
    public void spegniServer(ActionEvent actionEvent) {

    }

    @FXML
    public void avviaServer(ActionEvent actionEvent) {

    }
}