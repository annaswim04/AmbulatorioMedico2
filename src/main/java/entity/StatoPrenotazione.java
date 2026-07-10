package entity;

/**
 * Pattern: STATE. Rappresenta lo stato di una prenotazione e incapsula le
 * transizioni ammesse (macchina a stati del diagramma UML):
 *
 * <pre>
 *   Prenotato --annulla-->        Annullato
 *   Prenotato --effettua-->       Effettuato
 *   Prenotato --segnalaAssenza--> PazienteNonPresentato
 * </pre>
 *
 * Ogni sottoclasse implementa le transizioni consentite dal proprio stato;
 * gli stati terminali rifiutano ogni transizione. La persistenza avviene tramite
 * il token {@link StatoVisita} restituito da {@link #getTipo()}.
 */
public abstract class StatoPrenotazione {

    /** Token persistente corrispondente a questo stato. */
    public abstract StatoVisita getTipo();

    public String descrizione() {
        return getTipo().getDescrizione();
    }

    // Transizioni: per default non consentite (stati terminali).
    public StatoPrenotazione annulla() {
        throw new IllegalStateException("Impossibile annullare da stato " + descrizione());
    }

    public StatoPrenotazione effettua() {
        throw new IllegalStateException("Impossibile effettuare da stato " + descrizione());
    }

    public StatoPrenotazione segnalaAssenza() {
        throw new IllegalStateException("Impossibile segnalare assenza da stato " + descrizione());
    }

    /** Factory: ricostruisce l'oggetto-stato a partire dal token persistente. */
    public static StatoPrenotazione da(StatoVisita tipo) {
        return switch (tipo) {
            case PRENOTATO -> AppuntamentoPrenotato.getInstance();
            case EFFETTUATO -> AppuntamentoEffettuato.getInstance();
            case ANNULLATO -> AppuntamentoAnnullato.getInstance();
            case NON_PRESENTATO -> PazienteNonPresentato.getInstance();
        };
    }
}
