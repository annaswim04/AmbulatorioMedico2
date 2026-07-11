package boundary;
//ciao
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.toedter.calendar.JDateChooser;
import controller.ControllerPrenotazioni;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Boundary del caso d'uso "Effettua prenotazione".
 * Il paziente sceglie specializzazione, medico, data e fascia oraria libera e
 * conferma. A prenotazione avvenuta, la notifica di conferma è inviata tramite
 * il {@link SistemaNotifiche} (COTS), coerentemente col pattern BCED.
 *
 * <p>La UI è definita in {@code FormEffettuaPrenotazione.form}. La boundary
 * comunica solo con il {@link ControllerPrenotazioni} e riceve dati come
 * stringhe: non importa mai il package {@code entity}.</p>
 */
public class FormEffettuaPrenotazione {

    private JPanel effettuaPrenotazionePanel;
    private JTextField campoEmailPaziente;
    private JComboBox<String> comboSpecializzazione;
    private JComboBox<String> comboMedico;
    private JDateChooser selettoreData;
    private JComboBox<String> comboFascia;
    private JButton prenotaButton;

    private final ControllerPrenotazioni controller = ControllerPrenotazioni.getInstance();
    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Medici correnti della specializzazione selezionata: coppie {email, nome}.
     */
    private List<String[]> mediciCorrenti = new ArrayList<>();

    public FormEffettuaPrenotazione() {
        $$$setupUI$$$();
        for (String descrizione : controller.getSpecializzazioni()) {
            comboSpecializzazione.addItem(descrizione);
        }
        comboSpecializzazione.setSelectedIndex(-1);

        comboSpecializzazione.addActionListener(e -> aggiornaMedici());
        comboMedico.addActionListener(e -> aggiornaFasce());
        selettoreData.getDateEditor().addPropertyChangeListener("date", e -> aggiornaFasce());
        prenotaButton.addActionListener(e -> prenota());
    }

    private void aggiornaMedici() {
        comboMedico.removeAllItems();
        String specializzazione = (String) comboSpecializzazione.getSelectedItem();
        if (specializzazione == null) {
            mediciCorrenti = new ArrayList<>();
            return;
        }
        mediciCorrenti = controller.getMedici(specializzazione);
        for (String[] medico : mediciCorrenti) {
            comboMedico.addItem(medico[1]);
        }
    }

    private void aggiornaFasce() {
        comboFascia.removeAllItems();
        String emailMedico = emailMedicoSelezionato();
        if (emailMedico == null || selettoreData.getDate() == null) {
            return;
        }
        String data = formato.format(selettoreData.getDate());
        for (String fascia : controller.visualizzaDisponibilita(emailMedico, data)) {
            comboFascia.addItem(fascia);
        }
    }

    private void prenota() {
        String emailPaziente = campoEmailPaziente.getText().trim();
        String emailMedico = emailMedicoSelezionato();
        String fascia = (String) comboFascia.getSelectedItem();

        if (emailPaziente.isEmpty() || emailMedico == null || selettoreData.getDate() == null
                || fascia == null) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Compila tutti i campi e seleziona una fascia oraria disponibile.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String data = formato.format(selettoreData.getDate());
        String nomeMedico = mediciCorrenti.get(comboMedico.getSelectedIndex())[1];

        int esito = controller.effettuaPrenotazione(emailPaziente, emailMedico, data, fascia);

        switch (esito) {
            case ControllerPrenotazioni.SUCCESSO -> {
                // Notifica di conferma tramite COTS (Adapter), invocata dalla boundary
                SistemaNotifiche.getInstance().inviaConfermaPrenotazione(
                        emailPaziente, nomeMedico, data, fascia);
                JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                        "Prenotazione confermata per il " + data + " nella fascia " + fascia
                                + ".\nÈ stata inviata una email di conferma.",
                        "Prenotazione effettuata", JOptionPane.INFORMATION_MESSAGE);
                aggiornaFasce();
            }
            case ControllerPrenotazioni.PAZIENTE_NON_ESISTENTE -> mostraErrore(
                    "Nessun paziente registrato con questa email.");
            case ControllerPrenotazioni.MEDICO_NON_ESISTENTE -> mostraErrore(
                    "Medico non trovato.");
            case ControllerPrenotazioni.FASCIA_NON_DISPONIBILE -> mostraErrore(
                    "La fascia oraria selezionata non è più disponibile.");
            default -> mostraErrore("Errore durante il salvataggio della prenotazione.");
        }
    }

    /**
     * Email del medico selezionato in combo, o {@code null} se nessuno.
     */
    private String emailMedicoSelezionato() {
        int indice = comboMedico.getSelectedIndex();
        if (indice < 0 || indice >= mediciCorrenti.size()) {
            return null;
        }
        return mediciCorrenti.get(indice)[0];
    }

    private void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(effettuaPrenotazionePanel, messaggio,
                "Errore", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Componente a creazione manuale richiesto dal form (custom-create).
     */
    private void createUIComponents() {
        selettoreData = new JDateChooser();
    }

    /**
     * Crea e mostra la finestra del caso d'uso. Restituisce il {@link JFrame} creato.
     */
    public JFrame apriFormEffettuaPrenotazione() {
        JFrame frame = new JFrame("Effettua prenotazione");
        frame.setContentPane(effettuaPrenotazionePanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        effettuaPrenotazionePanel = new JPanel();
        effettuaPrenotazionePanel.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Email Paziente:");
        effettuaPrenotazionePanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        campoEmailPaziente = new JTextField();
        effettuaPrenotazionePanel.add(campoEmailPaziente, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Specializzazione:");
        effettuaPrenotazionePanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboSpecializzazione = new JComboBox();
        effettuaPrenotazionePanel.add(comboSpecializzazione, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Medico:");
        effettuaPrenotazionePanel.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboMedico = new JComboBox();
        effettuaPrenotazionePanel.add(comboMedico, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Data:");
        effettuaPrenotazionePanel.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Fascia oraria:");
        effettuaPrenotazionePanel.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboFascia = new JComboBox();
        effettuaPrenotazionePanel.add(comboFascia, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        effettuaPrenotazionePanel.add(selettoreData, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        prenotaButton = new JButton();
        prenotaButton.setText("Prenota");
        effettuaPrenotazionePanel.add(prenotaButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return effettuaPrenotazionePanel;
    }

}
