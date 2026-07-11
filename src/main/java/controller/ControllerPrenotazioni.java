package controller;

import entity.DisponibilitaMedico;
import entity.FasciaOraria;
import entity.Medico;
import entity.Paziente;
import entity.Prenotazione;
import entity.RegistroPrenotazioni;
import entity.RegistroSpecializzazioni;
import entity.RegistroUtenti;
import entity.RisultatoMonitoraggio;
import entity.ServiziMonitoraggio;
import entity.Specializzazione;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller di facciata per i casi d'uso legati alle prenotazioni.
 *
 * <p>Pattern: FACADE. : orchestra i {@code Registro*} del livello entity e non
 * contiene né codice Swing né accessi diretti al DB.</p>
 *
 * <p>Per rispettare il flusso BCED, i metodi <b>non espongono oggetti di dominio</b>:
 * ricevono e restituiscono solo tipi semplici e strutture di stringhe
 * ({@code String}, {@code String[]}, {@code List<String[]>}, {@code Map}). È il
 * controller a tradurre le entity in questi dati, così la boundary non importa
 * mai il package {@code entity}.</p>
 */
public class ControllerPrenotazioni {

    // Esiti esposti alla boundary (rimappano quelli del registro)
    public static final int SUCCESSO = RegistroPrenotazioni.SUCCESSO;
    public static final int PAZIENTE_NON_ESISTENTE = RegistroPrenotazioni.PAZIENTE_NON_ESISTENTE;
    public static final int MEDICO_NON_ESISTENTE = RegistroPrenotazioni.MEDICO_NON_ESISTENTE;
    public static final int FASCIA_NON_DISPONIBILE = RegistroPrenotazioni.FASCIA_NON_DISPONIBILE;


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

    /** Descrizioni delle specializzazioni offerte dall'ambulatorio. */
    public String[] getSpecializzazioni() {
        List<Specializzazione> specializzazioni = registroSpecializzazioni.getSpecializzazioni();
        String[] descrizioni = new String[specializzazioni.size()];
        for (int i = 0; i < specializzazioni.size(); i++) {
            descrizioni[i] = specializzazioni.get(i).getDescrizione();
        }
        return descrizioni;
    }

    /**
     * Medici di una specializzazione (indicata per descrizione).
     * Ogni elemento è la coppia {@code {email, nomeCompleto}}.
     */
    public List<String[]> getMedici(String descrizioneSpecializzazione) {
        List<String[]> medici = new ArrayList<>();
        Specializzazione specializzazione = specializzazioneDaDescrizione(descrizioneSpecializzazione);
        if (specializzazione == null) {
            return medici;
        }
        for (Medico m : registroSpecializzazioni.getMedici(specializzazione)) {
            medici.add(new String[]{m.getEmail(), m.getNomeCompleto()});
        }
        return medici;
    }

    /** Nomi delle fasce orarie libere di un medico in una data. */
    public String[] visualizzaDisponibilita(String emailMedico, String data) {
        Medico medico = registroUtenti.getMedico(emailMedico);
        if (medico == null) {
            return new String[0];
        }
        DisponibilitaMedico disponibilita = registroPrenotazioni.getDisponibilita(medico, data);
        List<FasciaOraria> fasce = disponibilita.getFasceOrarieDisponibili();
        String[] nomi = new String[fasce.size()];
        for (int i = 0; i < fasce.size(); i++) {
            nomi[i] = fasce.get(i).getNome();
        }
        return nomi;
    }

    // --- UC: Effettua prenotazione ---

    /** Effettua una prenotazione; restituisce uno dei codici di esito pubblici. */
    public int effettuaPrenotazione(String emailPaziente, String emailMedico,
                                    String data, String fascia) {
        return registroPrenotazioni.salvaPrenotazione(emailPaziente, emailMedico, data, fascia);
    }

    // --- UC: Elenco prenotazioni (medico) ---

    /**
     * Prenotazioni di un medico. Ogni elemento è la riga
     * {@code {data, fascia, paziente, stato, emailPaziente}}.
     * L'ultimo campo (email del paziente) è ad uso interno della boundary per
     * richiamare {@link #getDatiPaziente(String)}: non va mostrato in tabella.
     */
    public List<String[]> visualizzaElencoPrenotazioni(String emailMedico) {
        return visualizzaElencoPrenotazioni(emailMedico, null, null);
    }

