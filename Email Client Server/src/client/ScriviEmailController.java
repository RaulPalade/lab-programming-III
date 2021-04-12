package client;

import common.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class ScriviEmailController {
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

    private final ButtonType si = new ButtonType("Si", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
    private MailBox mailBox;

    public void initModel(MailBox mailBox) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
    }

    @FXML
    public void eliminaMailCorrente(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        alert.setHeaderText("Sei sicuro di cancellare questa email?");
        alert.show();
        alert.setOnCloseRequest(event -> {
            // TODO (1): Aggiungere il pulsante nuova email di EmailClient.fxml
        });
    }

    @FXML
    public void handleInviaEmail() {
        String[] emailSplit = destinatario.getText().toLowerCase().replaceAll("\\s+", "").split(";");
        ArrayList<String> listaEmail = new ArrayList<>(Arrays.asList(emailSplit));
        Alert alert;

        if (formCompilato()) {
            if (emailValide(listaEmail)) {
                Connection connection = new Connection(mailBox);
                String id = connection.getEmailId();
                System.out.println("ID PER NUOVA EMAIL: " + id);
                String mittente = mailBox.getUsername();
                ObservableList<String> list = FXCollections.observableArrayList();
                list.addAll(emailSplit);
                boolean correct = true;
                for (String s : list) {
                    connection = new Connection(mailBox);
                    if (!connection.controlUsername(s)) {
                        correct = false;
                    }
                }
                String oggettoEmail = oggetto.getText();
                String messaggioEmail = testoEmail.getText();
                GregorianCalendar date = new GregorianCalendar();
                if (correct) {
                    connection = new Connection(mailBox);
                    Email email = new Email(id, mittente, list, oggettoEmail, messaggioEmail, date.getTime().toString());
                    connection.sendEmail(email);
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Email inviata");
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Email inesistenti");
                }
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Formato email sbagliato");
            }
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Compilare tutti i campi in rosso correttamente");
        }
        alert.show();
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