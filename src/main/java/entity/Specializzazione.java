package entity;

/**
 * Specializzazioni mediche disponibili nell'ambulatorio.
 * Modellata come enum: valori chiusi e noti a priori.
 */
public enum Specializzazione {
    CARDIOLOGIA("Cardiologia"),
    DERMATOLOGIA("Dermatologia"),
    ORTOPEDIA("Ortopedia"),
    OCULISTICA("Oculistica"),
    NEUROLOGIA("Neurologia"),
    PEDIATRIA("Pediatria");

    private final String descrizione;

    Specializzazione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
