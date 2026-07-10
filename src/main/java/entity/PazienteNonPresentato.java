package entity;

/**
 * Stato terminale: il paziente non si è presentato alla visita.
 * Pattern State + Singleton.
 */
public class PazienteNonPresentato extends StatoPrenotazione {

    private static PazienteNonPresentato instance;

    private PazienteNonPresentato() {
    }

    public static PazienteNonPresentato getInstance() {
        if (instance == null) {
            instance = new PazienteNonPresentato();
        }
        return instance;
    }

    @Override
    public StatoVisita getTipo() {
        return StatoVisita.NON_PRESENTATO;
    }
}
