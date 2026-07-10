package entity;

import java.util.List;

/**
 * Pattern: COMPOSITE (component). Rappresenta una fascia oraria della giornata.
 *
 * Una fascia può essere:
 * <ul>
 *   <li>una foglia: {@link SlotOrario} (singolo orario prenotabile, es. "09:00");</li>
 *   <li>un composite: {@link PeriodoDiGiornata} e le sue sottoclassi
 *       {@link Mattina}, {@link PrimoPomeriggio}, {@link TardoPomeriggio},
 *       che raggruppano più slot; oppure {@link Giornata}, che raggruppa i periodi.</li>
 * </ul>
 *
 * Il client tratta in modo uniforme foglie e composite tramite {@link #getSlot()}
 * e {@link #contaSlot()}.
 */
public abstract class FasciaOraria {

    /** Nome descrittivo della fascia (es. "Mattina", "09:00"). */
    public abstract String getNome();

    /** Elenco ricorsivo di tutti gli slot foglia contenuti. */
    public abstract List<SlotOrario> getSlot();

    /** Numero di slot prenotabili nella fascia. */
    public int contaSlot() {
        return getSlot().size();
    }
}
