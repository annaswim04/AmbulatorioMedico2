package boundary;

import com.toedter.calendar.JDateChooser;
import controller.ControllerPrenotazioni;
import entity.DisponibilitaMedico;
import entity.Medico;
import entity.SlotOrario;
import entity.Specializzazione;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Boundary del caso d'uso "Effettua prenotazione".
 * Il paziente sceglie specializzazione, medico, data e orario libero e conferma.
 * A prenotazione avvenuta, la notifica di conferma è inviata tramite il
 * {@link SistemaNotifiche} (COTS), coerentemente col pattern BCED.
 */
public class FormEffettuaPrenotazione extends JFrame {

    private final ControllerPrenotazioni controller = ControllerPrenotazioni.getInstance();
    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

    private final JTextField campoEmailPaziente = new JTextField(20);
    private final JComboBox<Specializzazione> comboSpecializzazione = new JComboBox<>();
    private final JComboBox<Medico> comboMedico = new JComboBox<>();
    private final JDateChooser selettoreData = new JDateChooser();
    private final JComboBox<SlotOrario> comboOrario = new JComboBox<>();

    public FormEffettuaPrenotazione() {
        super("Effettua prenotazione");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(460, 320);
        setLocationRelativeTo(null);
        inizializza();
    }

    private void inizializza() {
        JPanel pannello = new JPanel(new GridBagLayout());
        pannello.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int r = 0;
        aggiungiRiga(pannello, gbc, r++, "Email paziente:", campoEmailPaziente);
        aggiungiRiga(pannello, gbc, r++, "Specializzazione:", comboSpecializzazione);
        aggiungiRiga(pannello, gbc, r++, "Medico:", comboMedico);
        aggiungiRiga(pannello, gbc, r++, "Data:", selettoreData);
        aggiungiRiga(pannello, gbc, r++, "Orario:", comboOrario);

        JButton bottonePrenota = new JButton("Prenota");
        gbc.gridx = 1;
        gbc.gridy = r;
        pannello.add(bottonePrenota, gbc);

        add(pannello);

        // Popola le specializzazioni
        for (Specializzazione s : controller.getSpecializzazioni()) {
            comboSpecializzazione.addItem(s);
        }
        comboSpecializzazione.setSelectedIndex(-1);

        // Listener: aggiornano le combo a cascata
        comboSpecializzazione.addActionListener(e -> aggiornaMedici());
        comboMedico.addActionListener(e -> aggiornaOrari());
        selettoreData.getDateEditor().addPropertyChangeListener("date", e -> aggiornaOrari());
        bottonePrenota.addActionListener(e -> prenota());

        // Rendering leggibile delle combo
        comboMedico.setRenderer(new MedicoRenderer());
    }

    private void aggiungiRiga(JPanel p, GridBagConstraints gbc, int riga,
                              String etichetta, JComponent campo) {
        gbc.gridx = 0;
        gbc.gridy = riga;
        gbc.weightx = 0;
        p.add(new JLabel(etichetta), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        p.add(campo, gbc);
    }

    private void aggiornaMedici() {
        comboMedico.removeAllItems();
        Specializzazione s = (Specializzazione) comboSpecializzazione.getSelectedItem();
        if (s == null) {
            return;
        }
        for (Medico m : controller.getMedici(s)) {
            comboMedico.addItem(m);
        }
    }

    private void aggiornaOrari() {
        comboOrario.removeAllItems();
        Medico medico = (Medico) comboMedico.getSelectedItem();
        if (medico == null || selettoreData.getDate() == null) {
            return;
        }
        String data = formato.format(selettoreData.getDate());
        DisponibilitaMedico disp = controller.visualizzaDisponibilita(medico.getEmail(), data);
        if (disp == null) {
            return;
        }
        List<SlotOrario> liberi = disp.getFasceOrarieDisponibili();
        for (SlotOrario slot : liberi) {
            comboOrario.addItem(slot);
        }
    }

    private void prenota() {
        Medico medico = (Medico) comboMedico.getSelectedItem();
        SlotOrario slot = (SlotOrario) comboOrario.getSelectedItem();
        String emailPaziente = campoEmailPaziente.getText().trim();

        if (emailPaziente.isEmpty() || medico == null || selettoreData.getDate() == null
                || slot == null) {
            JOptionPane.showMessageDialog(this,
                    "Compila tutti i campi e seleziona uno slot disponibile.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String data = formato.format(selettoreData.getDate());
        String orario = slot.getOrario();

        int esito = controller.effettuaPrenotazione(emailPaziente, medico.getEmail(), data, orario);

        switch (esito) {
            case ControllerPrenotazioni.SUCCESSO -> {
                // Notifica di conferma tramite COTS (Adapter), invocata dalla boundary
                SistemaNotifiche.getInstance().inviaConfermaPrenotazione(
                        emailPaziente, medico.getNomeCompleto(), data, orario);
                JOptionPane.showMessageDialog(this,
                        "Prenotazione confermata per il " + data + " alle " + orario
                                + ".\nÈ stata inviata una email di conferma.",
                        "Prenotazione effettuata", JOptionPane.INFORMATION_MESSAGE);
                aggiornaOrari();
            }
            case ControllerPrenotazioni.PAZIENTE_NON_ESISTENTE -> mostraErrore(
                    "Nessun paziente registrato con questa email.");
            case ControllerPrenotazioni.MEDICO_NON_ESISTENTE -> mostraErrore(
                    "Medico non trovato.");
            case ControllerPrenotazioni.SLOT_NON_DISPONIBILE -> mostraErrore(
                    "Lo slot selezionato non è più disponibile.");
            default -> mostraErrore("Errore durante il salvataggio della prenotazione.");
        }
    }

    private void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    /** Mostra nome+specializzazione del medico nelle combo. */
    private static class MedicoRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Medico m) {
                setText(m.getNomeCompleto() + " (" + m.getSpecializzazione().getDescrizione() + ")");
            }
            return this;
        }
    }
}
