package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class ScriviEmailController {
    @FXML
    private TextArea destinatario;

    @FXML
    private TextArea cc;

    @FXML
    private TextArea ccn;

    @FXML
    private TextArea oggetto;

    @FXML
    private Button elimina;

    @FXML
    private Button invia;

    @FXML
    private TextArea testoEmail;

    @FXML
    public void eliminaMailCorrente(ActionEvent actionEvent) {

    }

    @FXML
    public void inviaEmail(ActionEvent actionEvent) {

    }
}