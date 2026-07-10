package entity;

/**
 * Stato terminale: la visita è stata effettuata. Pattern State + Singleton.
 */
public class AppuntamentoEffettuato extends StatoPrenotazione {

    private static AppuntamentoEffettuato instance;

    private AppuntamentoEffettuato() {
    }

    public static AppuntamentoEffettuato getInstance() {
        if (instance == null) {
            instance = new AppuntamentoEffettuato();
        }
        return instance;
    }

    @Override
    public StatoVisita getTipo() {
        return StatoVisita.EFFETTUATO;
    }
}
