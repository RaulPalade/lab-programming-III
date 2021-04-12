package client;

import javafx.scene.control.Tab;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 05/03/2021
 */
public class LeggiEmailController {


    private Tab tabPostaInArrivo;
    private Tab tabPostaInviata;

    private MailBox mailBox;

    public void initModel(MailBox mailBox, Tab tabPostaInArrivo, Tab tabPostaInviata) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.mailBox = mailBox;
        this.tabPostaInArrivo = tabPostaInArrivo;
        this.tabPostaInviata = tabPostaInviata;


    }


}