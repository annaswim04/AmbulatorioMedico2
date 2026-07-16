package boundary;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * Implementazione COTS del sistema di notifiche via email con Jakarta Mail.
 * Pattern: ADAPTER + SINGLETON. Adatta la libreria esterna
 * Jakarta Mail all'interfaccia SistemaNotifiche.
 */
class SistemaNotificheJakarta implements SistemaNotifiche {

    private static SistemaNotificheJakarta instance;

    private SistemaNotificheJakarta() {
    }

    static SistemaNotificheJakarta getInstance() {
        if (instance == null) {
            instance = new SistemaNotificheJakarta();
        }
        return instance;
    }

    // --- Configurazione SMTP (Ethereal) ---
    private static final String MITTENTE  = "frederique.schimmel@ethereal.email";
    private static final String PASSWORD  = "HsZw2teEyMg6ZvARBQ";
    private static final String SMTP_HOST = "smtp.ethereal.email";
    private static final String SMTP_PORT = "587";

    private static final String OGGETTO_CONFERMA = "Conferma prenotazione visita";

    @Override
    public boolean inviaConfermaPrenotazione(String destinatario, String nomeMedico,
                                             String data, String fascia) {
        String corpo = "Gentile paziente,\n\n"
                + "la sua prenotazione è confermata.\n"
                + "Medico: " + nomeMedico + "\n"
                + "Data: " + data + "\n"
                + "Fascia oraria: " + fascia + "\n\n"
                + "Grazie per aver scelto il nostro ambulatorio.";
        return inviaEmail(destinatario, OGGETTO_CONFERMA, corpo);
    }

    /** Metodo generico di invio: unico punto che usa realmente Jakarta Mail. */
    private boolean inviaEmail(String destinatario, String oggetto, String corpo) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MITTENTE, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MITTENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(oggetto);
            message.setText(corpo);
            Transport.send(message);
            System.out.println("[SistemaNotifiche] Email inviata a: " + destinatario);
            return true;
        } catch (MessagingException e) {
            System.err.println("[SistemaNotifiche] Errore invio email: " + e.getMessage());
            return false;
        }
    }
}