    /**
     * Come {@link #visualizzaElencoPrenotazioni(String)}, filtrabile per data
     * (formato {@code yyyy-MM-dd}) e/o fascia oraria. Filtri {@code null} o
     * vuoti sono ignorati.
     */
    public List<String[]> visualizzaElencoPrenotazioni(String emailMedico, String filtroData, String filtroFascia) {
        List<String[]> righe = new ArrayList<>();
        Medico medico = registroUtenti.getMedico(emailMedico);
        if (medico == null) {
            return righe;
        }
        for (Prenotazione p : registroPrenotazioni.getPrenotazioniPerMedico(medico, filtroData, filtroFascia)) {
            String paziente = p.getPaziente() != null ? p.getPaziente().getNomeCompleto() : "-";
            String emailPaziente = p.getPaziente() != null ? p.getPaziente().getEmail() : "";
            righe.add(new String[]{
                    p.getData(), p.getOrario(), paziente, p.getStato().descrizione(), emailPaziente
            });
        }
        return righe;
    }

    /** Nomi delle fasce orarie della giornata, per popolare un filtro. */
    public String[] getFasceOrarie() {
        List<FasciaOraria> fasce = FasciaOraria.valori();
        String[] nomi = new String[fasce.size()];
        for (int i = 0; i < fasce.size(); i++) {
            nomi[i] = fasce.get(i).getNome();
        }
        return nomi;
    }

    // --- UC: Visualizza dati paziente (dal dettaglio elenco prenotazioni) ---

    /**
     * Dati anagrafici di un paziente, come riga {@code {email, nome, cognome, recapito}}.
     * Restituisce {@code null} se il paziente non esiste.
     */
    public String[] getDatiPaziente(String emailPaziente) {
        Paziente paziente = registroUtenti.getPaziente(emailPaziente);
        if (paziente == null) {
            return null;
        }
        return new String[]{
                paziente.getEmail(), paziente.getNome(), paziente.getCognome(), paziente.getRecapitoTelefonico()
        };
    }

    // --- UC: Monitoraggio ambulatorio ---

    /**
     * Riepilogo del monitoraggio, come mappa con tre chiavi:
     * <ul>
     *   <li>{@code "totali"} → una riga {@code {numeroPrenotazioni, numeroAnnullamenti}};</li>
     *   <li>{@code "specializzazioni"} → righe {@code {descrizione, conteggio}};</li>
     *   <li>{@code "fasce"} → righe {@code {fascia, occupazione}}.</li>
     * </ul>
     */
    public Map<String, List<String[]>> visualizzaMonitoraggioAmbulatorio(String dataInizio, String dataFine) {
        RisultatoMonitoraggio r = serviziMonitoraggio.monitora(dataInizio, dataFine);
        Map<String, List<String[]>> riepilogo = new LinkedHashMap<>();

        List<String[]> totali = new ArrayList<>();
        totali.add(new String[]{
                String.valueOf(r.getNumeroPrenotazioni()),
                String.valueOf(r.getNumeroAnnullamenti())
        });
        riepilogo.put("totali", totali);

        List<String[]> specializzazioni = new ArrayList<>();
        for (Map.Entry<Specializzazione, Integer> e : r.getPrenotazioniPerSpecializzazione().entrySet()) {
            specializzazioni.add(new String[]{e.getKey().getDescrizione(), String.valueOf(e.getValue())});
        }
        riepilogo.put("specializzazioni", specializzazioni);

        List<String[]> fasce = new ArrayList<>();
        for (Map.Entry<String, Integer> e : r.getOccupazioneFasce().entrySet()) {
            fasce.add(new String[]{e.getKey(), String.valueOf(e.getValue())});
        }
        riepilogo.put("fasce", fasce);

        return riepilogo;
    }

    // --- Helper interni ---

    private Specializzazione specializzazioneDaDescrizione(String descrizione) {
        for (Specializzazione s : Specializzazione.values()) {
            if (s.getDescrizione().equals(descrizione)) {
                return s;
            }
        }
        return null;
    }
}
