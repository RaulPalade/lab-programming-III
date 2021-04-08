package server;

import com.google.gson.Gson;
import common.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hildan.fxgson.FxGson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Raul Palade
 * @project Email Client Server
 * @date 08/04/2021
 */
public class TestJSON {
    public static void main(String[] args) throws IOException {
        File fileEmails = new File("resources/files/emails.json");
        /*JsonArray emailJsonList = (JsonArray) JsonParser.parseReader(new FileReader(fileEmails));
        Gson gson = FxGson.coreBuilder().create();
        System.out.println(emailJsonList + "\n");
        Email[] emailList = gson.fromJson(emailJsonList, Email[].class);
        System.out.println(Arrays.toString(emailList));*/

        ObservableList<String> addresses = FXCollections.observableArrayList();
        addresses.add("raul@mail.com");
        Email email = new Email("0", "raul@mail.com", addresses, "MAIL1", "CIAO", "08/04/2021");

        ArrayList<Email> toWrite = new ArrayList<>();
        FileWriter file = new FileWriter(fileEmails);
        toWrite.add(email);
        Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
        gson.toJson(toWrite.toArray(), file);
        file.close();
    }
}
