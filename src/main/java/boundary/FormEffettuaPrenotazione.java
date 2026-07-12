package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.toedter.calendar.JDateChooser;
import controller.ControllerPrenotazioni;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Boundary del caso d'uso "Effettua prenotazione".
 * Il paziente sceglie specializzazione, medico, data e fascia oraria libera e
 * conferma.
 */
public class FormEffettuaPrenotazione {

    private JPanel effettuaPrenotazionePanel;
    private JTextField campoEmailPaziente;
    private JComboBox<String> comboSpecializzazione;
    private JComboBox<String> comboMedico;
    private JDateChooser selettoreData;
    private JComboBox<String> comboFascia;
    private JButton prenotaButton;

    /** Anticipo minimo per prenotare: 48 ore (2 giorni), per rispettare il limite di annullamento. */
    private static final int GIORNI_MINIMI_ANTICIPO = 2;

    private final ControllerPrenotazioni controller = ControllerPrenotazioni.getInstance();
    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Medici correnti della specializzazione selezionata: coppie {email, nome}.
     */
    private List<String[]> mediciCorrenti = new ArrayList<>();

    public FormEffettuaPrenotazione() {


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

        if (anticipoInsufficiente(selettoreData.getDate())) {
            mostraErrore("Le prenotazioni vanno effettuate con almeno 48 ore di anticipo.");
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
        // Si può prenotare solo con almeno 48 ore di anticipo: blocca le date prima di oggi+2gg
        Date primaDataPrenotabile = Date.from(LocalDate.now().plusDays(GIORNI_MINIMI_ANTICIPO)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        selettoreData.setMinSelectableDate(primaDataPrenotabile);
    }

    private boolean anticipoInsufficiente(Date data) {
        LocalDate scelta = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return scelta.isBefore(LocalDate.now().plusDays(GIORNI_MINIMI_ANTICIPO));
    }

    /**
     * Crea e mostra la finestra del caso d'uso.
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


    public JComponent $$$getRootComponent$$$() {
        return effettuaPrenotazionePanel;
    }
}
