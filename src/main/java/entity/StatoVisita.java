package entity;

/**
 * Token persistente dello stato di una prenotazione (salvato in DB).
 * Fa da ponte tra la persistenza JPA e il pattern State ({@link StatoPrenotazione}):
 * in memoria si lavora con gli oggetti-stato, su DB si salva questo enum.
 */
public enum StatoVisita {
    PRENOTATO("Prenotato"),
    EFFETTUATO("Effettuato"),
    ANNULLATO("Annullato"),
    NON_PRESENTATO("Paziente non presentato");

    private final String descrizione;

    StatoVisita(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
