package client;

import common.Email;
import common.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static common.Operation.*;
import static common.Utils.IP;
import static common.Utils.PORT;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 16/03/2021
 */
public class Connection {
    private final MailBox mailBox;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Connection(MailBox mailBox) {
        this.mailBox = mailBox;
        socket = null;
        out = null;
        in = null;
        try {
            socket = new Socket(IP, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (out != null) {
                out.close();
            }

            if (in != null) {
                in.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String email) {
        boolean loginCorrect = false;
        try {
            if (out != null) {
                out.writeObject(LOGIN);
                out.writeObject(email);
                out.flush();
                loginCorrect = (boolean) in.readObject();
                System.out.println(loginCorrect);
            } else {
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return loginCorrect;
    }

    public void logout(String email) {
        try {
            if (out != null) {
                out.writeObject(LOGOUT);
                out.writeObject(email);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public boolean controlUsername(String username) {
        boolean trovato = false;
        try {
            out.writeObject(CONTROL_USERNAME);
            out.writeObject(username);
            out.writeObject(mailBox.getUsername());
            out.flush();
            trovato = (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return trovato;
    }

    public String getEmailId() {
        int id = Integer.MIN_VALUE;
        try {
            out.writeObject(GET_EMAIL_ID);
            out.writeObject(mailBox.getUsername());
            out.flush();
            id = (int) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return String.valueOf(id);
    }

    // TODO (1): Add boolean to control if sended correctly
    public void sendEmail(Email email) {
        try {
            out.writeObject(SEND_EMAIL);
            out.writeObject(mailBox.getUsername());
            out.writeObject(email);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public boolean deleteEmail(Email email) {
        boolean deletedCorrectly = false;
        try {
            if (out != null) {
                out.writeObject(DELETE_EMAIL);
                out.writeObject(mailBox.getUsername());
                out.writeObject(email);
                deletedCorrectly = (boolean) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return deletedCorrectly;
    }

    public void loadReceivedEmails(int lastId) {
        ArrayList<Email> receivedEmails = getEmails(lastId, LOAD_RECEIVED_EMAILS);
        mailBox.addReceivedEmails(receivedEmails);
    }

    public void loadSendedEmails(int lastId) {
        ArrayList<Email> sendedEmails = getEmails(lastId, LOAD_SENDED_EMAILS);
        mailBox.addSendedEmails(sendedEmails);
    }

    private ArrayList<Email> getEmails(int lastId, Operation operation) {
        ArrayList<Email> emails = new ArrayList<>();
        try {
            out.writeObject(operation);
            out.writeObject(mailBox.getUsername());
            out.writeObject(lastId);
            out.flush();
            int controllo = (int) in.readObject();
            while (controllo-- != 0) {
                Object obj = in.readObject();
                if (obj instanceof Email) {
                    Email email = (Email) obj;
                    emails.add(email);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return emails;
    }
}