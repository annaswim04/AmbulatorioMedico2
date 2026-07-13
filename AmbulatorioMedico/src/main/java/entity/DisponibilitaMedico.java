package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Disponibilità di un medico in una data. Oggetto di dominio (non persistito):
 * si ottiene dall'elenco delle fasce orarie della giornata
 * ({@link FasciaOraria#values()}) escludendo quelle già occupate da una
 * prenotazione.
 */
public class DisponibilitaMedico {

    private final Medico medico;
    private final String data;
    private final List<FasciaOraria> fasceDisponibili = new ArrayList<>();

    /**
     * @param medico         medico di riferimento
     * @param data           data (yyyy-MM-dd)
     * @param fasceOccupate  orari delle fasce già prenotate per quel medico in quella data
     */
    public DisponibilitaMedico(Medico medico, String data, Set<String> fasceOccupate) {
        this.medico = medico;
        this.data = data;
        for (FasciaOraria fascia : FasciaOraria.values()) {
            if (!fasceOccupate.contains(fascia.name())) {
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

    /** Fasce orarie libere per la prenotazione (operazione getFasceOrarieDisponibili). */
    public List<FasciaOraria> getFasceOrarieDisponibili() {
        return fasceDisponibili;
    }

    public boolean isDisponibile() {
        return !fasceDisponibili.isEmpty();
    }
}
