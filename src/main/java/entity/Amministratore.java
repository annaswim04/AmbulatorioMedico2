package entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Amministratore dell'ambulatorio. Sottotipo di Utente.
 * Ha accesso alle funzioni di monitoraggio.
 */
@Entity
@DiscriminatorValue("AMMINISTRATORE")
public class Amministratore extends Utente {

    public Amministratore() {
    }

    public Amministratore(String email, String password, String nome, String cognome,
                          String recapitoTelefonico) {
        super(email, password, nome, cognome, recapitoTelefonico);
    }
}
