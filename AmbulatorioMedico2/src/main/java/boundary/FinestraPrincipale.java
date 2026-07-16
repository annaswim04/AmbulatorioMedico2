package boundary;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale dell'applicazione: menu di accesso ai casi d'uso implementati.
 */
public class FinestraPrincipale {

    private JPanel finestraPrincipalePanel;
    private JButton visualizzaDisponibilitaButton;
    private JButton elencoPrenotazioniButton;
    private JButton monitoraggioButton;

    // Riferimenti alle finestre aperte (per non duplicarle)
    private JFrame frameVisualizzaDisponibilita;
    private JFrame frameElencoPrenotazioni;
    private JFrame frameMonitoraggio;

    public FinestraPrincipale() {
        // La prenotazione si avvia da "Visualizza disponibilità" dopo aver
        // scelto specializzazione, medico, data e fascia oraria.
        visualizzaDisponibilitaButton.addActionListener(e -> apri(
                () -> nonVisibile(frameVisualizzaDisponibilita),
                () -> frameVisualizzaDisponibilita = new FormVisualizzaDisponibilita().apriFormVisualizzaDisponibilita(),
                () -> frameVisualizzaDisponibilita.toFront()));
        elencoPrenotazioniButton.addActionListener(e -> apri(
                () -> nonVisibile(frameElencoPrenotazioni),
                () -> frameElencoPrenotazioni = new FormElencoPrenotazioniMedico().apriFormElencoPrenotazioniMedico(),
                () -> frameElencoPrenotazioni.toFront()));
        monitoraggioButton.addActionListener(e -> apri(
                () -> nonVisibile(frameMonitoraggio),
                () -> frameMonitoraggio = new FormMonitoraggio().apriFormMonitoraggio(),
                () -> frameMonitoraggio.toFront()));
    }

    /**
     * Apre la finestra (o la porta in primo piano se già aperta).
     */
    private void apri(java.util.function.BooleanSupplier daAprire, Runnable apertura, Runnable inPrimoPiano) {
        try {
            if (daAprire.getAsBoolean()) {
                apertura.run();
            } else {
                inPrimoPiano.run();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(finestraPrincipalePanel,
                    t.getClass().getSimpleName() + ": " + t.getMessage(),
                    "Errore nell'apertura della finestra", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean nonVisibile(JFrame frame) {
        return frame == null || !frame.isDisplayable();
    }

    /**
     * Crea e mostra la finestra principale. Restituisce il JFrame creato.
     */
    public JFrame apriFinestraPrincipale() {
        JFrame frame = new JFrame("Ambulatorio Medico");

        frame.setContentPane(finestraPrincipalePanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {

        FinestraPrincipale finestraPrincipale = new FinestraPrincipale();

        finestraPrincipale.apriFinestraPrincipale();
    }

}
