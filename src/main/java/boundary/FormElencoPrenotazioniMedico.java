package boundary;

import controller.ControllerPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Boundary del caso d'uso "Visualizza elenco prenotazioni (medico)".
 * Il medico consulta l'elenco delle visite prenotate presso il proprio ambulatorio.
 *
 * <p>La UI è definita in {@code FormElencoPrenotazioniMedico.form}. La boundary
 * comunica solo con il {@link ControllerPrenotazioni} e riceve le righe come
 * array di stringhe: non importa mai il package {@code entity}.</p>
 */
public class FormElencoPrenotazioniMedico {

    private JPanel elencoPrenotazioniPanel;
    private JTextField campoEmailMedico;
    private JButton mostraButton;
    private JTable tabella;

    private final ControllerPrenotazioni controller = ControllerPrenotazioni.getInstance();
    private final DefaultTableModel modello = new DefaultTableModel(
            new Object[]{"Data", "Fascia oraria", "Paziente", "Recapito", "Stato"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    public FormElencoPrenotazioniMedico() {
        tabella.setModel(modello);
        tabella.getTableHeader().setReorderingAllowed(false);
        mostraButton.addActionListener(e -> caricaPrenotazioni());
    }

    private void caricaPrenotazioni() {
        modello.setRowCount(0);
        String email = campoEmailMedico.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(elencoPrenotazioniPanel, "Inserisci l'email del medico.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> prenotazioni = controller.visualizzaElencoPrenotazioni(email);
        if (prenotazioni.isEmpty()) {
            JOptionPane.showMessageDialog(elencoPrenotazioniPanel,
                    "Nessuna prenotazione trovata per questo medico.",
                    "Elenco vuoto", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (String[] riga : prenotazioni) {
            modello.addRow(riga);
        }
    }

    /** Crea e mostra la finestra del caso d'uso. Restituisce il {@link JFrame} creato. */
    public JFrame apriFormElencoPrenotazioniMedico() {
        JFrame frame = new JFrame("Elenco prenotazioni - Medico");
        frame.setContentPane(elencoPrenotazioniPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }
}
