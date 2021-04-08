package client;

import common.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class EmailClientController implements Initializable, Serializable {
    @FXML
    public HBox centerHBox;

    @FXML
    public HBox bottomHBox;

    @FXML
    private Label email;

    @FXML
    private Label statoServer;

    private final ButtonType si = new ButtonType("Si", ButtonBar.ButtonData.OK_DONE);

    @FXML
    private Button aggiorna;

    @FXML
    private Button elimina;

    @FXML
    private Button nuovaEmail;
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

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
    @FXML
    private Button logout;
    @FXML
    private TabPane tabPane;

    private MailBox mailBox;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }


    public void initModel(MailBox mailBox, Stage stage) {
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

        listaEmailInviate.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(email.getSubject());
                }
            }
        });

        listaEmailRicevute.setCellFactory(listView -> new ListCell<>() {
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
    public void eliminaEmail() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        alert.setHeaderText("Sei sicuro di eliminare la mail?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(no) == si) {
            Connection connection = new Connection(mailBox);
            connection.deleteEmail(listaEmailRicevute.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    public void scriviNuovaEmail() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ScriviEmail.fxml"));
        pannelloVuoto.getChildren().add(loader.load());
        ScriviEmailController scriviEmailController = loader.getController();
        scriviEmailController.initModel(mailBox);
        centerHBox.getChildren().remove(nuovaEmail);
    }

    @FXML
    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        alert.setHeaderText("Sei sicuro di spegnere il server?");
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
}