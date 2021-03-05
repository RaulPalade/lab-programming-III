package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class Email {
    private final IntegerProperty emailID = new SimpleIntegerProperty();
    private final StringProperty emailSender = new SimpleStringProperty();
    private final StringProperty emailRecipient = new SimpleStringProperty();
    private final StringProperty emailObject = new SimpleStringProperty();
    private final StringProperty emailText = new SimpleStringProperty();
    private final StringProperty emailDate = new SimpleStringProperty();

    public Email(int emailID, String emailSender, String emailRecipient, String emailObject, String emailText, String emailDate) {
        setEmailID(emailID);
        setEmailSender(emailSender);
        setEmailRecipient(emailRecipient);
        setEmailObject(emailObject);
        setEmailText(emailText);
        setEmailDate(emailDate);
    }

    public int getEmailID() {
        return emailID.get();
    }

    public IntegerProperty emailIDProperty() {
        return emailID;
    }

    public void setEmailID(int emailID) {
        this.emailID.set(emailID);
    }

    public String getEmailSender() {
        return emailSender.get();
    }

    public StringProperty emailSenderProperty() {
        return emailSender;
    }

    public void setEmailSender(String emailSender) {
        this.emailSender.set(emailSender);
    }

    public String getEmailRecipient() {
        return emailRecipient.get();
    }

    public StringProperty emailRecipientProperty() {
        return emailRecipient;
    }

    public void setEmailRecipient(String emailRecipient) {
        this.emailRecipient.set(emailRecipient);
    }

    public String getEmailObject() {
        return emailObject.get();
    }

    public StringProperty emailObjectProperty() {
        return emailObject;
    }

    public void setEmailObject(String emailObject) {
        this.emailObject.set(emailObject);
    }

    public String getEmailText() {
        return emailText.get();
    }

    public StringProperty emailTextProperty() {
        return emailText;
    }

    public void setEmailText(String emailText) {
        this.emailText.set(emailText);
    }

    public String getEmailDate() {
        return emailDate.get();
    }

    public StringProperty emailDateProperty() {
        return emailDate;
    }

    public void setEmailDate(String emailDate) {
        this.emailDate.set(emailDate);
    }
}