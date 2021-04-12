package client;

import common.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class EmailClientController implements Serializable {
    private final ButtonType si = new ButtonType("Si", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

    @FXML
    private Label email;

    @FXML
    private Label statoServer;
    @FXML
    private HBox centerHBox;

    @FXML
    private Button aggiorna;

    @FXML
    private Button nuovaEmail;
    @FXML
    private HBox bottomHBox;
    @FXML
    private Button logout;
    @FXML
    private TabPane tabPane;

    @FXML
    private ListView<Email> listaEmailInviate;

    @FXML
    private ListView<Email> listaEmailRicevute;
    @FXML
    private Tab tabPostaInArrivo;
    @FXML
    private Tab tabPostaInviata;
    @FXML
    private Pane pannelloVuoto;
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
    private Label cc;
    @FXML
    private Label oggetto;
    @FXML
    private Label data;
    @FXML
    private TextArea testoEmail;

    private MailBox mailBox;
    private Stage stage;

    public void initModel(MailBox mailBox, Stage stage) throws IOException {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.stage = stage;
        this.mailBox.setCurrentEmail(null);
        email.setText(mailBox.getUsername());
        statoServer.setText("controllo...");
        listaEmailInviate.setItems(mailBox.getEmailListSended());
        listaEmailRicevute.setItems(mailBox.getEmailListReceived());
        loadEmails();
    }

    @FXML
    private void loadEmails() {
        for (ListView<Email> emailListView : Arrays.asList(listaEmailRicevute, listaEmailInviate)) {
            emailListView.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(Email email, boolean empty) {
                    super.updateItem(email, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(email.getSubject() + " " + email.getDate());
                    }
                }
            });
        }

        listaEmailRicevute.getSelectionModel().selectedItemProperty().addListener((observable, oldEmail, newEmail) ->
                mailBox.setCurrentEmail(newEmail));

        listaEmailInviate.getSelectionModel().selectedItemProperty().addListener((observable, oldEmail, newEmail) ->
                mailBox.setCurrentEmail(newEmail));

        mailBox.currentEmailProperty().addListener((observable, oldEmail, newEmail) -> {
            if (oldEmail != null) {
                mittente.setText(oldEmail.getSender());
                oggetto.setText(oldEmail.getSubject());
                testoEmail.setText(oldEmail.getMessage());
                data.setText(oldEmail.getDate());
            }
            if (newEmail == null) {
                mittente.setText("");
                oggetto.setText("");
                testoEmail.setText("");
                data.setText("");
            } else {
                mittente.setText(newEmail.getSender());
                oggetto.setText(newEmail.getSubject());
                testoEmail.setText(newEmail.getMessage());
                data.setText(newEmail.getDate());
            }
        });
    }

    @FXML
    public void aggiornaListaEmail() {
        try {
            Connection connection = new Connection(mailBox);
            connection.loadReceivedEmails(mailBox.getLastIdReceived());
            connection = new Connection(mailBox);
            connection.loadSendedEmails(mailBox.getLastIdSend());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        alert.setHeaderText("Sei sicuro di voler uscire?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(no) == si) {
            Connection connection = new Connection(mailBox);
            connection.logout(mailBox.getUsername());
            Stage stage = (Stage) logout.getScene().getWindow();
            stage.close();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/LoginClient.fxml"));
                Stage primaryStage = new Stage();
                Parent root = loader.load();
                primaryStage.setTitle("Email Client Login");
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void scriviNuovaEmail() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ScriviEmail.fxml"));
        pannelloVuoto.getChildren().clear();
        pannelloVuoto.getChildren().add(loader.load());
        ScriviEmailController scriviEmailController = loader.getController();
        scriviEmailController.initModel(mailBox);
    }

    @FXML
    public void eliminaEmail() {
        if (mailBox.getCurrentEmail() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
            alert.setHeaderText("Sei sicuro di eliminare la mail?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(no) == si) {
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
    }

    @FXML
    public void rispondiEmail() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.show();
    }

    @FXML
    public void rispondiEmailATutti() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.show();
    }

    @FXML
    public void inoltraEmail() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.show();
    }
}