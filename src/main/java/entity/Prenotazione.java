package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Prenotazione di una visita: associa un Paziente a un Medico
 * in una certa data e fascia oraria, con uno stato.
 *
 * Sia la fascia oraria sia lo stato sono enum persistiti come stringa.
 */
@Entity
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;   // formato "yyyy-MM-dd"

    @Enumerated(EnumType.STRING)
    private FasciaOraria fascia;

    @Enumerated(EnumType.STRING)
    private StatoPrenotazione stato;

    @ManyToOne
    @JoinColumn(name = "paziente_email")
    private Paziente paziente;

    @ManyToOne
    @JoinColumn(name = "medico_email")
    private Medico medico;

    public Prenotazione() {
    }

    public Prenotazione(String data, FasciaOraria fascia, Paziente paziente, Medico medico) {
        this.data = data;
        this.fascia = fascia;
        this.paziente = paziente;
        this.medico = medico;
        this.stato = StatoPrenotazione.PRENOTATO;
    }

    // --- Getter/Setter ---

    public Long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public FasciaOraria getFascia() {
        return fascia;
    }

    public void setFascia(FasciaOraria fascia) {
        this.fascia = fascia;
    }

    public StatoPrenotazione getStato() {
        return stato;
    }

    public void setStato(StatoPrenotazione stato) {
        this.stato = stato;
    }

    public Paziente getPaziente() {
        return paziente;
    }

    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    @Override
    public String toString() {
        return "Prenotazione{id=" + id + ", data='" + data + "', fascia=" + fascia
                + ", stato=" + stato + "}";
    }
}
