package entity;

import database.GestorePersistenza;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Registro delle prenotazioni. Contiene la logica applicativa di salvataggio e
 * ricerca delle prenotazioni, appoggiandosi a {@link GestorePersistenza}.
 * Livello entity: nessuna dipendenza da boundary/controller.
 */
public class RegistroPrenotazioni {

    // Codici di esito
    public static final int SUCCESSO = 1;
    public static final int PAZIENTE_NON_ESISTENTE = 2;
    public static final int MEDICO_NON_ESISTENTE = 3;
    public static final int SLOT_NON_DISPONIBILE = 4;
    public static final int ERRORE_DB = 5;

    private final GestorePersistenza gestore = new GestorePersistenza();
    private final RegistroUtenti registroUtenti = new RegistroUtenti();

    /**
     * Salva una nuova prenotazione dopo aver validato paziente, medico e
     * disponibilità dello slot.
     */
    public int salvaPrenotazione(String emailPaziente, String emailMedico,
                                 String data, String orario) {
        Paziente paziente = registroUtenti.getPaziente(emailPaziente);
        if (paziente == null) {
            return PAZIENTE_NON_ESISTENTE;
        }

        Medico medico = registroUtenti.getMedico(emailMedico);
        if (medico == null) {
            return MEDICO_NON_ESISTENTE;
        }

        if (!getOrariOccupati(medico, data).isEmpty()
                && getOrariOccupati(medico, data).contains(orario)) {
            return SLOT_NON_DISPONIBILE;
        }

        Prenotazione prenotazione = new Prenotazione(data, orario, paziente, medico);
        boolean salvato = gestore.salva(prenotazione);
        return salvato ? SUCCESSO : ERRORE_DB;
    }

    /** Orari già occupati (stato PRENOTATO) di un medico in una data. */
    public Set<String> getOrariOccupati(Medico medico, String data) {
        List<Prenotazione> prenotazioni = gestore.cercaPerCampi(Prenotazione.class,
                Map.of("medico", medico, "data", data));
        Set<String> occupati = new HashSet<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getStatoVisita() == StatoVisita.PRENOTATO) {
                occupati.add(p.getOrario());
            }
        }
        return occupati;
    }

    /** Calcola la disponibilità di un medico in una data (pattern Composite). */
    public DisponibilitaMedico getDisponibilita(Medico medico, String data) {
        return new DisponibilitaMedico(medico, data, getOrariOccupati(medico, data));
    }

    /** Tutte le prenotazioni di un medico (UC elenco prenotazioni medico). */
    public List<Prenotazione> getPrenotazioniPerMedico(Medico medico) {
        return gestore.cercaPerCampi(Prenotazione.class, Map.of("medico", medico));
    }

    /** Elenco completo delle prenotazioni presenti nel sistema. */
    public List<Prenotazione> getElencoPrenotazioni() {
        return gestore.cercaTutti(Prenotazione.class);
    }

    /** Prenotazioni comprese nell'intervallo [dataInizio, dataFine] (inclusi). */
    public List<Prenotazione> getPrenotazioniPerIntervallo(String dataInizio, String dataFine) {
        return getElencoPrenotazioni().stream()
                .filter(p -> p.getData() != null
                        && p.getData().compareTo(dataInizio) >= 0
                        && p.getData().compareTo(dataFine) <= 0)
                .collect(Collectors.toList());
    }
}
