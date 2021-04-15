package common;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

/**
 * @author Raul Palade
 * @project Email Server
 * @date 29/03/2021
 */
public class Email implements Serializable {
    private StringProperty ID = new SimpleStringProperty();
    private StringProperty sender = new SimpleStringProperty();
    private ListProperty<String> cc = new SimpleListProperty<>();
    private StringProperty subject = new SimpleStringProperty();
    private StringProperty message = new SimpleStringProperty();
    private StringProperty date = new SimpleStringProperty();

    public Email(String ID, String sender, ObservableList<String> cc, String subject, String message, String date) {
        this.ID.set(ID);
        this.sender.set(sender);
        this.cc.set(cc);
        this.subject.set(subject);
        this.message.set(message);
        this.date.set(date);
    }

    public String getID() {
        return ID.get();
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public StringProperty IDProperty() {
        return ID;
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

    public ObservableList<String> getCc() {
        return cc.get();
    }

    public void setCc(ObservableList<String> cc) {
        this.cc.set(cc);
    }

    public ListProperty<String> ccProperty() {
        return cc;
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

    public String getStringAddresses() {
        StringBuilder s = new StringBuilder();
        Iterator<String> it = cc.iterator();
        while (it.hasNext()) {
            s.append(it.next());
            if (it.hasNext()) {
                s.append("; ");
            }
        }

        return s.toString();
    }

    private void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(getID());
            out.writeObject(getSender());
            writeListProperty(out);
            out.writeObject(getSubject());
            out.writeObject(getMessage());
            out.writeObject(getDate());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream in) {
        try {
            ID = new SimpleStringProperty((String) in.readObject());
            sender = new SimpleStringProperty((String) in.readObject());
            cc = new SimpleListProperty<>(readListProperty(in));
            subject = new SimpleStringProperty((String) in.readObject());
            message = new SimpleStringProperty((String) in.readObject());
            date = new SimpleStringProperty((String) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ListProperty<String> readListProperty(ObjectInputStream in) {
        ListProperty<String> listProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        try {
            int loop = in.readInt();
            for (int i = 0; i < loop; i++) {
                listProperty.add((String) in.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return listProperty;
    }

    private void writeListProperty(ObjectOutputStream out) {
        try {
            out.writeInt(cc.size());
            for (String elt : cc.getValue()) {
                out.writeObject(elt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Email{" +
                "ID=" + ID +
                ", sender=" + sender +
                ", cc=" + cc +
                ", subject=" + subject +
                ", message=" + message +
                ", date=" + date +
                '}';
    }
}