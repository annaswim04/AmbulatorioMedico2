package entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servizi di monitoraggio dell'andamento dell'ambulatorio (per l'amministratore).
 * Elabora le prenotazioni in un intervallo di tempo producendo i conteggi
 * richiesti: totale prenotazioni, annullamenti, distribuzione per specializzazione
 * e occupazione delle fasce orarie (sfruttando il Composite {@link Giornata}).
 */
public class ServiziMonitoraggio {

    private final RegistroPrenotazioni registroPrenotazioni = new RegistroPrenotazioni();

    /** Produce il riepilogo completo per l'intervallo [dataInizio, dataFine]. */
    public RisultatoMonitoraggio monitora(String dataInizio, String dataFine) {
        List<Prenotazione> prenotazioni =
                registroPrenotazioni.getPrenotazioniPerIntervallo(dataInizio, dataFine);

        RisultatoMonitoraggio risultato = new RisultatoMonitoraggio(dataInizio, dataFine);
        risultato.setNumeroPrenotazioni(getNumeroPrenotazioni(prenotazioni));
        risultato.setNumeroAnnullamenti(getNumeroAnnullamenti(prenotazioni));

        // Prenotazioni per specializzazione (escluse le annullate)
        for (Specializzazione s : Specializzazione.values()) {
            int n = (int) prenotazioni.stream()
                    .filter(p -> p.getStatoVisita() != StatoVisita.ANNULLATO)
                    .filter(p -> p.getMedico() != null && p.getMedico().getSpecializzazione() == s)
                    .count();
            if (n > 0) {
                risultato.getPrenotazioniPerSpecializzazione().put(s, n);
            }
        }

        // Occupazione delle fasce orarie principali (Mattina / Primo / Tardo pomeriggio)
        Giornata giornata = new Giornata();
        for (PeriodoDiGiornata periodo : giornata.getPeriodi()) {
            Set<String> orariPeriodo = new HashSet<>();
            for (SlotOrario slot : periodo.getSlot()) {
                orariPeriodo.add(slot.getOrario());
            }
            int occupati = (int) prenotazioni.stream()
                    .filter(p -> p.getStatoVisita() != StatoVisita.ANNULLATO)
                    .filter(p -> orariPeriodo.contains(p.getOrario()))
                    .count();
            risultato.getOccupazioneFasce().put(periodo.getNome(), occupati);
        }

        return risultato;
    }

    /** Numero di visite prenotate (non annullate) nell'elenco. */
    public int getNumeroPrenotazioni(List<Prenotazione> prenotazioni) {
        return (int) prenotazioni.stream()
                .filter(p -> p.getStatoVisita() != StatoVisita.ANNULLATO)
                .count();
    }

    /** Numero di annullamenti nell'elenco. */
    public int getNumeroAnnullamenti(List<Prenotazione> prenotazioni) {
        return (int) prenotazioni.stream()
                .filter(p -> p.getStatoVisita() == StatoVisita.ANNULLATO)
                .count();
    }
}
