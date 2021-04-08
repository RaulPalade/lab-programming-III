package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import common.Email;
import org.hildan.fxgson.FxGson;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Raul Palade
 * @project Email Server
 * @date 30/03/2021
 */
public class ServerTask implements Runnable {
    private final Socket clientSocket;
    private final DataModel dataModel;
    private final Lock writeLock;
    private final Lock readLock;

    private final File fileEmails = new File("resources/files/emails.json");
    private final File fileUsers = new File("resources/files/users.json");
    private final ArrayList<String> usernameList = new ArrayList<>();
    private final ArrayList<Email> receivedEmails = new ArrayList<>();
    private final ArrayList<Email> sendedEmails = new ArrayList<>();
    private JsonArray jsonArray = null;
    private JsonArray receivedJson = null;
    private JsonArray emailJsonList = null;
    private Iterator<JsonElement> iterator;

    public ServerTask(Socket clientSocket, DataModel dataModel) {
        this.clientSocket = clientSocket;
        this.dataModel = dataModel;
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
            String operation = (String) in.readObject();

            System.out.println("OPERATION: " + operation);

            switch (operation) {
                case "LOGIN":
                    username = (String) in.readObject();
                    login(out, username);
                    out.writeObject("OK");
                    out.flush();
                    break;

                case "LOGOUT":
                    username = (String) in.readObject();
                    logout(username);
                    out.writeObject("OK");
                    out.flush();
                    break;

                case "CONTROL_USERNAME":
                    username = (String) in.readObject();
                    controlUsername(out, username);
                    out.writeObject("OK");
                    break;

                case "GET_EMAIL_ID":
                    username = (String) in.readObject();
                    getEmailId(out);
                    break;

                case "SEND_EMAIL":
                    username = (String) in.readObject();
                    email = (Email) in.readObject();
                    sendEmail(email);
                    out.writeObject("OK");
                    dataModel.addToLog("<" + username + "> sent new email");
                    break;

                case "DELETE_EMAIL":
                    username = (String) in.readObject();
                    email = (Email) in.readObject();
                    deleteEmail(email);
                    out.writeObject("OK");
                    dataModel.addToLog("Mail ID " + email.getID() + "deleted by <" + username + ">");
                    break;

                case "LOAD_RECEIVED_EMAILS":
                    username = (String) in.readObject();
                    lastId = (int) in.readObject();
                    loadReceivedEmails(out, username, lastId);
                    out.writeObject("OK");
                    out.flush();
                    break;

                case "LOAD_SENDED_EMAILS":
                    username = (String) in.readObject();
                    lastId = (int) in.readObject();
                    loadSendedEmails(out, username, lastId);
                    out.writeObject("OK");
                    out.flush();
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
            readLock.lock();
            jsonArray = (JsonArray) JsonParser.parseReader(new FileReader(fileUsers));
            System.out.println(jsonArray);
            iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                usernameList.add(iterator.next().toString());
            }
            readLock.unlock();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        boolean trovato = false;
        for (String s : usernameList) {
            if (s.equals("\"" + username + "\"")) {
                trovato = true;
                break;
            }
        }

        if (trovato) {
            GregorianCalendar calendar = new GregorianCalendar();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            simpleDateFormat.setCalendar(calendar);
            String date = simpleDateFormat.format(calendar.getTime());
            dataModel.addToLog("<" + username + "> logged in at " + date);
        }

        try {
            out.writeObject(trovato);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logout(String username) {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        simpleDateFormat.setCalendar(calendar);
        String date = simpleDateFormat.format(calendar.getTime());
        dataModel.addToLog("<" + username + "> logged out at " + date);
    }

    private void controlUsername(ObjectOutputStream out, String username) {
        try {
            readLock.lock();
            jsonArray = (JsonArray) JsonParser.parseReader(new FileReader(fileUsers));
            iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                usernameList.add(iterator.next().toString());
            }
            readLock.unlock();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        boolean trovato = false;
        for (String s : usernameList) {
            if (s.equals("\"" + username + "\"")) {
                trovato = true;
                break;
            }
        }

        if (trovato) {
            dataModel.addToLog(username + " not found");
        }
        try {
            out.writeObject(trovato);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getEmailId(ObjectOutputStream out) {
        try {
            readLock.lock();
            receivedJson = (JsonArray) JsonParser.parseReader(new FileReader(fileEmails));
            System.out.println(receivedJson);
            Gson gson = FxGson.coreBuilder().create();
            Email[] emailList = gson.fromJson(receivedJson, Email[].class);
            readLock.unlock();

            int emailCounter = 0;
            if (emailList.length > 0) {
                emailCounter = Integer.parseInt(emailList[emailList.length - 1].getID()) + 1;
            }
            out.writeObject(emailCounter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(Email email) {
        try {
            readLock.lock();
            emailJsonList = (JsonArray) JsonParser.parseReader(new FileReader(fileEmails));
            Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
            Email[] emailList = gson.fromJson(emailJsonList, Email[].class);
            readLock.unlock();

            ArrayList<Email> toWrite = new ArrayList<>();
            FileWriter file = new FileWriter(fileEmails);
            Collections.addAll(toWrite, emailList);

            writeLock.lock();
            toWrite.add(email);
            gson.toJson(toWrite.toArray(), file);
            file.close();
            writeLock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteEmail(Email emailToDelete) {
        try {
            readLock.lock();
            receivedJson = (JsonArray) JsonParser.parseReader(new FileReader(fileEmails));
            Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
            Email[] emailList = gson.fromJson(receivedJson, Email[].class);
            readLock.unlock();

            ArrayList<Email> toWrite = new ArrayList<>();
            FileWriter file = new FileWriter(fileEmails);

            writeLock.lock();
            for (Email email : emailList) {
                if (!email.getID().equals(emailToDelete.getID())) {
                    toWrite.add(email);
                }
            }
            gson.toJson(toWrite.toArray(), file);
            file.close();
            writeLock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadReceivedEmails(ObjectOutputStream out, String username, int lastId) {
        try {
            readLock.lock();
            emailJsonList = (JsonArray) JsonParser.parseReader(new FileReader(fileEmails));
            Gson gson = FxGson.coreBuilder().create();
            Email[] emailList = gson.fromJson(emailJsonList, Email[].class);
            readLock.unlock();

            System.out.println(Arrays.toString(emailList));

            int i = 0;
            boolean newEmail = false;
            if (emailList != null) {
                for (Email email : emailList) {
                    if (email.getAddresses().contains(username) && Integer.parseInt(email.getID()) > lastId) {
                        receivedEmails.add(email);
                        newEmail = true;
                        i++;
                    }
                }
                if (newEmail) {
                    dataModel.addToLog("<" + username + ">" + " received a new email");
                }
            }

            out.writeObject(i);
            out.flush();

            for (Email email : receivedEmails) {
                out.writeObject(email);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSendedEmails(ObjectOutputStream out, String username, int lastId) {
        try {
            readLock.lock();
            emailJsonList = (JsonArray) JsonParser.parseReader(new FileReader(fileEmails));
            Gson gson = FxGson.coreBuilder().create();
            Email[] emailList = gson.fromJson(emailJsonList, Email[].class);
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}