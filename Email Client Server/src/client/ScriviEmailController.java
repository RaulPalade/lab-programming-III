package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class ScriviEmailController {
    public static int id = 0;

    @FXML
    private TextArea destinatario;

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
    public void handleInviaEmail(ActionEvent actionEvent) {
        String[] emailSplit = destinatario.getText().toLowerCase().replaceAll("\\s+", "").split(";");
        ArrayList<String> listaEmail = new ArrayList<>(Arrays.asList(emailSplit));
        Alert alert;

        /*if (formCompilato()) {
            if (emailValide(listaEmail)) {
               *//* boolean mailInviata = dataModel.richiediInvioEmail(listaEmail, oggetto.getText(), testoEmail.getText());*//*
                if (mailInviata) {
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Email Inviata");
                } else {
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Email non inviata");
                }
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Formato email sbagliato");
            }
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Compilare tutti i campi in rosso correttamente");
        }
        alert.show();*/
    }

    private boolean formCompilato() {
        boolean formCompilato = true;
        TextArea[] form = {destinatario, oggetto};

        for (TextArea t : form) {
            if (t.getText().isBlank()) {
                t.setStyle("-fx-text-box-border: #ff0033; -fx-focus-color: #ff0033;");
                formCompilato = false;
            }
        }

        return formCompilato;
    }

    private boolean emailValide(ArrayList<String> listaEmail) {
        boolean emailValide = false;

        for (String email : listaEmail) {
            if (Utils.validateEmail(email)) {
                emailValide = true;
            }
        }

        return emailValide;
    }

    @FXML
    private void clear() {
        TextArea[] form = {destinatario, oggetto};
        for (TextArea t : form) {
            if (t.isFocused()) {
                t.setStyle("-fx-text-box-border: #d0d0d0; -fx-focus-color: #d0d0d0;");
            }
        }
    }
}