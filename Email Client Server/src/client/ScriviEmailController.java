package client;

import common.Email;
import common.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Optional;

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
    private Pane pane;

    public void initModel(MailBox mailBox, Pane pane) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.pane = pane;
    }

    // RISPONDI
    public void initModel(MailBox mailBox, Pane pane, String mittente, String messaggio, String oggettoEmail) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.pane = pane;
        destinatario.setText(mittente);
        oggetto.setText(oggettoEmail);
        testoEmail.setText(messaggio);
    }

    // INOLTRA
    public void initModel(MailBox mailBox, Pane pane, String messaggio, String oggettoEmail) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.pane = pane;
        testoEmail.setText(messaggio);
        oggetto.setText(oggettoEmail);
        testoEmail.setEditable(false);
    }

    @FXML
    public void eliminaMailCorrente() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        alert.setHeaderText("Sei sicuro di voler cancellare questa email?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(no) == si) {
            pane.getChildren().remove(1);
            pane.getChildren().get(0).setVisible(true);
        }
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
                    alert.showAndWait();
                    pane.getChildren().remove(1);
                    pane.getChildren().get(0).setVisible(true);
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Email inesistenti");
                    alert.show();
                }
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Formato email sbagliato");
                alert.show();
            }
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Compilare tutti i campi in rosso correttamente");
            alert.show();
        }
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