package boundary;

/**
 * Pattern: ADAPTER. Il resto dell'applicazione dipende solo da questa
 * interfaccia e ignora la libreria COTS concreta usata per l'invio (Jakarta Mail).
 */
public interface SistemaNotifiche {

    /** Invia al paziente la conferma di una prenotazione. */
    boolean inviaConfermaPrenotazione(String destinatario, String nomeMedico,
                                      String data, String fascia);

    /** Ottiene l'istanza del sistema di notifiche configurato. */
    static SistemaNotifiche getInstance() {
        return SistemaNotificheJakarta.getInstance();
    }
}
