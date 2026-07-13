package boundary;

import com.toedter.calendar.JDateChooser;
import controller.ControllerPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Boundary del caso d'uso "Monitoraggio ambulatorio" (amministratore).
 * Mostra, per un intervallo di tempo: numero di visite prenotate, numero di
 * annullamenti, distribuzione per specializzazione e occupazione delle fasce orarie.
 *
 * <p>La UI è definita in {@code FormMonitoraggio.form}. La boundary comunica solo
 * con il {@link ControllerPrenotazioni} e riceve il riepilogo come mappa di
 * stringhe: non importa mai il package {@code entity}.</p>
 */
public class FormMonitoraggio {

    private JPanel monitoraggioPanel;
    private JDateChooser dataInizio;
    private JDateChooser dataFine;
    private JButton generaButton;
    private JLabel etichettaTotali;
    private JTable tabellaSpec;
    private JTable tabellaFasce;

    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

    private final DefaultTableModel modelloSpec = new DefaultTableModel(
            new Object[]{"Specializzazione", "Prenotazioni"}, 0);
    private final DefaultTableModel modelloFasce = new DefaultTableModel(
            new Object[]{"Fascia oraria", "Occupazione"}, 0);

    public FormMonitoraggio() {

        tabellaSpec.setModel(modelloSpec);
        tabellaSpec.getTableHeader().setReorderingAllowed(false);
        tabellaFasce.setModel(modelloFasce);
        tabellaFasce.getTableHeader().setReorderingAllowed(false);

        generaButton.addActionListener(e -> generaReport());
    }

    private void generaReport() {
        if (dataInizio.getDate() == null || dataFine.getDate() == null) {
            JOptionPane.showMessageDialog(monitoraggioPanel, "Seleziona l'intervallo di date.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String inizio = formato.format(dataInizio.getDate());
        String fine = formato.format(dataFine.getDate());
        if (inizio.compareTo(fine) > 0) {
            JOptionPane.showMessageDialog(monitoraggioPanel, "La data iniziale è successiva a quella finale.",
                    "Intervallo non valido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, List<String[]>> riepilogo =
                ControllerPrenotazioni.visualizzaMonitoraggioAmbulatorio(inizio, fine);

        String[] totali = riepilogo.get("totali").get(0);
        etichettaTotali.setText(String.format(
                "Visite prenotate: %s    |    Annullamenti: %s    (%s → %s)",
                totali[0], totali[1], inizio, fine));

        modelloSpec.setRowCount(0);
        for (String[] riga : riepilogo.get("specializzazioni")) {
            modelloSpec.addRow(riga);
        }
        if (modelloSpec.getRowCount() == 0) {
            modelloSpec.addRow(new Object[]{"(nessuna prenotazione)", "0"});
        }

        modelloFasce.setRowCount(0);
        for (String[] riga : riepilogo.get("fasce")) {
            modelloFasce.addRow(riga);
        }
    }

    /**
     * Componenti a creazione manuale richiesti dal form (custom-create).
     */
    private void createUIComponents() {
        dataInizio = new JDateChooser();
        dataFine = new JDateChooser();
    }

    /**
     * Crea e mostra la finestra del caso d'uso. Restituisce il {@link JFrame} creato.
     */
    public JFrame apriFormMonitoraggio() {
        JFrame frame = new JFrame("Monitoraggio ambulatorio");
        frame.setContentPane(monitoraggioPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

}
