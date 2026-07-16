package entity;

import database.GestorePersistenza;

import java.util.List;

/**
 * Registro degli utenti. Fornisce l'accesso ai dati di Utente e
 * sottotipi tramite GestorePersistenza.
 */
public class RegistroUtenti {

    private final GestorePersistenza gestore = new GestorePersistenza();

    public Paziente getPaziente(String email) {
        return gestore.trovaPerId(Paziente.class, email);
    }

    public Medico getMedico(String email) {
        return gestore.trovaPerId(Medico.class, email);
    }

    public Amministratore getAmministratore(String email) {
        return gestore.trovaPerId(Amministratore.class, email);
    }

    public List<Medico> getTuttiMedici() {
        return gestore.cercaTutti(Medico.class);
    }
}
