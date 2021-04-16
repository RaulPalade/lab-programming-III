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

    private final Label labelIniziale = new Label("Seleziona una mail o scrivi una nuova email");

    private MailBox mailBox;

    public void initModel(MailBox mailBox, Stage stage) throws IOException {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.mailBox.setCurrentEmail(null);
        email.setText(mailBox.getUsername());
        statoServer.setText("controllo...");
        listaEmailInviate.setItems(mailBox.getEmailListSended());
        listaEmailRicevute.setItems(mailBox.getEmailListReceived());
        pannelloVuoto.getChildren().get(0).setVisible(false);
        labelIniziale.setLayoutX(88);
        labelIniziale.setLayoutY(235);
        labelIniziale.setStyle("-fx-font-size: 20px");
        pannelloVuoto.getChildren().add(labelIniziale);

        stage.setOnCloseRequest(event -> {
            Connection connection = new Connection(mailBox);
            connection.logout(mailBox.getUsername());
            stage.close();
        });

        loadEmails();

        ThreadRefreshEmailList threadRefreshEmailList = new ThreadRefreshEmailList(mailBox, statoServer);
        try {
            System.out.println("start");
            threadRefreshEmailList.setDaemon(true);
            threadRefreshEmailList.start();
        } catch (Exception e) {
            System.out.println("Interrupt");
            threadRefreshEmailList.interrupt();
        }
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

        listaEmailRicevute.getSelectionModel().selectedItemProperty().addListener((observable, oldEmail, newEmail) -> {
            if (pannelloVuoto.getChildren().contains(labelIniziale)) {
                pannelloVuoto.getChildren().remove(labelIniziale);
                pannelloVuoto.getChildren().get(0).setVisible(true);
            }
            mailBox.setCurrentEmail(newEmail);
        });

        listaEmailInviate.getSelectionModel().selectedItemProperty().addListener((observable, oldEmail, newEmail) -> {
            if (pannelloVuoto.getChildren().contains(labelIniziale)) {
                pannelloVuoto.getChildren().remove(labelIniziale);
                pannelloVuoto.getChildren().get(0).setVisible(true);
            }
            mailBox.setCurrentEmail(newEmail);
        });

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
        pannelloVuoto.getChildren().get(0).setVisible(false);
        pannelloVuoto.getChildren().add(loader.load());
        ScriviEmailController scriviEmailController = loader.getController();
        scriviEmailController.initModel(mailBox, pannelloVuoto);
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
                if (connection.deleteEmail(mailBox.getCurrentEmail())) {
                    if (tabPostaInArrivo.isSelected()) {
                        mailBox.deleteReceivedEmail(mailBox.getCurrentEmail());
                    } else if (tabPostaInviata.isSelected()) {
                        mailBox.deleteSendedEmail(mailBox.getCurrentEmail());
                    }
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Impossibile eliminare l'email al momento");
                    alert.show();
                }
            }
        }
    }

    @FXML
    public void rispondiEmail() throws IOException {
        String mittente = mailBox.getCurrentEmail().getSender();
        String testoEmail = mailBox.getCurrentEmail().getMessage() + "\n------------------";
        String oggettoEmail = mailBox.getCurrentEmail().getSubject();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ScriviEmail.fxml"));
        pannelloVuoto.getChildren().get(0).setVisible(false);
        pannelloVuoto.getChildren().add(loader.load());
        ScriviEmailController scriviEmailController = loader.getController();
        scriviEmailController.initModel(mailBox, pannelloVuoto, mittente, testoEmail, oggettoEmail);
    }

    @FXML
    public void rispondiEmailATutti() throws IOException {
        String[] cc = mailBox.getCurrentEmail().getCc().toArray(new String[0]);
        String destinatario = Arrays.toString(cc).replace(",", ";").replace("[", "").replace("]", "");
        System.out.println(destinatario);
        String testoEmail = mailBox.getCurrentEmail().getMessage() + "\n------------------";
        String oggettoEmail = mailBox.getCurrentEmail().getSubject();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ScriviEmail.fxml"));
        pannelloVuoto.getChildren().get(0).setVisible(false);
        pannelloVuoto.getChildren().add(loader.load());
        ScriviEmailController scriviEmailController = loader.getController();
        scriviEmailController.initModel(mailBox, pannelloVuoto, destinatario, testoEmail, oggettoEmail);
    }

    @FXML
    public void inoltraEmail() throws IOException {
        String testoEmail = "------Messaggio inoltrato------\n" +
                "Da: <" + mailBox.getCurrentEmail().getSender() + ">\n" +
                "Data: " + mailBox.getCurrentEmail().getDate() + "\n" +
                "Oggetto: " + mailBox.getCurrentEmail().getSubject() + "\n" +
                "A: " + mailBox.getCurrentEmail().getStringAddresses() + "\n\n" +
                mailBox.getCurrentEmail().getMessage();
        String oggettoEmail = "Fwd: " + mailBox.getCurrentEmail().getSubject();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ScriviEmail.fxml"));
        pannelloVuoto.getChildren().get(0).setVisible(false);
        pannelloVuoto.getChildren().add(loader.load());
        ScriviEmailController scriviEmailController = loader.getController();
        scriviEmailController.initModel(mailBox, pannelloVuoto, testoEmail, oggettoEmail);
    }
}