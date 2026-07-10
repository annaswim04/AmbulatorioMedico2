package controller;

import entity.DisponibilitaMedico;
import entity.Medico;
import entity.Prenotazione;
import entity.RegistroPrenotazioni;
import entity.RegistroSpecializzazioni;
import entity.RegistroUtenti;
import entity.RisultatoMonitoraggio;
import entity.ServiziMonitoraggio;
import entity.Specializzazione;

import java.util.List;

/**
 * Controller di facciata per i casi d'uso legati alle prenotazioni.
 *
 * Pattern: SINGLETON + FACADE. È l'unico punto di contatto della boundary con la
 * logica applicativa: orchestra i {@code Registro*} del livello entity e non
 * contiene né codice Swing né accessi diretti al DB.
 */
public class ControllerPrenotazioni {

    // Esiti esposti alla boundary (rimappano quelli del registro)
    public static final int SUCCESSO = RegistroPrenotazioni.SUCCESSO;
    public static final int PAZIENTE_NON_ESISTENTE = RegistroPrenotazioni.PAZIENTE_NON_ESISTENTE;
    public static final int MEDICO_NON_ESISTENTE = RegistroPrenotazioni.MEDICO_NON_ESISTENTE;
    public static final int SLOT_NON_DISPONIBILE = RegistroPrenotazioni.SLOT_NON_DISPONIBILE;
    public static final int ERRORE_DB = RegistroPrenotazioni.ERRORE_DB;

    private static ControllerPrenotazioni instance;

    private final RegistroPrenotazioni registroPrenotazioni = new RegistroPrenotazioni();
    private final RegistroSpecializzazioni registroSpecializzazioni = new RegistroSpecializzazioni();
    private final RegistroUtenti registroUtenti = new RegistroUtenti();
    private final ServiziMonitoraggio serviziMonitoraggio = new ServiziMonitoraggio();

    private ControllerPrenotazioni() {
    }

    public static ControllerPrenotazioni getInstance() {
        if (instance == null) {
            instance = new ControllerPrenotazioni();
        }
        return instance;
    }

    // --- UC: Visualizza disponibilità ---

    public List<Specializzazione> getSpecializzazioni() {
        return registroSpecializzazioni.getSpecializzazioni();
    }

    public List<Medico> getMedici(Specializzazione specializzazione) {
        return registroSpecializzazioni.getMedici(specializzazione);
    }

    /** Disponibilità (slot liberi) di un medico in una data. */
    public DisponibilitaMedico visualizzaDisponibilita(String emailMedico, String data) {
        Medico medico = registroUtenti.getMedico(emailMedico);
        if (medico == null) {
            return null;
        }
        return registroPrenotazioni.getDisponibilita(medico, data);
    }

    // --- UC: Effettua prenotazione ---

    /** Effettua una prenotazione; restituisce uno dei codici di esito pubblici. */
    public int effettuaPrenotazione(String emailPaziente, String emailMedico,
                                    String data, String orario) {
        return registroPrenotazioni.salvaPrenotazione(emailPaziente, emailMedico, data, orario);
    }

    // --- UC: Elenco prenotazioni (medico) ---

    public List<Prenotazione> visualizzaElencoPrenotazioni(String emailMedico) {
        Medico medico = registroUtenti.getMedico(emailMedico);
        if (medico == null) {
            return List.of();
        }
        return registroPrenotazioni.getPrenotazioniPerMedico(medico);
    }

    // --- UC: Monitoraggio ambulatorio ---

    public RisultatoMonitoraggio visualizzaMonitoraggioAmbulatorio(String dataInizio, String dataFine) {
        return serviziMonitoraggio.monitora(dataInizio, dataFine);
    }
}
