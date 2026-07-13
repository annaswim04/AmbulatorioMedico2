package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Registro delle specializzazioni disponibili. Espone l'elenco delle
 * specializzazioni e, per ciascuna, i medici associati.
 */
public class RegistroSpecializzazioni {

    private final RegistroUtenti registroUtenti = new RegistroUtenti();

    /**
     * Specializzazioni effettivamente offerte dall'ambulatorio, cioè quelle
     * per cui esiste almeno un medico: mostrarne una senza medici porterebbe a
     * un vicolo cieco nella scelta del medico.
     */
    public List<Specializzazione> getSpecializzazioni() {
        List<Specializzazione> offerte = new ArrayList<>();
        for (Specializzazione specializzazione : Specializzazione.values()) {
            if (!getMedici(specializzazione).isEmpty()) {
                offerte.add(specializzazione);
            }
        }
        return offerte;
    }

    /** Medici associati a una specializzazione. */
    public List<Medico> getMedici(Specializzazione specializzazione) {
        return registroUtenti.getMediciPerSpecializzazione(specializzazione);
    }
}
