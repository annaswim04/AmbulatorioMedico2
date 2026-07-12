package entity;

/**
 * Stato di una prenotazione, come da UML. È un semplice enum persistito come
 * stringa: la logica delle transizioni ammesse (macchina a stati) è a carico
 * di {@link Prenotazione}.
 *
 * <pre>
 *   PRENOTATO --annulla-->        ANNULLATO
 *   PRENOTATO --effettua-->       EFFETTUATO
 *   PRENOTATO --segnalaAssenza--> NON_PRESENTATO
 * </pre>
 */
public enum StatoPrenotazione {
    PRENOTATO("Prenotato"),
    EFFETTUATO("Effettuato"),
    ANNULLATO("Annullato"),
    NON_PRESENTATO("Paziente non presentato");

    private final String descrizione;

    StatoPrenotazione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
