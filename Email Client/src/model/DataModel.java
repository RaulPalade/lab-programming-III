package model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 06/03/2021
 */
public class DataModel {
    private final ObservableList<Email> emailList = FXCollections.observableArrayList(email ->
            new Observable[]{email.emailSenderProperty(), email.emailDateProperty()});

    public void loadData(File file) {
        emailList.setAll(new Email("raul.palade@edu.unito.it", "06/03/2021"),
                new Email("daniele.pepe@edu.unito.it", "05/03/2021"));
    }

    public ObservableList<Email> getEmailList() {
        return emailList;
    }
}
