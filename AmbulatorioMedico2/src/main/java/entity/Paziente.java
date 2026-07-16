package entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Paziente dell'ambulatorio. Sottotipo di Utente.
 */
@Entity
@DiscriminatorValue("PAZIENTE")
public class Paziente extends Utente {

    public Paziente() {
    }

    public Paziente(String email, String password, String nome, String cognome,
                    String recapitoTelefonico) {
        super(email, password, nome, cognome, recapitoTelefonico);
    }
}
