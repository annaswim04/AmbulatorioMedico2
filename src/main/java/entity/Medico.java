package entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Medico dell'ambulatorio. Sottotipo di {@link Utente}, caratterizzato dalla
 * propria {@link Specializzazione}.
 */
@Entity
@DiscriminatorValue("MEDICO")
public class Medico extends Utente {

    @Enumerated(EnumType.STRING)
    private Specializzazione specializzazione;

    public Medico() {
    }

    public Medico(String email, String password, String nome, String cognome,
                  String recapitoTelefonico, Specializzazione specializzazione) {
        super(email, password, nome, cognome, recapitoTelefonico);
        this.specializzazione = specializzazione;
    }

    public Specializzazione getSpecializzazione() {
        return specializzazione;
    }

    public void setSpecializzazione(Specializzazione specializzazione) {
        this.specializzazione = specializzazione;
    }
}
