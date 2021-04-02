package client;

import common.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 16/03/2021
 */
public class Connection {
    private final int PORT = 10000;
    private final MailBox mailBox;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Connection(MailBox mailBox) {
        this.mailBox = mailBox;
        try {
            socket = new Socket(InetAddress.getLoopbackAddress(), PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
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
                out.writeObject("LOGIN");
                out.writeObject(email);
                out.writeObject(mailBox.getUsername());
                out.flush();
                loginCorrect = in.readBoolean();
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return loginCorrect;
    }

    public void logout(String email) {
        try {
            if (out != null) {
                out.writeObject("LOGOUT");
                out.writeObject(email);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void sendEmail(Email email) {
        try {
            out.writeObject("SEND_EMAIL");
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
        boolean deleted = false;
        try {
            if (out != null) {
                out.writeObject("DELETE");
                out.writeObject(mailBox.getUsername());
                out.writeObject(email);
                deleted = in.readBoolean();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return deleted;
    }

    public void loadReceivedEmails(int lastId) {
        try {
            if (out != null) {
                out.writeObject("LOAD_RECEIVED_EMAILS");
                out.writeObject(mailBox.getUsername());
                out.writeObject(lastId);
                out.flush();
                int controllo = in.readInt();
                ArrayList<Email> receivedEmails = new ArrayList<>();
                while (controllo-- != 0) {
                    Object obj = in.readObject();
                    if (obj instanceof Email) {
                        Email email = (Email) obj;
                        receivedEmails.add(email);
                    }
                }
                mailBox.addReceivedEmails(receivedEmails);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void loadSendedEmails(int lastId) {
        try {
            if (out != null) {
                out.writeObject("LOAD_SENDED_EMAILS");
                out.writeObject(mailBox.getUsername());
                out.writeObject(lastId);
                out.flush();
                int controllo = in.readInt();
                ArrayList<Email> sendedEmails = new ArrayList<>();
                while (controllo-- != 0) {
                    Object obj = in.readObject();
                    if (obj instanceof Email) {
                        Email email = (Email) obj;
                        sendedEmails.add(email);
                    }
                }
                mailBox.addSendedEmails(sendedEmails);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
}