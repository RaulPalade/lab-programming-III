package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class LeggiEmailController {
    @FXML
    public Pane leggiEmailPane;

    @FXML
    private Button elimina;

    @FXML
    private Button rispondi;

    @FXML
    private Button rispondiATutti;

    @FXML
    private Button inoltra;

    @FXML
    private Label mittente;

    @FXML
    private Label oggetto;

    @FXML
    private Label testoEmail;

    private Tab tabPostaInArrivo;
    private Tab tabPostaInviata;

    private MailBox mailBox;

    public void initModel(MailBox mailBox, Tab tabPostaInArrivo, Tab tabPostaInviata) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.tabPostaInArrivo = tabPostaInArrivo;
        this.tabPostaInviata = tabPostaInviata;

        mailBox.currentEmailProperty().addListener((observable, oldEmail, newEmail) -> {
            System.out.println(oldEmail);
            System.out.println(newEmail);
            if (oldEmail != null) {
                mittente.setText(oldEmail.getSender());
                oggetto.setText(oldEmail.getSubject());
                testoEmail.setText(oldEmail.getMessage());
            }
            if (newEmail == null) {
                mittente.setText("");
                oggetto.setText("");
                testoEmail.setText("");
            } else {
                mittente.setText(newEmail.getSender());
                oggetto.setText(newEmail.getSubject());
                testoEmail.setText(newEmail.getMessage());
            }
        });
    }

    @FXML
    public void eliminaEmail(ActionEvent actionEvent) {
        if (mailBox.getCurrentEmail() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.show();
        } else {
            Connection connection = new Connection(mailBox);
            if (connection.deleteEmail(mailBox.getCurrentEmail()).equalsIgnoreCase("OK")) {
                if (tabPostaInArrivo.isSelected()) {
                    mailBox.deleteReceivedEmail(mailBox.getCurrentEmail());
                } else if (tabPostaInviata.isSelected()) {
                    mailBox.deleteSendedEmail(mailBox.getCurrentEmail());
                }
            }
        }
    }

    @FXML
    public void rispondiEmail(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.show();
    }

    @FXML
    public void rispondiEmailATutti(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.show();
    }

    @FXML
    public void inoltraEmail(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.show();
    }
}