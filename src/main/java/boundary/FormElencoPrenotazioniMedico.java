package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import controller.ControllerPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Boundary del caso d'uso "Visualizza elenco prenotazioni (medico)".
 * Il medico consulta l'elenco delle visite prenotate presso il proprio ambulatorio
 * e, selezionando una riga, visualizza i dati anagrafici del paziente corrispondente.
 *
 * <p>La UI è definita in {@code FormElencoPrenotazioniMedico.form}. La boundary
 * comunica solo con il {@link ControllerPrenotazioni} e riceve le righe come
 * array di stringhe: non importa mai il package {@code entity}.</p>
 */
public class FormElencoPrenotazioniMedico {

    private JPanel elencoPrenotazioniPanel;
    private JTextField campoEmailMedico;
    private JButton mostraButton;
    private JTextField campoFiltroData;
    private JComboBox<String> comboFiltroFascia;
    // Bind del .form: avvolge "tabella" e abilita lo scroll con molte prenotazioni.
    // Non richiamato direttamente in codice: il collegamento avviene per riflessione dal form loader.
    @SuppressWarnings("unused")
    private JScrollPane ScrollPane;
    private JTable tabella;

    private final DefaultTableModel modello = new DefaultTableModel(
            new Object[]{"Data", "Fascia oraria", "Paziente", "Stato"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    /**
     * Righe complete restituite dal controller (con email paziente), parallele al modello di tabella.
     */
    private final List<String[]> prenotazioniCorrenti = new ArrayList<>();

    public FormElencoPrenotazioniMedico() {
        tabella.setModel(modello);
        tabella.getTableHeader().setReorderingAllowed(false);

        comboFiltroFascia.addItem("Tutte");
        for (String fascia : ControllerPrenotazioni.getFasceOrarie()) {
            comboFiltroFascia.addItem(fascia);
        }
        comboFiltroFascia.setSelectedIndex(0);

        mostraButton.addActionListener(_ -> caricaPrenotazioni());
        tabella.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostraDatiPaziente();
            }
        });
    }

    private void caricaPrenotazioni() {
        modello.setRowCount(0);
        prenotazioniCorrenti.clear();
        String email = campoEmailMedico.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(elencoPrenotazioniPanel, "Inserisci l'email del medico.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String filtroData = campoFiltroData.getText().trim();
        String filtroFascia = comboFiltroFascia.getSelectedIndex() > 0
                ? (String) comboFiltroFascia.getSelectedItem()
                : null;

        List<String[]> prenotazioni = ControllerPrenotazioni.visualizzaElencoPrenotazioni(
                email, filtroData.isEmpty() ? null : filtroData, filtroFascia);
        if (prenotazioni.isEmpty()) {
            JOptionPane.showMessageDialog(elencoPrenotazioniPanel,
                    "Nessuna prenotazione trovata per questo medico.",
                    "Elenco vuoto", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (String[] riga : prenotazioni) {
            prenotazioniCorrenti.add(riga);
            modello.addRow(new Object[]{riga[0], riga[1], riga[2], riga[3]});
        }
    }

    /**
     * Mostra i dati del paziente associato alla riga selezionata in tabella.
     */
    private void mostraDatiPaziente() {
        int riga = tabella.getSelectedRow();
        if (riga < 0 || riga >= prenotazioniCorrenti.size()) {
            return;
        }
        String emailPaziente = prenotazioniCorrenti.get(riga)[4];
        if (emailPaziente.isEmpty()) {
            return;
        }
        String[] dati = ControllerPrenotazioni.getDatiPaziente(emailPaziente);
        if (dati == null) {
            JOptionPane.showMessageDialog(elencoPrenotazioniPanel,
                    "Paziente non trovato.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String messaggio = "Email: " + dati[0] + "\n"
                + "Nome: " + dati[1] + "\n"
                + "Cognome: " + dati[2] + "\n"
                + "Recapito telefonico: " + dati[3];
        JOptionPane.showMessageDialog(elencoPrenotazioniPanel, messaggio,
                "Dati paziente", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Crea e mostra la finestra del caso d'uso. Restituisce il {@link JFrame} creato.
     */
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
