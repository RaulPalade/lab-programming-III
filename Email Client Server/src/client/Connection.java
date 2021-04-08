package client;

import common.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

// TODO (2): Rimuovere il codice duplicato
// TODO (3): Creare una classe con variabili globali per ip, porta e operazioni tra client e server
// TODO (4): Creare per generazione utenti

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
            socket = new Socket("127.0.0.1", 7777);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
                System.out.println("LOGIN IN CORSO");
                loginCorrect = (boolean) in.readObject();
                System.out.println("LOGIN IN CORSO");
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

    public boolean controlUsername(String username) {
        boolean trovato = false;
        try {
            out.writeObject("CONTROL_USERNAME");
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
        System.out.println(mailBox.getUsername());
        int id = -1;
        try {
            out.writeObject("GET_EMAIL_ID");
            out.writeObject(mailBox.getUsername());
            id = (int) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return String.valueOf(id);
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
                out.writeObject("DELETE_EMAIL");
                out.writeObject(mailBox.getUsername());
                out.writeObject(email);
                deleted = (boolean) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
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
                int controllo = (int) in.readObject();
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
        } catch (IOException | ClassNotFoundException e) {
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
                int controllo = (int) in.readObject();
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