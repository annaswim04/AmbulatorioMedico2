package entity;

import java.util.Arrays;
import java.util.List;

/**
 * Registro delle specializzazioni disponibili. Espone l'elenco delle
 * specializzazioni e, per ciascuna, i medici associati.
 */
public class RegistroSpecializzazioni {

    private final RegistroUtenti registroUtenti = new RegistroUtenti();

    /** Tutte le specializzazioni offerte dall'ambulatorio. */
    public List<Specializzazione> getSpecializzazioni() {
        return Arrays.asList(Specializzazione.values());
    }

    /** Medici associati a una specializzazione. */
    public List<Medico> getMedici(Specializzazione specializzazione) {
        return registroUtenti.getMediciPerSpecializzazione(specializzazione);
    }
}
