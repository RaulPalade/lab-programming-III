package server;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Raul Palade
 * @project Email Client Server
 * @date 08/04/2021
 */
public class TestJSON {

    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        FileReader fileEmails = new FileReader("resources/files/users.json");
        FileReader fileTestEmails = new FileReader("resources/files/testEmails.json");

       /* ObservableList<String> cc = FXCollections.observableArrayList();
        cc.add("raul@mail.com");
        Email email = new Email("0", "raul@mail.com", cc, "MAIL1", "CIAO", "08/04/2021");

        ArrayList<Email> toWrite = new ArrayList<>();
        FileWriter file = new FileWriter(fileEmails);
        toWrite.add(email);
        Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
        gson.toJson(toWrite.toArray(), file);
        file.close();*/

        boolean isFileValid = checkIfFileIsNull(fileEmails);
        System.out.println(isFileValid);
    }

    public static boolean checkIfFileIsNull(FileReader file) throws IOException {
        return new BufferedReader(file).readLine() != null;
    }
}