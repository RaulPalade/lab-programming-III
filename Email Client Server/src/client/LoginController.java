package client;

import common.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Raul Palade
 * @project Email Client Server
 * @date 31/03/2021
 */
public class LoginController {
    @FXML
    private TextField email;

    @FXML
    private Button login;

    @FXML
    private Label errore;

    @FXML
    public void login() throws IOException {
        Stage s = new Stage();
        boolean connectedCorrectly;
        errore.setStyle("-fx-border-color: #ff0033; -fx-background-color: #ffbaba;");
        if (formCompilato()) {
            if (Utils.validateEmail(email.getText().trim())) {
                MailBox mailBox = new MailBox(email.getText());
                Connection connection = new Connection(mailBox);
                connectedCorrectly = connection.login(email.getText());
                if (connectedCorrectly) {
                    Stage stage = (Stage) login.getScene().getWindow();
                    stage.close();
                    FXMLLoader emailClient = new FXMLLoader(getClass().getResource("/client/EmailClient.fxml"));
                    Parent root = emailClient.load();
                    EmailClientController emailClientController = emailClient.getController();
                    emailClientController.initModel(mailBox, stage);
                    s.setTitle("Email Client");
                    s.setScene(new Scene(root));
                    s.setResizable(false);
                    s.show();
                } else {
                    errore.setText("Login non riuscito, server offline");
                }
            } else {
                errore.setText("Formato Email incorretto");
            }
        } else {
            errore.setText("Compila tutti i campi in rosso");
        }
    }

    private boolean formCompilato() {
        boolean formCompilato = true;
        TextField[] form = {email};

        for (TextField t : form) {
            if (t.getText().isBlank()) {
                t.setStyle("-fx-text-box-border: #ff0033; -fx-focus-color: #ff0033;");
                formCompilato = false;
            }
        }

        return formCompilato;
    }

    @FXML
    private void clear() {
        TextField[] form = {email};
        for (TextField t : form) {
            if (t.isFocused()) {
                t.setStyle("-fx-text-box-border: #d0d0d0; -fx-focus-color: #d0d0d0;");
                errore.setText("");
                errore.setStyle("-fx-text-box-border: #d0d0d0; -fx-focus-color: #d0d0d0;");
            }
        }
    }
}