package setup;

import boundary.FinestraPrincipale;
import database.GestorePersistenza;
import database.JpaUtil;

import javax.swing.SwingUtilities;

/**
 * Punto di avvio dell'applicazione.
 * Inizializza la persistenza (Hibernate crea/aggiorna le tabelle MySQL),
 * inserisce i dati di test e apre la GUI Swing.
 */
public class MainSetup {

    public static void main(String[] args) {
        // Avvia Hibernate / crea l'EntityManagerFactory
        JpaUtil.getInstance();

        // Popola il database con i dati iniziali
        GestorePersistenza gestore = new GestorePersistenza();
        DatiTest.popola(gestore);

        // Avvia la GUI sul thread di Swing
        SwingUtilities.invokeLater(() -> new FinestraPrincipale().apriFinestraPrincipale());

        System.out.println("Applicazione avviata.");
    }
}
