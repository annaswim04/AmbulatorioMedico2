package entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contenitore dei risultati del monitoraggio dell'ambulatorio in un intervallo
 * di tempo. Prodotto da {@link ServiziMonitoraggio} e mostrato dalla boundary.
 */
public class RisultatoMonitoraggio {

    private final String dataInizio;
    private final String dataFine;
    private int numeroPrenotazioni;
    private int numeroAnnullamenti;
    private final Map<Specializzazione, Integer> prenotazioniPerSpecializzazione = new LinkedHashMap<>();
    private final Map<String, Integer> occupazioneFasce = new LinkedHashMap<>();

    public RisultatoMonitoraggio(String dataInizio, String dataFine) {
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

    public String getDataInizio() {
        return dataInizio;
    }

    public String getDataFine() {
        return dataFine;
    }

    public int getNumeroPrenotazioni() {
        return numeroPrenotazioni;
    }

    public void setNumeroPrenotazioni(int numeroPrenotazioni) {
        this.numeroPrenotazioni = numeroPrenotazioni;
    }

    public int getNumeroAnnullamenti() {
        return numeroAnnullamenti;
    }

    public void setNumeroAnnullamenti(int numeroAnnullamenti) {
        this.numeroAnnullamenti = numeroAnnullamenti;
    }

    public Map<Specializzazione, Integer> getPrenotazioniPerSpecializzazione() {
        return prenotazioniPerSpecializzazione;
    }

    public Map<String, Integer> getOccupazioneFasce() {
        return occupazioneFasce;
    }
}
