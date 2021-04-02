package client;

import common.Email;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * @author Raul Palade
 * @project Email Server
 * @date 29/03/2021
 */
public class MailBox {
    private final transient String username;
    private final ObservableList<common.Email> emailListSended;
    private final ObservableList<common.Email> emailListReceived;
    private final ObjectProperty<common.Email> currentEmail = new SimpleObjectProperty<>(null);
    private int lastIdReceived;
    private int lastIdSend;

    public MailBox(String username) {
        this.username = username;
        emailListSended = FXCollections.observableArrayList();
        emailListReceived = FXCollections.observableArrayList();
        this.lastIdReceived = -1;
        this.lastIdSend = -1;
    }

    public String getUsername() {
        return username;
    }

    public ObservableList<common.Email> getEmailListSended() {
        return emailListSended;
    }

    public ObservableList<common.Email> getEmailListReceived() {
        return emailListReceived;
    }

    public int getLastIdReceived() {
        return lastIdReceived;
    }

    public int getLastIdSend() {
        return lastIdSend;
    }

    public ObjectProperty<common.Email> currentEmailProperty() {
        return currentEmail;
    }

    public final common.Email getCurrentEmail() {
        return currentEmailProperty().get();
    }

    public final void setCurrentEmail(common.Email email) {
        currentEmailProperty().set(email);
    }

    public void addSendedEmails(ArrayList<common.Email> emails) {
        if (!emails.isEmpty()) {
            lastIdSend = Integer.parseInt(emails.get(emails.size() - 1).getId());
            emailListSended.addAll(emails);
        }
    }

    public void addReceivedEmails(ArrayList<Email> emails) {
        if (!emails.isEmpty()) {
            lastIdReceived = Integer.parseInt(emails.get(emails.size() - 1).getId());
            Platform.runLater(() -> emailListReceived.addAll(emails));
        }
    }
}