package entity;

/**
 * Stato terminale: la prenotazione è stata annullata. Pattern State + Singleton.
 */
public class AppuntamentoAnnullato extends StatoPrenotazione {

    private static AppuntamentoAnnullato instance;

    private AppuntamentoAnnullato() {
    }

    public static AppuntamentoAnnullato getInstance() {
        if (instance == null) {
            instance = new AppuntamentoAnnullato();
        }
        return instance;
    }

    @Override
    public StatoVisita getTipo() {
        return StatoVisita.ANNULLATO;
    }
}
