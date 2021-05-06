package client;

import common.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class EmailClientController implements Serializable {
    private final Label startLabel = new Label("Seleziona una mail o scrivi una nuova email");
    private final ButtonType si = new ButtonType("Si", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

    @FXML
    private Button logout;
    @FXML
    private ListView<Email> listaEmailInviate;
    @FXML
    private ListView<Email> listaEmailRicevute;
    @FXML
    private Label userEmail;
    @FXML
    private Label serverStatus;
    @FXML
    private Tab tabReceivedEmails;
    @FXML
    private Label cc;
    @FXML
    private Tab tabSendedEmails;
    @FXML
    private Pane emptyPane;
    @FXML
    private Label sender;
    @FXML
    private Label subject;
    @FXML
    private Label date;
    @FXML
    private TextArea message;

    private MailBox mailBox;

    public void initModel(MailBox mailBox, Stage stage) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.mailBox.setCurrentEmail(null);
        userEmail.setText(mailBox.getUserEmail());
        listaEmailInviate.setItems(mailBox.getEmailListSended());
        listaEmailRicevute.setItems(mailBox.getEmailListReceived());
        emptyPane.getChildren().get(0).setVisible(false);
        startLabel.setLayoutX(88);
        startLabel.setLayoutY(235);
        startLabel.setStyle("-fx-font-size: 20px");
        emptyPane.getChildren().add(startLabel);

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
            if (emptyPane.getChildren().contains(startLabel)) {
                emptyPane.getChildren().get(0).setVisible(true);
                emptyPane.getChildren().get(1).setVisible(false);
            }
            mailBox.setCurrentEmail(newEmail);
        });

        listaEmailInviate.getSelectionModel().selectedItemProperty().addListener((observable, oldEmail, newEmail) -> {
            if (emptyPane.getChildren().contains(startLabel)) {
                emptyPane.getChildren().get(0).setVisible(true);
                emptyPane.getChildren().get(1).setVisible(false);
            }
            mailBox.setCurrentEmail(newEmail);
        });

        mailBox.currentEmailProperty().addListener((observable, oldEmail, newEmail) -> {
            if (oldEmail != null) {
                sender.setText(oldEmail.getSender());
                cc.setText(oldEmail.getStringAddresses());
                subject.setText(oldEmail.getSubject());
                message.setText(oldEmail.getMessage());
                date.setText(oldEmail.getDate());
            }
            if (newEmail == null) {
                sender.setText("");
                cc.setText("");
                subject.setText("");
                message.setText("");
                date.setText("");
            } else {
                sender.setText(newEmail.getSender());
                cc.setText(newEmail.getStringAddresses());
                subject.setText(newEmail.getSubject());
                message.setText(newEmail.getMessage());
                date.setText(newEmail.getDate());
            }
        });

        stage.setOnCloseRequest(event -> {
            Connection connection = new Connection(mailBox);
            connection.logout(mailBox.getUserEmail());
            stage.close();
        });


        ThreadRefreshEmailList threadRefreshEmailList = new ThreadRefreshEmailList(mailBox, serverStatus);
        try {
            System.out.println("start");
            threadRefreshEmailList.start();
        } catch (Exception e) {
            System.out.println("Interrupt");
            threadRefreshEmailList.interrupt();
        }
    }

    @FXML
    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, si, no);
        alert.setHeaderText("Sei sicuro di voler uscire?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(no) == si) {
            Connection connection = new Connection(mailBox);
            connection.logout(mailBox.getUserEmail());
            Stage stage = (Stage) logout.getScene().getWindow();
            stage.close();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/LoginClientLayout.fxml"));
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
    public void writeNewEmail() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/WriteEmailLayout.fxml"));
        if (emptyPane.getChildren().get(0).isVisible()) {
            emptyPane.getChildren().get(0).setVisible(false);
        } else if (emptyPane.getChildren().get(0).isDisabled()) {
            emptyPane.getChildren().get(1).setVisible(false);
        }
        tabReceivedEmails.setDisable(true);
        tabSendedEmails.setDisable(true);
        emptyPane.getChildren().add(loader.load());
        WriteEmailController writeEmailController = loader.getController();
        writeEmailController.initModel(mailBox, emptyPane, tabReceivedEmails, tabSendedEmails);
    }

    @FXML
    public void deleteSelectedEmail() {
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
                    if (tabReceivedEmails.isSelected()) {
                        mailBox.deleteReceivedEmail(mailBox.getCurrentEmail());
                    } else if (tabSendedEmails.isSelected()) {
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
    public void replyEmail() throws IOException {
        String recipient = mailBox.getCurrentEmail().getSender();
        String message = mailBox.getCurrentEmail().getMessage() + "\n------------------";
        String subject = mailBox.getCurrentEmail().getSubject();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/WriteEmailLayout.fxml"));
        emptyPane.getChildren().get(0).setVisible(false);
        emptyPane.getChildren().add(loader.load());
        WriteEmailController writeEmailController = loader.getController();
        tabReceivedEmails.setDisable(true);
        tabSendedEmails.setDisable(true);
        writeEmailController.initModel(mailBox, emptyPane, recipient, message, subject, tabReceivedEmails, tabSendedEmails);
    }

    @FXML
    public void replyAllEmail() throws IOException {
        ArrayList<String> cc = new ArrayList<>(mailBox.getCurrentEmail().getCc());
        cc.remove(mailBox.getUserEmail());
        cc.add(mailBox.getCurrentEmail().getSender());
        String recipient1 = String.join("; ", cc);
        String message = mailBox.getCurrentEmail().getMessage() + "\n------------------";
        String subject = mailBox.getCurrentEmail().getSubject();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/WriteEmailLayout.fxml"));
        emptyPane.getChildren().get(0).setVisible(false);
        emptyPane.getChildren().add(loader.load());
        WriteEmailController writeEmailController = loader.getController();
        tabReceivedEmails.setDisable(true);
        tabSendedEmails.setDisable(true);
        writeEmailController.initModel(mailBox, emptyPane, recipient1, message, subject, tabReceivedEmails, tabSendedEmails);
    }

    @FXML
    public void forward() throws IOException {
        String message = "------Messaggio inoltrato------\n" +
                "Da: <" + mailBox.getCurrentEmail().getSender() + ">\n" +
                "Data: " + mailBox.getCurrentEmail().getDate() + "\n" +
                "Oggetto: " + mailBox.getCurrentEmail().getSubject() + "\n" +
                "A: " + mailBox.getCurrentEmail().getStringAddresses() + "\n\n" +
                mailBox.getCurrentEmail().getMessage();
        String subject = "Fwd: " + mailBox.getCurrentEmail().getSubject();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/WriteEmailLayout.fxml"));
        emptyPane.getChildren().get(0).setVisible(false);
        emptyPane.getChildren().add(loader.load());
        WriteEmailController writeEmailController = loader.getController();
        tabReceivedEmails.setDisable(true);
        tabSendedEmails.setDisable(true);
        writeEmailController.initModel(mailBox, emptyPane, message, subject, tabReceivedEmails, tabSendedEmails);
    }
}