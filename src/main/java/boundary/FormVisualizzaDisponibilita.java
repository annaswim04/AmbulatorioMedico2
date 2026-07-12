package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.toedter.calendar.IDateEvaluator;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import controller.ControllerPrenotazioni;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Boundary del caso d'uso "Visualizza disponibilità".
 * L'utente sceglie una specializzazione, un medico associato e una data;
 * il sistema mostra le fasce orarie libere di quel medico.
 *
 * <p>La UI è definita in {@code FormVisualizzaDisponibilita.form}. La boundary
 * comunica solo con il {@link ControllerPrenotazioni} e riceve dati come
 * stringhe: non importa mai il package {@code entity}.</p>
 */
public class FormVisualizzaDisponibilita {

    private JPanel visualizzaDisponibilitaPanel;
    private JComboBox comboMedico;
    private JDateChooser selettoreData;
    private JButton mostraButton;
    private JList listaFasce;
    private JComboBox comboSpecializzazione;
    private JLabel labelEsito;
    private JButton prenotaButton;

    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    private final DefaultListModel<String> modelloFasce = new DefaultListModel<>();

    /**
     * Medici correnti della specializzazione selezionata: coppie {email, nome}.
     */
    private List<String[]> mediciCorrenti = new ArrayList<>();

    /**
     * Date (yyyy-MM-dd) in cui il medico selezionato ha almeno una fascia
     * libera: pilota {@link #dataEvaluator}, che disabilita le altre date nel
     * calendario.
     */
    private final Set<String> dateDisponibili = new HashSet<>();

    /** Disabilita nel calendario le date assenti da {@link #dateDisponibili}. */
    private final IDateEvaluator dataEvaluator = new IDateEvaluator() {
        @Override
        public boolean isSpecial(Date date) {
            return false;
        }

        @Override
        public Color getSpecialForegroundColor() {
            return null;
        }

        @Override
        public Color getSpecialBackroundColor() {
            return null;
        }

        @Override
        public String getSpecialTooltip() {
            return null;
        }

        @Override
        public boolean isInvalid(Date date) {
            return !dateDisponibili.contains(formato.format(date));
        }

        @Override
        public Color getInvalidForegroundColor() {
            return Color.LIGHT_GRAY;
        }

        @Override
        public Color getInvalidBackroundColor() {
            return null;
        }

        @Override
        public String getInvalidTooltip() {
            return null;
        }
    };

    public FormVisualizzaDisponibilita() {

        listaFasce.setModel(modelloFasce);

        for (String descrizione : ControllerPrenotazioni.getSpecializzazioni()) {
            comboSpecializzazione.addItem(descrizione);
        }
        comboSpecializzazione.setSelectedIndex(-1);

        selettoreData.getJCalendar().getDayChooser().addDateEvaluator(dataEvaluator);

        comboSpecializzazione.addActionListener(e -> aggiornaMedici());
        comboMedico.addActionListener(e -> aggiornaDateDisponibili());
        mostraButton.addActionListener(e -> mostraDisponibilita());
        prenotaButton.addActionListener(
                e -> new FormEffettuaPrenotazione().apriFormEffettuaPrenotazione());
    }

    private void aggiornaMedici() {
        comboMedico.removeAllItems();
        String specializzazione = (String) comboSpecializzazione.getSelectedItem();
        if (specializzazione == null) {
            mediciCorrenti = new ArrayList<>();
        } else {
            mediciCorrenti = ControllerPrenotazioni.getMedici(specializzazione);
            for (String[] medico : mediciCorrenti) {
                comboMedico.addItem(medico[1]);
            }
        }
        aggiornaDateDisponibili();
    }

    /**
     * Ricalcola le date disponibili del medico selezionato (UC: Medico
     * information expert di DisponibilitàMedico) e aggiorna il calendario,
     * disabilitando le date senza alcuna fascia libera.
     */
    private void aggiornaDateDisponibili() {
        dateDisponibili.clear();
        int indice = comboMedico.getSelectedIndex();
        if (indice >= 0 && indice < mediciCorrenti.size()) {
            String emailMedico = mediciCorrenti.get(indice)[0];
            dateDisponibili.addAll(Arrays.asList(ControllerPrenotazioni.getDateDisponibili(emailMedico)));
        }
        selettoreData.setDate(null);
        JCalendar jCalendar = selettoreData.getJCalendar();
        jCalendar.getDayChooser().setCalendar(jCalendar.getCalendar());
    }

    private void mostraDisponibilita() {
        modelloFasce.clear();
        labelEsito.setText(" ");

        int indice = comboMedico.getSelectedIndex();
        if (indice < 0 || indice >= mediciCorrenti.size() || selettoreData.getDate() == null) {
            JOptionPane.showMessageDialog(visualizzaDisponibilitaPanel, "Seleziona medico e data.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String emailMedico = mediciCorrenti.get(indice)[0];
        String data = formato.format(selettoreData.getDate());
        String[] fasce = ControllerPrenotazioni.visualizzaDisponibilita(emailMedico, data);

        if (fasce.length == 0) {
            labelEsito.setText("  Nessuna fascia disponibile per il " + data + ".");
            return;
        }
        for (String fascia : fasce) {
            modelloFasce.addElement(fascia);
        }
        labelEsito.setText("  " + fasce.length + " fasce libere il " + data + ".");
    }

    /**
     * Componente a creazione manuale richiesto dal form (custom-create).
     */
    private void createUIComponents() {
        selettoreData = new JDateChooser();
        // Coerente col limite di prenotazione: solo date da oggi + 48 ore in poi
        Date primaDataPrenotabile = Date.from(LocalDate.now().plusDays(2)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        selettoreData.setMinSelectableDate(primaDataPrenotabile);
    }

    /**
     * Crea e mostra la finestra del caso d'uso. Restituisce il {@link JFrame} creato.
     */
    public JFrame apriFormVisualizzaDisponibilita() {
        JFrame frame = new JFrame("Visualizza disponibilità");
        frame.setContentPane(visualizzaDisponibilitaPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }


    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return visualizzaDisponibilitaPanel;
    }
}
