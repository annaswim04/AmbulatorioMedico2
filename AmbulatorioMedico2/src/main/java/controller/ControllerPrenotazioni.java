package controller;

import entity.DisponibilitaMedico;
import entity.FasciaOraria;
import entity.Medico;
import entity.Paziente;
import entity.Prenotazione;
import entity.RegistroPrenotazioni;
import entity.RegistroSpecializzazioni;
import entity.RegistroUtenti;
import entity.ServiziMonitoraggio;
import entity.Specializzazione;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller di facciata per i casi d'uso legati alle prenotazioni.
 * Pattern: FACADE.
 */

public class ControllerPrenotazioni {

    // Esiti esposti alla boundary (riprendono quelli del registro)
    public static final int SUCCESSO = RegistroPrenotazioni.SUCCESSO;
    public static final int PAZIENTE_NON_ESISTENTE = RegistroPrenotazioni.PAZIENTE_NON_ESISTENTE;
    public static final int MEDICO_NON_ESISTENTE = RegistroPrenotazioni.MEDICO_NON_ESISTENTE;
    public static final int FASCIA_NON_DISPONIBILE = RegistroPrenotazioni.FASCIA_NON_DISPONIBILE;

    // --------------------------------- UC: Visualizza disponibilità ---------------------------------

    /** Nomi delle specializzazioni offerte dall'ambulatorio. */
    public static String[] getSpecializzazioni() {
        RegistroSpecializzazioni registroSpecializzazioni = new RegistroSpecializzazioni();
        List<Specializzazione> specializzazioni = registroSpecializzazioni.getSpecializzazioni();
        String[] nomi = new String[specializzazioni.size()];
        for (int i = 0; i < specializzazioni.size(); i++) {
            nomi[i] = specializzazioni.get(i).getNome();
        }
        return nomi;
    }

    /**
     * Medici di una specializzazione (indicata per nome).
     * Ogni elemento è la coppia {email, nomeCompleto}.
     */
    public static List<String[]> getMedici(String nomeSpecializzazione) {
        List<String[]> medici = new ArrayList<>();
        RegistroSpecializzazioni registroSpecializzazioni = new RegistroSpecializzazioni();
        Specializzazione specializzazione = registroSpecializzazioni.getSpecializzazione(nomeSpecializzazione);
        if (specializzazione == null) {
            return medici;
        }
        for (Medico m : registroSpecializzazioni.getMedici(specializzazione)) {
            medici.add(new String[]{m.getEmail(), m.getNomeCompleto()});
        }
        return medici;
    }

    /** Nomi delle fasce orarie libere di un medico in una data. */
    public static String[] visualizzaDisponibilita(String emailMedico, String data) {
        RegistroUtenti registroUtenti = new RegistroUtenti();
        Medico medico = registroUtenti.getMedico(emailMedico);
        if (medico == null) {
            return new String[0];
        }
        RegistroPrenotazioni registroPrenotazioni = new RegistroPrenotazioni();
        DisponibilitaMedico disponibilita = registroPrenotazioni.getDisponibilita(medico, data);
        List<FasciaOraria> fasce = disponibilita.getFasceOrarieDisponibili();
        String[] nomi = new String[fasce.size()];
        for (int i = 0; i < fasce.size(); i++) {
            nomi[i] = fasce.get(i).name();
        }
        return nomi;
    }

    /**
     * Date in cui un medico ha almeno una fascia oraria libera
     * (usato per abilitare solo quei giorni nel calendario).
     */
    public static String[] getDateDisponibili(String emailMedico) {
        RegistroUtenti registroUtenti = new RegistroUtenti();
        Medico medico = registroUtenti.getMedico(emailMedico);
        if (medico == null) {
            return new String[0];
        }
        RegistroPrenotazioni registroPrenotazioni = new RegistroPrenotazioni();
        List<DisponibilitaMedico> disponibilita = registroPrenotazioni.getDateDisponibili(medico);
        String[] date = new String[disponibilita.size()];
        for (int i = 0; i < disponibilita.size(); i++) {
            date[i] = disponibilita.get(i).getData();
        }
        return date;
    }

    // --------------------------------- UC: Effettua prenotazione ---------------------------------

    /** Effettua una prenotazione; restituisce uno dei codici di esito pubblici. */
    public static int effettuaPrenotazione(String emailPaziente, String emailMedico,
                                    String data, String fascia) {
        RegistroPrenotazioni registroPrenotazioni = new RegistroPrenotazioni();
        return registroPrenotazioni.salvaPrenotazione(emailPaziente, emailMedico, data, fascia);
    }

