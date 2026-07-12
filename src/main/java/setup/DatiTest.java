package setup;

import database.GestorePersistenza;
import entity.Amministratore;
import entity.FasciaOraria;
import entity.Medico;
import entity.Paziente;
import entity.Prenotazione;
import entity.Specializzazione;
import entity.StatoPrenotazione;
import entity.Utente;

import java.util.List;

/**
 * Popola il database con dati iniziali per provare l'applicazione.
 * L'inserimento è idempotente per singolo utente: ogni utente viene salvato solo
 * se non è già presente, così è possibile aggiungerne di nuovi senza azzerare il DB.
 */
public class DatiTest {

    /** Salva l'utente solo se non esiste già un utente con la stessa email. */
    private static void salvaUtenteSeAssente(GestorePersistenza gestore, Utente utente) {
        if (gestore.trovaPerId(Utente.class, utente.getEmail()) == null) {
            gestore.salva(utente);
            System.out.println("[DatiTest] Inserito utente: " + utente.getEmail());
        }
    }

    public static void popola(GestorePersistenza gestore) {
        // --- Medici ---
        Medico cardiologo = new Medico("mario.rossi@ambulatorio.it", "pwd",
                "Mario", "Rossi", "3401111111", Specializzazione.CARDIOLOGIA);
        Medico dermatologo = new Medico("laura.bianchi@ambulatorio.it", "pwd",
                "Laura", "Bianchi", "3402222222", Specializzazione.DERMATOLOGIA);
        Medico ortopedico = new Medico("giulio.verdi@ambulatorio.it", "pwd",
                "Giulio", "Verdi", "3403333333", Specializzazione.ORTOPEDIA);
        salvaUtenteSeAssente(gestore, cardiologo);
        salvaUtenteSeAssente(gestore, dermatologo);
        salvaUtenteSeAssente(gestore, ortopedico);

        // --- Pazienti ---
        Paziente anna = new Paziente("anna.esposito@email.it", "pwd",
                "Anna", "Esposito", "3336666666");
        Paziente luca = new Paziente("luca.ferrari@email.it", "pwd",
                "Luca", "Ferrari", "3337777777");
        Paziente annina = new Paziente("anninademaio1@gmail.com","pwd","Anna","De Maio","3282322726");
        salvaUtenteSeAssente(gestore, anna);
        salvaUtenteSeAssente(gestore, luca);
        salvaUtenteSeAssente(gestore, annina);

        // --- Amministratore ---
        salvaUtenteSeAssente(gestore, new Amministratore("admin@ambulatorio.it", "admin",
                "Admin", "Sistema", "3300000000"));

        // --- Prenotazioni di esempio (per elenco medico e monitoraggio) ---
        if (gestore.cercaTutti(Prenotazione.class).isEmpty()) {
            Prenotazione p1 = new Prenotazione("2026-07-15", FasciaOraria.MATTINA.name(), anna, cardiologo);
            Prenotazione p2 = new Prenotazione("2026-07-15", FasciaOraria.PRIMO_POMERIGGIO.name(), luca, cardiologo);
            Prenotazione p3 = new Prenotazione("2026-07-16", FasciaOraria.MATTINA.name(), anna, dermatologo);
            Prenotazione p4 = new Prenotazione("2026-07-16", FasciaOraria.TARDO_POMERIGGIO.name(), luca, ortopedico);
            p4.setStato(StatoPrenotazione.ANNULLATO); // un annullamento per il monitoraggio
            for (Prenotazione p : List.of(p1, p2, p3, p4)) {
                gestore.salva(p);
            }
        }

        System.out.println("[DatiTest] Dati di test inseriti.");
    }
}
