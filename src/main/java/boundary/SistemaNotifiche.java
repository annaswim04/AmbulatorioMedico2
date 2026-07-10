package boundary;

/**
 * Astrazione del sistema di notifiche, collocata nella BOUNDARY.
 *
 * Pattern: ADAPTER (target). Il resto dell'applicazione dipende solo da questa
 * interfaccia e ignora la libreria COTS concreta usata per l'invio (Jakarta Mail).
 * Sostituire il COTS significa fornire un'altra implementazione, senza toccare
 * controller o entity.
 */
public interface SistemaNotifiche {

    /** Invia al paziente la conferma di una prenotazione. */
    boolean inviaConfermaPrenotazione(String destinatario, String nomeMedico,
                                      String data, String orario);

    /** Ottiene l'istanza del sistema di notifiche configurato. */
    static SistemaNotifiche getInstance() {
        return SistemaNotificheJakarta.getInstance();
    }
}
