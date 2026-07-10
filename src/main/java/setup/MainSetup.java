package setup;

import boundary.FinestraPrincipale;
import database.GestorePersistenza;
import database.JpaUtil;

import javax.swing.SwingUtilities;

/**
 * Punto di avvio dell'applicazione.
 * Inizializza la persistenza (Hibernate crea/aggiorna le tabelle MySQL),
 * inserisce i dati di test e apre la GUI Swing.
 *
 * Richiede un server MySQL attivo su 127.0.0.1:3306 con le credenziali indicate
 * in src/main/resources/META-INF/persistence.xml.
 */
public class MainSetup {

    public static void main(String[] args) {
        // Avvia Hibernate / crea l'EntityManagerFactory (Singleton)
        JpaUtil.getInstance();

        // Popola il database con i dati iniziali (idempotente)
        GestorePersistenza gestore = new GestorePersistenza();
        DatiTest.popola(gestore);

        // Avvia la GUI sul thread di Swing
        SwingUtilities.invokeLater(() -> new FinestraPrincipale().setVisible(true));

        System.out.println("Applicazione avviata.");
    }
}
