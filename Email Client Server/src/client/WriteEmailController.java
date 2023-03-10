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
public class WriteEmailController {
    @FXML
    private TextArea recipient;

    @FXML
    private TextArea subject;

    @FXML
    private TextArea message;

    private final ButtonType si = new ButtonType("Si", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
    private MailBox mailBox;
    private Pane pane;
    private Tab tabReceivedEmails;
    private Tab tabSendedEmails;

    public void initModel(MailBox mailBox, Pane pane, Tab tabReceivedEmails, Tab tabSendedEmails) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.pane = pane;
        this.tabReceivedEmails = tabReceivedEmails;
        this.tabSendedEmails = tabSendedEmails;
    }


    /**
     * Reply Email Contructor
     *
     * @param mailBox
     * @param pane
     * @param recipient
     * @param message
     * @param subject
     */
    public void initModel(MailBox mailBox, Pane pane, String recipient, String message, String subject, Tab tabReceivedEmails, Tab tabSendedEmails) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.pane = pane;
        this.tabReceivedEmails = tabReceivedEmails;
        this.tabSendedEmails = tabSendedEmails;
        this.recipient.setText(recipient);
        this.subject.setText(subject);
        this.message.setText(message);
    }

    /**
     * Forward Constructor
     *
     * @param mailBox
     * @param pane
     * @param message
     * @param subject
     */
    public void initModel(MailBox mailBox, Pane pane, String message, String subject, Tab tabReceivedEmails, Tab tabSendedEmails) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.pane = pane;
        this.tabReceivedEmails = tabReceivedEmails;
        this.tabSendedEmails = tabSendedEmails;
        this.message.setText(message);
        this.subject.setText(subject);
        this.message.setEditable(false);
    }

    @FXML
    public void deleteCurrectEmail() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        alert.setHeaderText("Sei sicuro di voler cancellare questa email?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(no) == si) {
            pane.getChildren().get(0).setVisible(true);
            pane.getChildren().get(1).setVisible(false);
            pane.getChildren().remove(2);
            tabReceivedEmails.setDisable(false);
            tabSendedEmails.setDisable(false);
        }
    }

    @FXML
    public void sendEmail() {
        String[] emailListSplitted = recipient.getText().toLowerCase().replaceAll("\\s+", "").split(";");
        ArrayList<String> emailList = new ArrayList<>(Arrays.asList(emailListSplitted));
        Alert alert;

        if (formCompiled()) {
            if (validEmails(emailList)) {
                Connection connection = new Connection(mailBox);
                String id = connection.getEmailId();
                String sender = mailBox.getUserEmail();
                ObservableList<String> list = FXCollections.observableArrayList();
                list.addAll(emailListSplitted);
                boolean userEmailExists = true;
                for (String userEmail : list) {
                    connection = new Connection(mailBox);
                    if (!connection.checkIfUserExists(userEmail)) {
                        userEmailExists = false;
                    }
                }
                String subject = this.subject.getText();
                String message = this.message.getText();
                GregorianCalendar date = new GregorianCalendar();
                if (userEmailExists) {
                    connection = new Connection(mailBox);
                    Email email = new Email(id, sender, list, subject, message, date.getTime().toString());
                    boolean sendedCorrectly = connection.sendEmail(email);
                    if (sendedCorrectly) {
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Email inviata");
                        alert.showAndWait();
                        pane.getChildren().get(0).setVisible(true);
                        pane.getChildren().get(1).setVisible(false);
                        pane.getChildren().remove(2);
                        tabReceivedEmails.setDisable(false);
                        tabSendedEmails.setDisable(false);
                    } else {
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Non ?? possibile inviare l'email al momento");
                        alert.showAndWait();
                    }

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

    private boolean formCompiled() {
        boolean formCompiled = true;
        TextArea[] form = {recipient, subject};

        for (TextArea t : form) {
            if (t.getText().isBlank()) {
                t.setStyle("-fx-text-box-border: #ff0033; -fx-focus-color: #ff0033;");
                formCompiled = false;
            }
        }

        return formCompiled;
    }

    private boolean validEmails(ArrayList<String> emails) {
        boolean validEmails = false;

        for (String email : emails) {
            if (Utils.validateEmail(email)) {
                validEmails = true;
            }
        }

        return validEmails;
    }

    @FXML
    private void clear() {
        TextArea[] form = {recipient, subject};
        for (TextArea t : form) {
            if (t.isFocused()) {
                t.setStyle("-fx-text-box-border: #d0d0d0; -fx-focus-color: #d0d0d0;");
            }
        }
    }
}