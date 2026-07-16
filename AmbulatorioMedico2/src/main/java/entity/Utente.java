package entity;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * Utente generico del sistema. Superclasse della gerarchia
 * Paziente / Medico / Amministratore.
 *
 * Mappata con strategia SINGLE_TABLE: un'unica tabella "Utente" con una colonna
 * discriminante "tipo_utente" che distingue il ruolo.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_utente")
public abstract class Utente {

    @Id
    private String email;

    private String nome;
    private String cognome;
    private String recapitoTelefonico;
    private String password;

    protected Utente() {
    }

    protected Utente(String email, String password, String nome, String cognome,
                     String recapitoTelefonico) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.recapitoTelefonico = recapitoTelefonico;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getRecapitoTelefonico() {
        return recapitoTelefonico;
    }

    public void setRecapitoTelefonico(String recapitoTelefonico) {
        this.recapitoTelefonico = recapitoTelefonico;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNomeCompleto() {
        return nome + " " + cognome;
    }
}
