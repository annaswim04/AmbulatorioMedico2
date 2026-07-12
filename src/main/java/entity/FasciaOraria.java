package entity;

/**
 * Fascia oraria prenotabile dal paziente, in cui è suddivisa la giornata
 * dell'ambulatorio. Modellata come enum: valori chiusi e noti a priori.
 *
 * <p>La fascia è l'unità prenotabile: per un dato medico e una data, ciascuna
 * fascia può essere libera oppure occupata da una prenotazione.</p>
 */
public enum FasciaOraria {
    MATTINA,
    PRIMO_POMERIGGIO,
    TARDO_POMERIGGIO
}
