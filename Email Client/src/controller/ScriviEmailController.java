package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import model.DataModel;


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
    private HBox comandi;

    private DataModel dataModel;

    public void initModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    @FXML
    public void eliminaMailCorrente(ActionEvent actionEvent) {

    }

    @FXML
    public void inviaEmail(ActionEvent actionEvent) throws InterruptedException {
        if (destinatario.getText().isBlank() || cc.getText().isBlank() || ccn.getText().isBlank() || oggetto.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Compilare tutti i campi in rosso");
            alert.show();

            if (destinatario.getText().isBlank()) {
                destinatario.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            }

            if (cc.getText().isBlank()) {
                cc.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            }

            if (ccn.getText().isBlank()) {
                ccn.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            }

            if (oggetto.getText().isBlank()) {
                oggetto.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            }
        }
    }

}