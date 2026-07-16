package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Disponibilità di un medico in una data. Mostra le fasce orarie della giornata
 * escludendo quelle già occupate da una prenotazione.
 */
public class DisponibilitaMedico {

    private final Medico medico;
    private final String data;
    private final List<FasciaOraria> fasceDisponibili = new ArrayList<>();

    /**
     *  medico -> medico di riferimento
     *  data -> data (yyyy-MM-dd)
     *  fasceOccupate -> fasce già prenotate per quel medico in quella data
     */
    public DisponibilitaMedico(Medico medico, String data, Set<FasciaOraria> fasceOccupate) {
        this.medico = medico;
        this.data = data;
        for (FasciaOraria fascia : FasciaOraria.values()) {
            if (!fasceOccupate.contains(fascia)) {
                fasceDisponibili.add(fascia);
            }
        }
    }

    public Medico getMedico() {
        return medico;
    }

    public String getData() {
        return data;
    }

    /** Fasce orarie libere per la prenotazione. */
    public List<FasciaOraria> getFasceOrarieDisponibili() {
        return fasceDisponibili;
    }

    public boolean isDisponibile() {
        return !fasceDisponibili.isEmpty();
    }
}
