package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale dell'applicazione: menu di accesso ai quattro casi d'uso
 * implementati. È il punto di ingresso della GUI Swing.
 *
 * <p>La UI è definita nel file {@code FinestraPrincipale.form} (GUI Designer di
 * IntelliJ): i campi sotto sono agganciati per nome ai componenti del form.</p>
 */
public class FinestraPrincipale {

    private JPanel finestraPrincipalePanel;
    private JButton effettuaPrenotazioneButton;
    private JButton visualizzaDisponibilitaButton;
    private JButton elencoPrenotazioniButton;
    private JButton monitoraggioButton;

    // Riferimenti alle finestre aperte (per non duplicarle)
    private JFrame frameEffettuaPrenotazione;
    private JFrame frameVisualizzaDisponibilita;
    private JFrame frameElencoPrenotazioni;
    private JFrame frameMonitoraggio;

    public FinestraPrincipale() {
        effettuaPrenotazioneButton.addActionListener(e -> {
            if (nonVisibile(frameEffettuaPrenotazione)) {
                frameEffettuaPrenotazione = new FormEffettuaPrenotazione().apriFormEffettuaPrenotazione();
            } else {
                frameEffettuaPrenotazione.toFront();
            }
        });
        visualizzaDisponibilitaButton.addActionListener(e -> {
            if (nonVisibile(frameVisualizzaDisponibilita)) {
                frameVisualizzaDisponibilita = new FormVisualizzaDisponibilita().apriFormVisualizzaDisponibilita();
            } else {
                frameVisualizzaDisponibilita.toFront();
            }
        });
        elencoPrenotazioniButton.addActionListener(e -> {
            if (nonVisibile(frameElencoPrenotazioni)) {
                frameElencoPrenotazioni = new FormElencoPrenotazioniMedico().apriFormElencoPrenotazioniMedico();
            } else {
                frameElencoPrenotazioni.toFront();
            }
        });
        monitoraggioButton.addActionListener(e -> {
            if (nonVisibile(frameMonitoraggio)) {
                frameMonitoraggio = new FormMonitoraggio().apriFormMonitoraggio();
            } else {
                frameMonitoraggio.toFront();
            }
        });
    }

    private boolean nonVisibile(JFrame frame) {
        return frame == null || !frame.isDisplayable();
    }

    /**
     * Crea e mostra la finestra principale. Restituisce il {@link JFrame} creato.
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

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return finestraPrincipalePanel;
    }
}
