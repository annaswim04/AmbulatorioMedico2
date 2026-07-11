package boundary;

import com.toedter.calendar.JDateChooser;
import controller.ControllerPrenotazioni;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
    private JComboBox<String> comboSpecializzazione;
    private JComboBox<String> comboMedico;
    private JDateChooser selettoreData;
    private JButton mostraButton;
    private JList<String> listaFasce;
    private JLabel etichettaEsito;

    private final ControllerPrenotazioni controller = ControllerPrenotazioni.getInstance();
    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    private final DefaultListModel<String> modelloFasce = new DefaultListModel<>();

    /** Medici correnti della specializzazione selezionata: coppie {email, nome}. */
    private List<String[]> mediciCorrenti = new ArrayList<>();

    public FormVisualizzaDisponibilita() {
        listaFasce.setModel(modelloFasce);

        for (String descrizione : controller.getSpecializzazioni()) {
            comboSpecializzazione.addItem(descrizione);
        }
        comboSpecializzazione.setSelectedIndex(-1);

        comboSpecializzazione.addActionListener(e -> aggiornaMedici());
        mostraButton.addActionListener(e -> mostraDisponibilita());
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

    private void mostraDisponibilita() {
        modelloFasce.clear();
        etichettaEsito.setText(" ");

        int indice = comboMedico.getSelectedIndex();
        if (indice < 0 || indice >= mediciCorrenti.size() || selettoreData.getDate() == null) {
            JOptionPane.showMessageDialog(visualizzaDisponibilitaPanel, "Seleziona medico e data.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String emailMedico = mediciCorrenti.get(indice)[0];
        String data = formato.format(selettoreData.getDate());
        String[] fasce = controller.visualizzaDisponibilita(emailMedico, data);

        if (fasce.length == 0) {
            etichettaEsito.setText("  Nessuna fascia disponibile per il " + data + ".");
            return;
        }
        for (String fascia : fasce) {
            modelloFasce.addElement(fascia);
        }
        etichettaEsito.setText("  " + fasce.length + " fasce libere il " + data + ".");
    }

    /** Componente a creazione manuale richiesto dal form (custom-create). */
    private void createUIComponents() {
        selettoreData = new JDateChooser();
    }

    /** Crea e mostra la finestra del caso d'uso. Restituisce il {@link JFrame} creato. */
    public JFrame apriFormVisualizzaDisponibilita() {
        JFrame frame = new JFrame("Visualizza disponibilità");
        frame.setContentPane(visualizzaDisponibilitaPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }
}
