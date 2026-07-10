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
 * Lo stato è persistito come token {@link StatoVisita}, ma la logica delle
 * transizioni passa dal pattern State ({@link StatoPrenotazione}), accessibile
 * via {@link #getStato()} e i metodi {@link #annulla()}, {@link #effettua()},
 * {@link #segnalaAssenza()}.
 */
@Entity
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;   // formato "yyyy-MM-dd"
    private String orario; // formato "HH:mm"

    @Enumerated(EnumType.STRING)
    private StatoVisita stato;

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
        this.stato = StatoVisita.PRENOTATO;
    }

    // --- Pattern State: transizioni delegate all'oggetto-stato ---

    /** Oggetto-stato corrente (pattern State). */
    public StatoPrenotazione getStato() {
        return StatoPrenotazione.da(stato);
    }

    public void annulla() {
        this.stato = getStato().annulla().getTipo();
    }

    public void effettua() {
        this.stato = getStato().effettua().getTipo();
    }

    public void segnalaAssenza() {
        this.stato = getStato().segnalaAssenza().getTipo();
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

    public StatoVisita getStatoVisita() {
        return stato;
    }

    public void setStatoVisita(StatoVisita stato) {
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
