package server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

/**
 * @author Raul Palade
 * @project Email Client Server
 * @date 03/04/2021
 */
public class ServerModel implements Serializable {
    private ObservableList<String> log;

    public ServerModel() {
        log = FXCollections.observableArrayList();
    }

    public ObservableList<String> getLog() {
        return log;
    }

    public void setLog(ObservableList<String> log) {
        this.log = log;
    }

    public void addToLog(String newAction) {
        log.add(newAction);
    }
}