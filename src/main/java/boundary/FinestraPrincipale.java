package boundary;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale dell'applicazione: menu di accesso ai quattro casi d'uso
 * implementati. È il punto di ingresso della GUI Swing.
 */
public class FinestraPrincipale extends JFrame {

    public FinestraPrincipale() {
        super("Ambulatorio Medico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 340);
        setLocationRelativeTo(null);
        inizializza();
    }

    private void inizializza() {
        JPanel pannello = new JPanel();
        pannello.setLayout(new GridLayout(0, 1, 10, 10));
        pannello.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titolo = new JLabel("Gestione Ambulatorio Medico", SwingConstants.CENTER);
        titolo.setFont(titolo.getFont().deriveFont(Font.BOLD, 16f));
        pannello.add(titolo);

        pannello.add(creaBottone("Effettua prenotazione (paziente)",
                () -> new FormEffettuaPrenotazione().setVisible(true)));
        pannello.add(creaBottone("Visualizza disponibilità",
                () -> new FormVisualizzaDisponibilita().setVisible(true)));
        pannello.add(creaBottone("Elenco prenotazioni (medico)",
                () -> new FormElencoPrenotazioniMedico().setVisible(true)));
        pannello.add(creaBottone("Monitoraggio ambulatorio (amministratore)",
                () -> new FormMonitoraggio().setVisible(true)));

        add(pannello);
    }

    private JButton creaBottone(String testo, Runnable azione) {
        JButton b = new JButton(testo);
        b.addActionListener(e -> azione.run());
        return b;
    }
}
