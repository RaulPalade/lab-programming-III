package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import model.Email;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class EmailClientController {
    @FXML
    private Label email;

    @FXML
    private Button aggiorna;

    @FXML
    private Button elimina;

    @FXML
    private Button nuovaEmail;

    @FXML
    private ListView<Email> listaEmail;

    @FXML
    private Pane pannelloVuoto;

    @FXML
    public void aggiornaListaEmail(ActionEvent actionEvent) {

    }

    @FXML
    public void eliminaEmail(ActionEvent actionEvent) {

    }

    @FXML
    public void scriviNuovaEmail(ActionEvent actionEvent) {

    }
}