package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.DataModel;
import model.Email;

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
    private ListView<Email> listaEmail;

    @FXML
    private Pane pannelloVuoto;

    private DataModel dataModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        email.setText("E-mail Account: raul.palade@edu.unito.it");
        statoServer.setText("Stato Server: Offline");
    }

    public void initModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    @FXML
    public void aggiornaListaEmail(ActionEvent actionEvent) {

    }

    @FXML
    public void eliminaEmail(ActionEvent actionEvent) {

    }

    @FXML
    public void scriviNuovaEmail(ActionEvent actionEvent) throws IOException {
        Pane scriviEmail = FXMLLoader.load(getClass().getResource("/view/ScriviEmail.fxml"));
        pannelloVuoto.getChildren().add(scriviEmail);
        centerHBox.getChildren().remove(nuovaEmail);

        /*FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/ScriviEmail.fxml"));
        Pane scriviEmail = loader.load();
        ScriviEmailController controller = loader.getController();
        controller.initModel(dataModel);
        pannelloVuoto.getChildren().add(scriviEmail);*/
    }
}