package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Pattern Composite (COMPOSITE). Fascia composta da altre fasce orarie
 * (tipicamente slot). Superclasse di {@link Mattina}, {@link PrimoPomeriggio},
 * {@link TardoPomeriggio} e {@link Giornata}.
 */
public abstract class PeriodoDiGiornata extends FasciaOraria {

    private final List<FasciaOraria> componenti = new ArrayList<>();

    /** Aggiunge una fascia figlia (slot o altro periodo). */
    public void aggiungi(FasciaOraria fascia) {
        componenti.add(fascia);
    }

    protected void aggiungiOrari(String... orari) {
        for (String o : orari) {
            aggiungi(new SlotOrario(o));
        }
    }

    public List<FasciaOraria> getComponenti() {
        return componenti;
    }

    @Override
    public List<SlotOrario> getSlot() {
        List<SlotOrario> tutti = new ArrayList<>();
        for (FasciaOraria f : componenti) {
            tutti.addAll(f.getSlot());
        }
        return tutti;
    }
}
