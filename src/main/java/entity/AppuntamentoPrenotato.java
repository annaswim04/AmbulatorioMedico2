package entity;

/**
 * Stato iniziale di una prenotazione. È l'unico stato da cui sono ammesse
 * transizioni. Pattern State (concrete state) + Singleton (stato senza dati propri).
 */
public class AppuntamentoPrenotato extends StatoPrenotazione {

    private static AppuntamentoPrenotato instance;

    private AppuntamentoPrenotato() {
    }

    public static AppuntamentoPrenotato getInstance() {
        if (instance == null) {
            instance = new AppuntamentoPrenotato();
        }
        return instance;
    }

    @Override
    public StatoVisita getTipo() {
        return StatoVisita.PRENOTATO;
    }

    @Override
    public StatoPrenotazione annulla() {
        return AppuntamentoAnnullato.getInstance();
    }

    @Override
    public StatoPrenotazione effettua() {
        return AppuntamentoEffettuato.getInstance();
    }

    @Override
    public StatoPrenotazione segnalaAssenza() {
        return PazienteNonPresentato.getInstance();
    }
}
