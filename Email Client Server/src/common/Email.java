package common;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.io.Serializable;

/**
 * @author Raul Palade
 * @project Email Server
 * @date 29/03/2021
 */
public class Email implements Serializable {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty sender = new SimpleStringProperty();
    private final ListProperty<String> addesses = new SimpleListProperty<>();
    private final StringProperty subject = new SimpleStringProperty();
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();

    public Email(String id, String sender, ListProperty<String> addesses, String subject, String message, String date) {
        this.id.set(id);
        this.sender.set(sender);
        this.addesses.set(addesses);
        this.subject.set(subject);
        this.message.set(message);
        this.date.set(date);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getSender() {
        return sender.get();
    }

    public void setSender(String sender) {
        this.sender.set(sender);
    }

    public StringProperty senderProperty() {
        return sender;
    }

    public ObservableList<String> getAddesses() {
        return addesses.get();
    }

    public void setAddesses(ObservableList<String> addesses) {
        this.addesses.set(addesses);
    }

    public ListProperty<String> addessesProperty() {
        return addesses;
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public StringProperty messageProperty() {
        return message;
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public StringProperty dateProperty() {
        return date;
    }
}