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

/**
 * Boundary del caso d'uso "Visualizza disponibilità".
 * L'utente sceglie una specializzazione, un medico associato e una data;
 * il sistema mostra gli slot orari liberi di quel medico.
 */
public class FormVisualizzaDisponibilita extends JFrame {

    private final ControllerPrenotazioni controller = ControllerPrenotazioni.getInstance();
    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

    private final JComboBox<Specializzazione> comboSpecializzazione = new JComboBox<>();
    private final JComboBox<Medico> comboMedico = new JComboBox<>();
    private final JDateChooser selettoreData = new JDateChooser();
    private final DefaultListModel<String> modelloSlot = new DefaultListModel<>();
    private final JList<String> listaSlot = new JList<>(modelloSlot);
    private final JLabel etichettaEsito = new JLabel(" ");

    public FormVisualizzaDisponibilita() {
        super("Visualizza disponibilità");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(460, 420);
        setLocationRelativeTo(null);
        inizializza();
    }

    private void inizializza() {
        JPanel filtri = new JPanel(new GridLayout(4, 2, 6, 6));
        filtri.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        filtri.add(new JLabel("Specializzazione:"));
        filtri.add(comboSpecializzazione);
        filtri.add(new JLabel("Medico:"));
        filtri.add(comboMedico);
        filtri.add(new JLabel("Data:"));
        filtri.add(selettoreData);
        JButton bottoneCerca = new JButton("Mostra disponibilità");
        filtri.add(new JLabel());
        filtri.add(bottoneCerca);

        listaSlot.setBorder(BorderFactory.createTitledBorder("Slot liberi"));

        add(filtri, BorderLayout.NORTH);
        add(new JScrollPane(listaSlot), BorderLayout.CENTER);
        add(etichettaEsito, BorderLayout.SOUTH);

        for (Specializzazione s : controller.getSpecializzazioni()) {
            comboSpecializzazione.addItem(s);
        }
        comboSpecializzazione.setSelectedIndex(-1);
        comboMedico.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i,
                                                          boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                if (v instanceof Medico m) {
                    setText(m.getNomeCompleto());
                }
                return this;
            }
        });

        comboSpecializzazione.addActionListener(e -> aggiornaMedici());
        bottoneCerca.addActionListener(e -> mostraDisponibilita());
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

    private void mostraDisponibilita() {
        modelloSlot.clear();
        etichettaEsito.setText(" ");

        Medico medico = (Medico) comboMedico.getSelectedItem();
        if (medico == null || selettoreData.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleziona medico e data.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String data = formato.format(selettoreData.getDate());
        DisponibilitaMedico disp = controller.visualizzaDisponibilita(medico.getEmail(), data);

        if (disp == null || !disp.isDisponibile()) {
            etichettaEsito.setText("  Nessuno slot disponibile per il " + data + ".");
            return;
        }
        for (SlotOrario slot : disp.getFasceOrarieDisponibili()) {
            modelloSlot.addElement(slot.getOrario());
        }
        etichettaEsito.setText("  " + disp.getFasceOrarieDisponibili().size()
                + " slot liberi il " + data + ".");
    }
}
