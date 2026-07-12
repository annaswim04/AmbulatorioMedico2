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
 * Prenotazione di una visita: associa un {@link Paziente} a un {@link Medico}
 * in una certa data e slot orario, con uno stato.
 *
 * Lo stato è un enum {@link StatoPrenotazione} persistito come stringa. Le
 * transizioni ammesse (macchina a stati) sono verificate dai metodi
 * {@link #annulla()}, {@link #effettua()}, {@link #segnalaAssenza()}.
 */
@Entity
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;   // formato "yyyy-MM-dd"
    private String orario; // formato "HH:mm"

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

    public Prenotazione(String data, String orario, Paziente paziente, Medico medico) {
        this.data = data;
        this.orario = orario;
        this.paziente = paziente;
        this.medico = medico;
        this.stato = StatoPrenotazione.PRENOTATO;
    }

    // --- Transizioni di stato (macchina a stati del diagramma UML) ---

    public void annulla() {
        verificaTransizioneDaPrenotato();
        this.stato = StatoPrenotazione.ANNULLATO;
    }

    public void effettua() {
        verificaTransizioneDaPrenotato();
        this.stato = StatoPrenotazione.EFFETTUATO;
    }

    public void segnalaAssenza() {
        verificaTransizioneDaPrenotato();
        this.stato = StatoPrenotazione.NON_PRESENTATO;
    }

    /** Le transizioni sono ammesse solo dallo stato iniziale PRENOTATO. */
    private void verificaTransizioneDaPrenotato() {
        if (stato != StatoPrenotazione.PRENOTATO) {
            throw new IllegalStateException(
                    "Transizione non ammessa dallo stato " + stato.getDescrizione());
        }
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

    public String getOrario() {
        return orario;
    }

    public void setOrario(String orario) {
        this.orario = orario;
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
        return "Prenotazione{id=" + id + ", data='" + data + "', orario='" + orario
                + "', stato=" + stato + "}";
    }
}
