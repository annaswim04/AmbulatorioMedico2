package entity;

import database.GestorePersistenza;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registro delle prenotazioni. Contiene la logica di salvataggio e
 * ricerca delle prenotazioni, appoggiandosi a GestorePersistenza.
 */
public class RegistroPrenotazioni {

    public static final int SUCCESSO = 1;
    public static final int PAZIENTE_NON_ESISTENTE = 2;
    public static final int MEDICO_NON_ESISTENTE = 3;
    public static final int FASCIA_NON_DISPONIBILE = 4;
    public static final int ERRORE_DB = 5;

    /** Anticipo minimo per prenotare: 48 ore (2 giorni). */
    private static final int GIORNI_MINIMI_ANTICIPO = 2;
    /** Orizzonte massimo di prenotazione: 60 giorni da oggi. */
    private static final int GIORNI_MASSIMI_ANTICIPO = 60;

    private final GestorePersistenza gestore = new GestorePersistenza();
    private final RegistroUtenti registroUtenti = new RegistroUtenti();

    /**
     * Salva una nuova prenotazione dopo aver validato paziente, medico e
     * disponibilità della fascia oraria.
     */
    public int salvaPrenotazione(String emailPaziente, String emailMedico,
                                 String data, String fascia) {
        Paziente paziente = registroUtenti.getPaziente(emailPaziente);
        if (paziente == null) {
            return PAZIENTE_NON_ESISTENTE;
        }

        Medico medico = registroUtenti.getMedico(emailMedico);
        if (medico == null) {
            return MEDICO_NON_ESISTENTE;
        }

        FasciaOraria fasciaOraria = parseFascia(fascia);
        if (fasciaOraria == null || getFasceOccupate(medico, data).contains(fasciaOraria)) {
            return FASCIA_NON_DISPONIBILE;
        }

        Prenotazione prenotazione = new Prenotazione(data, fasciaOraria, paziente, medico);
        boolean salvato = gestore.salva(prenotazione);
        return salvato ? SUCCESSO : ERRORE_DB;
    }

    /**
     * Converte il nome di una fascia oraria nel relativo valore enum.
     * Restituisce null se la stringa è nulla, vuota o non riconosciuta.
     */
    private FasciaOraria parseFascia(String fascia) {
        if (fascia == null || fascia.isEmpty()) {
            return null;
        }
        try {
            return FasciaOraria.valueOf(fascia);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Fasce orarie già occupate (stato PRENOTATO) di un medico in una data. */
    public Set<FasciaOraria> getFasceOccupate(Medico medico, String data) {
        List<Prenotazione> prenotazioni = gestore.cercaPerCampi(Prenotazione.class,
                Map.of("medico", medico, "data", data));
        Set<FasciaOraria> occupate = new HashSet<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getStato() == StatoPrenotazione.PRENOTATO) {
                occupate.add(p.getFascia());
            }
        }
        return occupate;
    }

    /** Calcola la disponibilità di un medico in una data (fasce libere). */
    public DisponibilitaMedico getDisponibilita(Medico medico, String data) {
        return new DisponibilitaMedico(medico, data, getFasceOccupate(medico, data));
    }

    /**
     * Date, nell'orizzonte di prenotazione (da GIORNI_MINIMI_ANTICIPO
     * a GIORNI_MASSIMI_ANTICIPO; giorni da oggi), in cui il medico ha
     * almeno una fascia oraria libera.
     */
    public List<DisponibilitaMedico> getDateDisponibili(Medico medico) {
        Map<String, Set<FasciaOraria>> fasceOccupatePerData = new LinkedHashMap<>();
        LocalDate inizio = LocalDate.now().plusDays(GIORNI_MINIMI_ANTICIPO);
        LocalDate fine = LocalDate.now().plusDays(GIORNI_MASSIMI_ANTICIPO);
        for (LocalDate d = inizio; !d.isAfter(fine); d = d.plusDays(1)) {
            String data = d.toString();
            fasceOccupatePerData.put(data, getFasceOccupate(medico, data));
        }
        return medico.getDateDisponibili(fasceOccupatePerData);
    }


    /**
     * Prenotazioni di un medico, filtrabili per data e/o fascia oraria.
     * Filtri null o vuoti vengono ignorati.
     */
    public List<Prenotazione> getPrenotazioniPerMedico(Medico medico, String data, String fascia) {
        Map<String, Object> campi = new HashMap<>();
        campi.put("medico", medico);
        if (data != null && !data.isEmpty()) {
            campi.put("data", data);
        }
        FasciaOraria fasciaOraria = parseFascia(fascia);
        if (fasciaOraria != null) {
            campi.put("fascia", fasciaOraria);
        }
        return gestore.cercaPerCampi(Prenotazione.class, campi);
    }

    /** Elenco completo delle prenotazioni presenti nel sistema. */
    public List<Prenotazione> getElencoPrenotazioni() {
        return gestore.cercaTutti(Prenotazione.class);
    }
}
