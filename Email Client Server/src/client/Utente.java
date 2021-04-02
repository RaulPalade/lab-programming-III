package client;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 19/03/2021
 */
public class Utente implements Serializable {
    private String emailUtente;

    public Utente(String emailUtente) {
        this.emailUtente = emailUtente.toLowerCase();
    }

    public String getEmailUtente() {
        return emailUtente;
    }

    public void setEmailUtente(String emailUtente) {
        this.emailUtente = emailUtente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente utente = (Utente) o;
        return emailUtente.equals(utente.emailUtente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailUtente);
    }

    @Override
    public String toString() {
        return "Utente{" +
                "emailUtente='" + emailUtente + '\'' +
                '}';
    }
}