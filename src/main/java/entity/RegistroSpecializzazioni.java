package entity;

import database.GestorePersistenza;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Registro delle specializzazioni disponibili. Espone l'elenco delle
 * specializzazioni e, per ciascuna, i medici associati.
 */
public class RegistroSpecializzazioni {

    private final GestorePersistenza gestore = new GestorePersistenza();

    /**
     * Specializzazione a partire dal nome (chiave primaria), con i medici
     * associati già caricati. Restituisce null se non esiste.
     */
    public Specializzazione getSpecializzazione(String nome) {
        Specializzazione specializzazione = gestore.trovaPerId(Specializzazione.class, nome);
        if (specializzazione == null) {
            return null;
        }
        caricaMedici(specializzazione);
        return specializzazione;
    }

    /**
     * Specializzazioni effettivamente offerte dall'ambulatorio, cioè quelle
     * per cui esiste almeno un medico: mostrarne una senza medici porterebbe a
     * un vicolo cieco nella scelta del medico.
     */
    public List<Specializzazione> getSpecializzazioni() {
        List<Specializzazione> offerte = new ArrayList<>();
        for (Specializzazione specializzazione : gestore.cercaTutti(Specializzazione.class)) {
            caricaMedici(specializzazione);
            if (!specializzazione.getMedici().isEmpty()) {
                offerte.add(specializzazione);
            }
        }
        return offerte;
    }

    /** Medici associati a una specializzazione. */
    public List<Medico> getMedici(Specializzazione specializzazione) {
        return specializzazione.getMedici();
    }

    /**
     * Carica con una query separata i medici di una specializzazione (la
     * relazione è LAZY) e li ricollega ad essa, mantenendo coerente
     * l'associazione bidirezionale.
     */
    private void caricaMedici(Specializzazione specializzazione) {
        List<Medico> medici = gestore.cercaPerCampi(Medico.class,
                Map.of("specializzazione.nome", specializzazione.getNome()));
        for (Medico medico : medici) {
            medico.setSpecializzazione(specializzazione);
        }
        specializzazione.setMedici(medici);
    }
}
