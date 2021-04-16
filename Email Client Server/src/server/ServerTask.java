package server;

import com.google.gson.*;
import common.Email;
import common.Operation;
import org.hildan.fxgson.FxGson;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Raul Palade
 * @project Email Server
 * @date 30/03/2021
 */
public class ServerTask implements Runnable {
    private final Socket clientSocket;
    private final ServerModel serverModel;
    private final Lock writeLock;
    private final Lock readLock;

    private final File fileEmails = new File("resources/files/emails.json");
    private final File fileUsers = new File("resources/files/users.json");
    private final ArrayList<String> usernameList = new ArrayList<>();
    private final ArrayList<Email> receivedEmails = new ArrayList<>();
    private final ArrayList<Email> sendedEmails = new ArrayList<>();
    private JsonArray jsonArray = new JsonArray();

    public ServerTask(Socket clientSocket, ServerModel serverModel) {
        this.clientSocket = clientSocket;
        this.serverModel = serverModel;
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        readLock = reentrantReadWriteLock.readLock();
        writeLock = reentrantReadWriteLock.writeLock();
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            String username;
            Email email;
            int lastId;
            Operation operation = (Operation) in.readObject();

            System.out.println(operation.toString());

            switch (operation) {
                case LOGIN:
                    username = (String) in.readObject();
                    login(out, username);
                    break;

                case LOGOUT:
                    username = (String) in.readObject();
                    logout(username);
                    break;

                case CONTROL_USERNAME:
                    username = (String) in.readObject();
                    controlUsername(out, username);
                    break;

                case GET_EMAIL_ID:
                    getEmailId(out);
                    break;

                case SEND_EMAIL:
                    username = (String) in.readObject();
                    email = (Email) in.readObject();
                    sendEmail(email, username);
                    break;

                case DELETE_EMAIL:
                    username = (String) in.readObject();
                    email = (Email) in.readObject();
                    deleteEmail(out, email, username);
                    break;

                case LOAD_RECEIVED_EMAILS:
                    username = (String) in.readObject();
                    lastId = (int) in.readObject();
                    loadReceivedEmails(out, username, lastId);
                    break;

                case LOAD_SENDED_EMAILS:
                    username = (String) in.readObject();
                    lastId = (int) in.readObject();
                    loadSendedEmails(out, username, lastId);
                    break;

                default:
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void login(ObjectOutputStream out, String username) {
        try {
            if (fileIsNull(new FileReader(fileUsers))) {
                try {
                    out.writeObject(false);
                    out.flush();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        boolean userExists = checkUserEmail(username);

        if (userExists) {
            String date = getCurrentDate();
            serverModel.addToLog("<" + username + "> logged in at " + date);
        }

        try {
            out.writeObject(userExists);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logout(String username) {
        String date = getCurrentDate();
        serverModel.addToLog("<" + username + "> logged out at " + date);
    }

    private void controlUsername(ObjectOutputStream out, String username) {
        if (fileIsNull(out, fileUsers)) return;
        boolean userExists = checkUserEmail(username);
        if (!userExists) {
            serverModel.addToLog(username + " not found");
        }
        try {
            out.writeObject(userExists);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getEmailId(ObjectOutputStream out) {
        try {
            if (fileIsNull(new FileReader(fileEmails))) {
                try {
                    out.writeObject(Integer.MIN_VALUE);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            readLock.lock();
            Email[] emailList = processFileEmails();
            readLock.unlock();

            int emailCounter = 0;
            if (emailList.length > 0) {
                emailCounter = Integer.parseInt(emailList[emailList.length - 1].getID()) + 1;
            }

            out.writeObject(emailCounter);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(Email email, String username) {
        try {
            if (fileIsNull(new FileReader(fileEmails))) return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            readLock.lock();
            jsonArray = (JsonArray) JsonParser.parseReader(new FileReader(fileEmails));
            Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
            Email[] emailList = gson.fromJson(jsonArray, Email[].class);
            readLock.unlock();

            ArrayList<Email> toWrite = new ArrayList<>();
            FileWriter file = new FileWriter(fileEmails);
            Collections.addAll(toWrite, emailList);

            writeLock.lock();
            toWrite.add(email);
            gson.toJson(toWrite.toArray(), file);
            file.close();
            writeLock.unlock();

            serverModel.addToLog("<" + username + "> sent new email");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteEmail(ObjectOutputStream out, Email emailToDelete, String username) {
        if (fileIsNull(out, fileEmails)) return;

        try {
            readLock.lock();
            jsonArray = (JsonArray) JsonParser.parseReader(new FileReader(fileEmails));
            Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
            Email[] emailList = gson.fromJson(jsonArray, Email[].class);
            readLock.unlock();

            ArrayList<Email> toWrite = new ArrayList<>();
            FileWriter file = new FileWriter(fileEmails);

            writeLock.lock();
            boolean deletedCorrectly = false;
            for (Email email : emailList) {
                if (!email.getID().equals(emailToDelete.getID())) {
                    toWrite.add(email);
                } else {
                    deletedCorrectly = true;
                    serverModel.addToLog("Mail ID " + email.getID() + " deleted by <" + username + ">");
                }
            }
            gson.toJson(toWrite.toArray(), file);
            file.close();
            writeLock.unlock();

            out.writeObject(deletedCorrectly);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadReceivedEmails(ObjectOutputStream out, String username, int lastId) {
        try {
            if (fileIsNull(new FileReader(fileEmails))) return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            readLock.lock();
            Email[] emailList = processFileEmails();
            readLock.unlock();
            int i = 0;
            boolean newEmail = false;
            if (emailList != null) {
                for (Email email : emailList) {
                    if (email.getCc().contains(username) && Integer.parseInt(email.getID()) > lastId) {
                        receivedEmails.add(email);
                        newEmail = true;
                        i++;
                    }
                }
                if (newEmail) {
                    serverModel.addToLog("<" + username + ">" + " received a new email");
                }
            }

            out.writeObject(i);
            out.flush();

            for (Email email : receivedEmails) {
                out.writeObject(email);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSendedEmails(ObjectOutputStream out, String username, int lastId) {
        try {
            if (fileIsNull(new FileReader(fileEmails))) return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            readLock.lock();
            Email[] emailList = processFileEmails();
            readLock.unlock();

            int i = 0;
            if (emailList != null) {
                for (Email email : emailList) {
                    if (email.getSender().contains(username) && Integer.parseInt(email.getID()) > lastId) {
                        sendedEmails.add(email);
                        i++;
                    }
                }
            }
            out.writeObject(i);

            for (Email email : sendedEmails) {
                out.writeObject(email);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Email[] processFileEmails() {
        try {
            jsonArray = (JsonArray) JsonParser.parseReader(new FileReader(fileEmails));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Gson gson = FxGson.coreBuilder().create();
        return gson.fromJson(jsonArray, Email[].class);
    }

    @NotNull
    private String getCurrentDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        simpleDateFormat.setCalendar(calendar);
        return simpleDateFormat.format(calendar.getTime());
    }

    private boolean checkUserEmail(String username) {
        try {
            readLock.lock();
            jsonArray = (JsonArray) JsonParser.parseReader(new FileReader(fileUsers));
            for (JsonElement jsonElement : jsonArray) {
                usernameList.add(jsonElement.toString());
            }
            readLock.unlock();
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            e.printStackTrace();
        }

        boolean userExists = false;
        for (String s : usernameList) {
            if (s.equals("\"" + username + "\"")) {
                userExists = true;
                break;
            }
        }

        return userExists;
    }

    private boolean fileIsNull(ObjectOutputStream out, File fileUsers) {
        try {
            if (fileIsNull(new FileReader(fileUsers))) {
                try {
                    out.writeObject(false);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean fileIsNull(FileReader file) {
        try {
            readLock.lock();
            if (new BufferedReader(file).readLine() == null) {
                readLock.unlock();
                return true;
            }
            readLock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}