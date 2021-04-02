package client;

import common.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class EmailClientController implements Initializable {

    @FXML
    public HBox topHBox;

    @FXML
    public HBox centerHBox;

    @FXML
    public HBox bottomHBox;

    @FXML
    private Label email;

    @FXML
    private Label statoServer;

    @FXML
    private Button aggiorna;

    @FXML
    private Button elimina;

    @FXML
    private Button nuovaEmail;

    @FXML
    private Tab postaInArrivo;

    @FXML
    private Tab postaInviata;

    @FXML
    private ListView<Email> listaEmailInviate;

    @FXML
    private ListView<Email> listaEmailRicevute;

    @FXML
    private Pane pannelloVuoto;

    private MailBox mailBox;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statoServer.setText("Stato Server: Offline");
    }

    public void initModel(MailBox mailBox, Stage stage) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.stage = stage;
        this.mailBox.setCurrentEmail(null);
    }

    @FXML
    public void aggiornaListaEmail(ActionEvent actionEvent) {

    }

    @FXML
    public void eliminaEmail(ActionEvent actionEvent) {

    }

    @FXML
    public void scriviNuovaEmail(ActionEvent actionEvent) throws IOException {
        Pane scriviEmail = FXMLLoader.load(getClass().getResource("/client/ScriviEmail.fxml"));
        pannelloVuoto.getChildren().add(scriviEmail);
        centerHBox.getChildren().remove(nuovaEmail);
    }

    @FXML
    public void logout(ActionEvent actionEvent) {

    }
}