    // --------------------------------- UC: Elenco prenotazioni (medico) ---------------------------------

    /**
     * Filtrabile per data e/o fascia oraria. Filtri null o vuoti sono ignorati.
     */
    public static List<String[]> visualizzaElencoPrenotazioni(String emailMedico, String filtroData, String filtroFascia) {
        List<String[]> righe = new ArrayList<>();
        RegistroUtenti registroUtenti = new RegistroUtenti();
        Medico medico = registroUtenti.getMedico(emailMedico);
        if (medico == null) {
            return righe;
        }
        RegistroPrenotazioni registroPrenotazioni = new RegistroPrenotazioni();
        for (Prenotazione p : registroPrenotazioni.getPrenotazioniPerMedico(medico, filtroData, filtroFascia)) {
            String paziente = p.getPaziente() != null ? p.getPaziente().getNomeCompleto() : "-";
            String emailPaziente = p.getPaziente() != null ? p.getPaziente().getEmail() : "";
            String fascia = p.getFascia() != null ? p.getFascia().name() : "-";
            righe.add(new String[]{
                    p.getData(), fascia, paziente, p.getStato().name(), emailPaziente
            });
        }
        return righe;
    }

    /** Nomi delle fasce orarie della giornata, per popolare un filtro. */
    public static String[] getFasceOrarie() {
        List<FasciaOraria> fasce = Arrays.asList(FasciaOraria.values());
        String[] nomi = new String[fasce.size()];
        for (int i = 0; i < fasce.size(); i++) {
            nomi[i] = fasce.get(i).name();
        }
        return nomi;
    }

    // Visualizza dati paziente (dal dettaglio elenco prenotazioni)

    /**
     * Dati anagrafici di un paziente, come riga {email, nome, cognome, recapito}.
     * Restituisce null se il paziente non esiste.
     */
    public static String[] getDatiPaziente(String emailPaziente) {
        RegistroUtenti registroUtenti = new RegistroUtenti();
        Paziente paziente = registroUtenti.getPaziente(emailPaziente);
        if (paziente == null) {
            return null;
        }
        return new String[]{
                paziente.getEmail(), paziente.getNome(), paziente.getCognome(), paziente.getRecapitoTelefonico()
        };
    }

    // --------------------------------- UC: Monitoraggio ambulatorio ---------------------------------

    /**
     * Riepilogo del monitoraggio, come mappa con tre chiavi:
     *  "totali" → riga {numeroPrenotazioni, numeroAnnullamenti}
     *  "specializzazioni" → righe {nome, conteggio}
     *  "fasce" → righe {fascia, occupazione}
     */
    public static Map<String, List<String[]>> visualizzaMonitoraggioAmbulatorio(String dataInizio, String dataFine) {
        RegistroPrenotazioni registroPrenotazioni = new RegistroPrenotazioni();
        ServiziMonitoraggio serviziMonitoraggio = new ServiziMonitoraggio();
        List<Prenotazione> elencoPrenotazioni = registroPrenotazioni.getElencoPrenotazioni();
        List<Prenotazione> elencoNelPeriodo =
                serviziMonitoraggio.filtraPrenotazioniPerIntervallo(dataInizio, dataFine, elencoPrenotazioni);
        Map<String, List<String[]>> riepilogo = new LinkedHashMap<>();

        List<String[]> totali = new ArrayList<>();
        totali.add(new String[]{
                String.valueOf(serviziMonitoraggio.getNumeroPrenotazioni(elencoNelPeriodo)),
                String.valueOf(serviziMonitoraggio.getNumeroAnnullamenti(elencoNelPeriodo))
        });
        riepilogo.put("totali", totali);

        List<String[]> specializzazioni = new ArrayList<>();
        Map<Specializzazione, Integer> perSpecializzazione =
                serviziMonitoraggio.getNumeroPrenotazioniPerSpecializzazione(elencoNelPeriodo);
        for (Map.Entry<Specializzazione, Integer> e : perSpecializzazione.entrySet()) {
            specializzazioni.add(new String[]{e.getKey().getNome(), String.valueOf(e.getValue())});
        }
        riepilogo.put("specializzazioni", specializzazioni);

        List<String[]> fasce = new ArrayList<>();
        Map<String, Integer> occupazioneFasce =
                serviziMonitoraggio.getOccupazioneFasce(elencoNelPeriodo);
        for (Map.Entry<String, Integer> e : occupazioneFasce.entrySet()) {
            fasce.add(new String[]{e.getKey(), String.valueOf(e.getValue())});
        }
        riepilogo.put("fasce", fasce);

        return riepilogo;
    }
}
