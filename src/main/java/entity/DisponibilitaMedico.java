package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Disponibilità di un medico in una data. Oggetto di dominio (non persistito):
 * si calcola a partire dal template orario standard della {@link Giornata}
 * (pattern Composite) sottraendo gli slot già occupati dalle prenotazioni.
 */
public class DisponibilitaMedico {

    private final Medico medico;
    private final String data;
    private final List<SlotOrario> slotDisponibili = new ArrayList<>();

    /**
     * @param medico          medico di riferimento
     * @param data            data (yyyy-MM-dd)
     * @param orariOccupati   orari già prenotati per quel medico in quella data
     */
    public DisponibilitaMedico(Medico medico, String data, Set<String> orariOccupati) {
        this.medico = medico;
        this.data = data;
        for (SlotOrario slot : new Giornata().getSlot()) {
            if (!orariOccupati.contains(slot.getOrario())) {
                slotDisponibili.add(slot);
            }
        }
    }

    public Medico getMedico() {
        return medico;
    }

    public String getData() {
        return data;
    }

    /** Slot orari liberi per la prenotazione (operazione getFasceOrarieDisponibili). */
    public List<SlotOrario> getFasceOrarieDisponibili() {
        return slotDisponibili;
    }

    public boolean isDisponibile() {
        return !slotDisponibili.isEmpty();
    }
}
