package entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Medico dell'ambulatorio. Sottotipo di Utente, caratterizzato dalla propria Specializzazione.
 */
@Entity
@DiscriminatorValue("MEDICO")
public class Medico extends Utente {

    @ManyToOne
    @JoinColumn(name = "specializzazione")
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

    /**
     * Information expert: tra le date indicate, quelle in cui il
     * medico ha almeno una fascia oraria libera, note le fasce già occupate
     * in ciascuna data.
     */
    public List<DisponibilitaMedico> getDateDisponibili(Map<String, Set<FasciaOraria>> fasceOccupatePerData) {
        List<DisponibilitaMedico> dateDisponibili = new ArrayList<>();
        for (Map.Entry<String, Set<FasciaOraria>> e : fasceOccupatePerData.entrySet()) {
            DisponibilitaMedico disponibilita = new DisponibilitaMedico(this, e.getKey(), e.getValue());
            if (disponibilita.isDisponibile()) {
                dateDisponibili.add(disponibilita);
            }
        }
        return dateDisponibili;
    }
}
