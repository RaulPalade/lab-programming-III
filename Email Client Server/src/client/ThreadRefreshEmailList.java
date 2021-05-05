package client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * @author Raul Palade
 * @project Email Client Server
 * @date 14/04/2021
 */
public class ThreadRefreshEmailList extends Thread {
    private final MailBox mailBox;
    private final Label serverStatus;

    public ThreadRefreshEmailList(MailBox mailBox, Label serverStatus) {
        this.mailBox = mailBox;
        this.serverStatus = serverStatus;
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    @Override
    public void run() {
        boolean loggedIn = true;
        while (true) {
            try {
                Connection connection = new Connection(mailBox);
                if (!loggedIn) {
                    boolean loaded = connection.login(mailBox.getUserEmail());
                    if (loaded) {
                        connection = new Connection(mailBox);
                        updateEmailList(connection);
                        loggedIn = true;
                        Platform.runLater(() -> {
                            serverStatus.setText("Server Online");
                            Alert serverOnlineAlert = new Alert(Alert.AlertType.INFORMATION);
                            serverOnlineAlert.setHeaderText("Il server è tornato online, è possibile eseguire qualsiasi operazione");
                            serverOnlineAlert.show();
                        });
                    } else {
                        loggedIn = false;
                    }
                } else {
                    Platform.runLater(() -> serverStatus.setText("Server Online"));
                    updateEmailList(connection);
                }
            } catch (Exception e) {
                loggedIn = false;
                Platform.runLater(() -> {
                    serverStatus.setText("Server Offline");
                    Alert serverOfflineAlert = new Alert(Alert.AlertType.ERROR);
                    serverOfflineAlert.setHeaderText("Server offline, non è possibile eseguire alcuna operazione");
                    serverOfflineAlert.show();
                });
                e.printStackTrace();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateEmailList(Connection connection) {
        connection.getSendedEmails(mailBox.getLastIdSend());
        connection = new Connection(mailBox);
        connection.getReceivedEmails(mailBox.getLastIdReceived());
    }
}