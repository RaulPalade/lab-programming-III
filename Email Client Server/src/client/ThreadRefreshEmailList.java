package client;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * @author Raul Palade
 * @project Email Client Server
 * @date 14/04/2021
 */
public class ThreadRefreshEmailList extends Thread {
    private final MailBox mailBox;
    private final Label statoServer;

    public ThreadRefreshEmailList(MailBox mailBox, Label statoServer) {
        this.mailBox = mailBox;
        this.statoServer = statoServer;
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    @Override
    public void run() {
        boolean loggedIn = true;
        while (true) {
            try {
                Connection connection = new Connection(mailBox);
                if (!loggedIn) {
                    boolean loaded = connection.login(mailBox.getUsername());
                    if (loaded) {
                        connection = new Connection(mailBox);
                        updateEmailList(connection);
                        loggedIn = true;
                        Platform.runLater(() -> statoServer.setText("Server Online"));
                    } else {
                        loggedIn = false;
                    }
                } else {
                    Platform.runLater(() -> statoServer.setText("Server Online"));
                    updateEmailList(connection);
                }
            } catch (Exception e) {
                loggedIn = false;
                Platform.runLater(() -> statoServer.setText("Server Offline"));
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
        connection.loadSendedEmails(mailBox.getLastIdSend());
        connection = new Connection(mailBox);
        connection.loadReceivedEmails(mailBox.getLastIdReceived());
    }
}