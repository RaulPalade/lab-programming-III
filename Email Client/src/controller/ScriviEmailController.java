package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private DataModel dataModel;

    public void initModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    @FXML
    public void eliminaMailCorrente(ActionEvent actionEvent) {
        ButtonType si = new ButtonType("Si", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        alert.setHeaderText("Sei sicuro di cancellare questa email?");
        alert.show();
        alert.setOnCloseRequest(event -> {
            // TODO (1): Aggiungere il pulsante nuova email di EmailClient.fxml
        });

    }

    @FXML
    public void inviaEmail(ActionEvent actionEvent) throws InterruptedException {
        if (checkForm()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Compilare tutti i campi in rosso");
            alert.show();
        }
    }

    private boolean checkForm() {
        boolean completo = false;
        TextArea[] form = {destinatario, cc, ccn, oggetto, testoEmail};

        for (TextArea t : form) {
            if (t.getText().isBlank()) {
                t.setStyle("-fx-text-box-border: #ff0033; -fx-focus-color: #ff0033;");
                completo = true;
            }
        }

        return completo;
    }

    @FXML
    private void clear() {
        TextArea[] form = {destinatario, cc, ccn, oggetto, testoEmail};
        for (TextArea t : form) {
            if (t.isFocused()) {
                t.setStyle("-fx-text-box-border: #d0d0d0; -fx-focus-color: #d0d0d0;");
            }
        }
    }
}