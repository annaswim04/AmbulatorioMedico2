package boundary;

import controller.ControllerPrenotazioni;
import entity.Prenotazione;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Boundary del caso d'uso "Visualizza elenco prenotazioni (medico)".
 * Il medico autenticato consulta l'elenco delle visite prenotate presso il
 * proprio ambulatorio.
 */
public class FormElencoPrenotazioniMedico extends JFrame {

    private final ControllerPrenotazioni controller = ControllerPrenotazioni.getInstance();

    private final JTextField campoEmailMedico = new JTextField(20);
    private final DefaultTableModel modello = new DefaultTableModel(
            new Object[]{"Data", "Orario", "Paziente", "Recapito", "Stato"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    public FormElencoPrenotazioniMedico() {
        super("Elenco prenotazioni - Medico");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(640, 400);
        setLocationRelativeTo(null);
        inizializza();
    }

    private void inizializza() {
        JPanel alto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        alto.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        alto.add(new JLabel("Email medico:"));
        alto.add(campoEmailMedico);
        JButton bottone = new JButton("Mostra prenotazioni");
        alto.add(bottone);

        JTable tabella = new JTable(modello);

        add(alto, BorderLayout.NORTH);
        add(new JScrollPane(tabella), BorderLayout.CENTER);

        bottone.addActionListener(e -> caricaPrenotazioni());
    }

    private void caricaPrenotazioni() {
        modello.setRowCount(0);
        String email = campoEmailMedico.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci l'email del medico.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Prenotazione> prenotazioni = controller.visualizzaElencoPrenotazioni(email);
        if (prenotazioni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessuna prenotazione trovata per questo medico.",
                    "Elenco vuoto", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (Prenotazione p : prenotazioni) {
            modello.addRow(new Object[]{
                    p.getData(),
                    p.getOrario(),
                    p.getPaziente() != null ? p.getPaziente().getNomeCompleto() : "-",
                    p.getPaziente() != null ? p.getPaziente().getRecapitoTelefonico() : "-",
                    p.getStato().descrizione()
            });
        }
    }
}
