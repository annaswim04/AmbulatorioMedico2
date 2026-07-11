package entity;

import java.util.List;

/**
 * Fascia oraria prenotabile dal paziente.
 *
 * <p>Modellazione (UML): classe base astratta da cui derivano, per
 * <b>generalizzazione</b>, le tre fasce in cui è suddivisa la giornata
 * dell'ambulatorio: {@link Mattina}, {@link PrimoPomeriggio} e
 * {@link TardoPomeriggio}. La fascia non ha attributi: la sua identità coincide
 * con il tipo concreto.</p>
 *
 * <p>La fascia è l'unità prenotabile: per un dato medico e una data, ciascuna
 * fascia può essere libera oppure occupata da una prenotazione.</p>
 */
public abstract class FasciaOraria {

    /** Nome della fascia (es. "Mattina"); usato per la visualizzazione e come valore persistito. */
    public abstract String getNome();

    /** Le tre fasce orarie della giornata, nell'ordine in cui si succedono. */
    public static List<FasciaOraria> valori() {
        return List.of(new Mattina(), new PrimoPomeriggio(), new TardoPomeriggio());
    }

    /** Ricostruisce la fascia a partire dal suo nome, o {@code null} se sconosciuto. */
    public static FasciaOraria da(String nome) {
        return valori().stream()
                .filter(f -> f.getNome().equals(nome))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getNome();
    }
}
