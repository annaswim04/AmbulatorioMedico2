package boundary;

import controller.ControllerPrenotazioni;

import javax.swing.*;

/**
 * Boundary del caso d'uso "Effettua prenotazione".
 * La selezione di specializzazione, medico, giorno e fascia oraria avviene in
 * "Visualizza disponibilità"; questa finestra riceve quei dati, mostra il
 * riepilogo della prenotazione e il paziente conferma
 * (→ registrazione + notifica di conferma) oppure annulla
 * (→ conferma di annullamento, nessuna registrazione).
 *
 * Non essendo implementato il login, il paziente si identifica inserendo la
 * propria email in questa finestra (semplificazione).
 */
public class FormEffettuaPrenotazione {

    private JPanel effettuaPrenotazionePanel;
    private JTextField campoEmailPaziente;
    private JTextArea areaRiepilogo;
    private JButton confermaButton;
    private JButton annullaButton;

    /** Dati della prenotazione da confermare, ricevuti da "Visualizza disponibilità". */
    private String emailMedico;
    private String nomeMedico;
    private String data;
    private String fascia;

    private JFrame frame;

    public FormEffettuaPrenotazione() {
        areaRiepilogo.setEditable(false);
        areaRiepilogo.setLineWrap(true);
        areaRiepilogo.setWrapStyleWord(true);

        confermaButton.addActionListener(e -> conferma());
        annullaButton.addActionListener(e -> annulla());
    }

    /**
     * Ramo "if paziente conferma la prenotazione": registra la prenotazione,
     * mostra il messaggio di conferma e invia la notifica di conferma via COTS.
     */
    private void conferma() {
        String emailPaziente = campoEmailPaziente.getText().trim();
        if (emailPaziente.isEmpty()) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Inserisci l'email del paziente per confermare la prenotazione.",
                    "Email mancante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int esito = ControllerPrenotazioni.effettuaPrenotazione(emailPaziente, emailMedico, data, fascia);

        switch (esito) {
            case ControllerPrenotazioni.SUCCESSO -> {
                // Notifica di conferma tramite COTS (Adapter), invocata dalla boundary
                SistemaNotifiche.getInstance().inviaConfermaPrenotazione(
                        emailPaziente, nomeMedico, data, fascia);
                JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                        "Prenotazione confermata per il " + data + " nella fascia " + fascia
                                + ".\nÈ stata inviata una email di conferma.",
                        "Prenotazione effettuata", JOptionPane.INFORMATION_MESSAGE);
                chiudi();
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
     * Ramo "else" del diagramma: il paziente annulla al riepilogo.
     * Mostra la conferma di annullamento; nessuna prenotazione viene registrata.
     */
    private void annulla() {
        JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                "Prenotazione annullata. Nessuna prenotazione è stata registrata.",
                "Prenotazione annullata", JOptionPane.INFORMATION_MESSAGE);
        chiudi();
    }

    private void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(effettuaPrenotazionePanel, messaggio,
                "Errore", JOptionPane.ERROR_MESSAGE);
    }

    private void chiudi() {
        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     * Crea e mostra la finestra del riepilogo con i dati scelti in
     * "Visualizza disponibilità".
     */
    public JFrame apriFormEffettuaPrenotazione(String specializzazione, String nomeMedico,
            String emailMedico, String data, String fascia) {
        this.nomeMedico = nomeMedico;
        this.emailMedico = emailMedico;
        this.data = data;
        this.fascia = fascia;

        // mostra il riepilogo della prenotazione
        areaRiepilogo.setText(
                "Riepilogo prenotazione\n"
                        + "-----------------------------\n"
                        + "Specializzazione: " + specializzazione + "\n"
                        + "Medico:           " + nomeMedico + "\n"
                        + "Data:             " + data + "\n"
                        + "Fascia oraria:    " + fascia + "\n\n"
                        + "Inserisci la tua email e conferma per registrare la prenotazione, "
                        + "oppure annulla.");

        frame = new JFrame("Effettua prenotazione");
        frame.setContentPane(effettuaPrenotazionePanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }



}
