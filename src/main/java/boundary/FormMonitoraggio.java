package boundary;

import com.toedter.calendar.JDateChooser;
import controller.ControllerPrenotazioni;
import entity.RisultatoMonitoraggio;
import entity.Specializzazione;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Boundary del caso d'uso "Monitoraggio ambulatorio" (amministratore).
 * Mostra, per un intervallo di tempo: numero di visite prenotate, numero di
 * annullamenti, distribuzione per specializzazione e occupazione delle fasce orarie.
 */
public class FormMonitoraggio extends JFrame {

    private final ControllerPrenotazioni controller = ControllerPrenotazioni.getInstance();
    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

    private final JDateChooser dataInizio = new JDateChooser();
    private final JDateChooser dataFine = new JDateChooser();
    private final JLabel etichettaTotali = new JLabel(" ");

    private final DefaultTableModel modelloSpec = new DefaultTableModel(
            new Object[]{"Specializzazione", "Prenotazioni"}, 0);
    private final DefaultTableModel modelloFasce = new DefaultTableModel(
            new Object[]{"Fascia oraria", "Occupazione"}, 0);

    public FormMonitoraggio() {
        super("Monitoraggio ambulatorio");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 480);
        setLocationRelativeTo(null);
        inizializza();
    }

    private void inizializza() {
        JPanel filtri = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtri.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        filtri.add(new JLabel("Dal:"));
        filtri.add(dataInizio);
        filtri.add(new JLabel("Al:"));
        filtri.add(dataFine);
        JButton bottone = new JButton("Genera report");
        filtri.add(bottone);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        etichettaTotali.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        etichettaTotali.setFont(etichettaTotali.getFont().deriveFont(Font.BOLD, 14f));
        centro.add(etichettaTotali);

        JTable tabellaSpec = new JTable(modelloSpec);
        tabellaSpec.getTableHeader().setReorderingAllowed(false);
        JScrollPane spSpec = new JScrollPane(tabellaSpec);
        spSpec.setBorder(BorderFactory.createTitledBorder("Prenotazioni per specializzazione"));

        JTable tabellaFasce = new JTable(modelloFasce);
        tabellaFasce.getTableHeader().setReorderingAllowed(false);
        JScrollPane spFasce = new JScrollPane(tabellaFasce);
        spFasce.setBorder(BorderFactory.createTitledBorder("Occupazione fasce orarie"));

        centro.add(spSpec);
        centro.add(spFasce);

        add(filtri, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        bottone.addActionListener(e -> generaReport());
    }

    private void generaReport() {
        if (dataInizio.getDate() == null || dataFine.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleziona l'intervallo di date.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String inizio = formato.format(dataInizio.getDate());
        String fine = formato.format(dataFine.getDate());
        if (inizio.compareTo(fine) > 0) {
            JOptionPane.showMessageDialog(this, "La data iniziale è successiva a quella finale.",
                    "Intervallo non valido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RisultatoMonitoraggio r = controller.visualizzaMonitoraggioAmbulatorio(inizio, fine);

        etichettaTotali.setText(String.format(
                "Visite prenotate: %d    |    Annullamenti: %d    (%s → %s)",
                r.getNumeroPrenotazioni(), r.getNumeroAnnullamenti(), inizio, fine));

        modelloSpec.setRowCount(0);
        for (Map.Entry<Specializzazione, Integer> e : r.getPrenotazioniPerSpecializzazione().entrySet()) {
            modelloSpec.addRow(new Object[]{e.getKey().getDescrizione(), e.getValue()});
        }
        if (modelloSpec.getRowCount() == 0) {
            modelloSpec.addRow(new Object[]{"(nessuna prenotazione)", 0});
        }

        modelloFasce.setRowCount(0);
        for (Map.Entry<String, Integer> e : r.getOccupazioneFasce().entrySet()) {
            modelloFasce.addRow(new Object[]{e.getKey(), e.getValue()});
        }
    }
}
