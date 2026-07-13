package entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servizi di monitoraggio dell'andamento dell'ambulatorio (per l'amministratore).
 * Elabora le prenotazioni in un intervallo di tempo producendo i conteggi
 * richiesti: totale prenotazioni, annullamenti, distribuzione per specializzazione
 * e occupazione delle {@link FasciaOraria fasce orarie}.
 *
 * <p>Il chiamante filtra prima per intervallo con {@link #filtraPrenotazioniPerIntervallo}
 * e passa il risultato agli altri metodi, evitando di ripetere il filtro per data
 * ad ogni operazione.</p>
 */
public class ServiziMonitoraggio {

    /** Prenotazioni comprese nell'intervallo [dataInizio, dataFine] (inclusi). */
    public List<Prenotazione> filtraPrenotazioniPerIntervallo(String dataInizio, String dataFine,
                                                                List<Prenotazione> elencoPrenotazioni) {
        List<Prenotazione> filtrate = new ArrayList<>();
        for (Prenotazione p : elencoPrenotazioni) {
            if (p.getData() != null
                    && p.getData().compareTo(dataInizio) >= 0
                    && p.getData().compareTo(dataFine) <= 0) {
                filtrate.add(p);
            }
        }
        return filtrate;
    }

    /** Prenotazioni con stato PRENOTATO nell'elenco. */
    public List<Prenotazione> filtraPrenotazioniPerStatoPrenotato(List<Prenotazione> elencoPrenotazioni) {
        return elencoPrenotazioni.stream()
                .filter(p -> p.getStato() == StatoPrenotazione.PRENOTATO)
                .collect(Collectors.toList());
    }

    /** Prenotazioni con stato ANNULLATO nell'elenco. */
    public List<Prenotazione> filtraPrenotazioniPerStatoAnnullato(List<Prenotazione> elencoPrenotazioni) {
        return elencoPrenotazioni.stream()
                .filter(p -> p.getStato() == StatoPrenotazione.ANNULLATO)
                .collect(Collectors.toList());
    }

    /** Numero di elementi dell'elenco filtrato. */
    public int contaPrenotazioni(List<Prenotazione> elencoFiltrato) {
        return elencoFiltrato.size();
    }

    /** Numero di visite con stato PRENOTATO nell'elenco (già filtrato per intervallo). */
    public int getNumeroPrenotazioni(List<Prenotazione> elencoPrenotazioni) {
        return contaPrenotazioni(filtraPrenotazioniPerStatoPrenotato(elencoPrenotazioni));
    }

    /** Numero di annullamenti nell'elenco (già filtrato per intervallo). */
    public int getNumeroAnnullamenti(List<Prenotazione> elencoPrenotazioni) {
        return contaPrenotazioni(filtraPrenotazioniPerStatoAnnullato(elencoPrenotazioni));
    }

    /** Prenotazioni (in qualsiasi stato) di una specializzazione, nell'elenco. */
    public List<Prenotazione> filtraPrenotazioniPerSpecializzazione(Specializzazione specializzazione,
                                                                      List<Prenotazione> elencoPrenotazioni) {
        return elencoPrenotazioni.stream()
                .filter(p -> p.getMedico() != null && p.getMedico().getSpecializzazione() == specializzazione)
                .collect(Collectors.toList());
    }

    /**
     * Numero di prenotazioni (in qualsiasi stato) per specializzazione, nell'elenco
     * (già filtrato per intervallo). Sono incluse solo le specializzazioni con almeno
     * una prenotazione.
     */
    public Map<Specializzazione, Integer> getNumeroPrenotazioniPerSpecializzazione(List<Prenotazione> elencoPrenotazioni) {
        Map<Specializzazione, Integer> risultato = new LinkedHashMap<>();
        for (Prenotazione p : elencoPrenotazioni) {
            Specializzazione s = p.getMedico() != null ? p.getMedico().getSpecializzazione() : null;
            if (s != null && !risultato.containsKey(s)) {
                risultato.put(s, contaPrenotazioni(filtraPrenotazioniPerSpecializzazione(s, elencoPrenotazioni)));
            }
        }
        return risultato;
    }

    /** Prenotazioni (in qualsiasi stato) di una fascia oraria, nell'elenco. */
    public List<Prenotazione> filtraPrenotazioniPerFasciaOraria(FasciaOraria fascia,
                                                                  List<Prenotazione> elencoPrenotazioni) {
        return elencoPrenotazioni.stream()
                .filter(p -> fascia.name().equals(p.getOrario()))
                .collect(Collectors.toList());
    }

    /**
     * Occupazione (numero di prenotazioni in qualsiasi stato) di ogni fascia oraria,
     * nell'elenco (già filtrato per intervallo).
     */
    public Map<String, Integer> getOccupazioneFasce(List<Prenotazione> elencoPrenotazioni) {
        Map<String, Integer> risultato = new LinkedHashMap<>();
        for (FasciaOraria fascia : FasciaOraria.values()) {
            int occupati = contaPrenotazioni(filtraPrenotazioniPerFasciaOraria(fascia, elencoPrenotazioni));
            risultato.put(fascia.name(), occupati);
        }
        return risultato;
    }
}
