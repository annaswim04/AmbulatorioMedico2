package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Specializzazione medica offerta dall'ambulatorio.
 *
 * Modellata come entità (non enum): l'elenco delle specializzazioni non è
 * chiuso a priori e può crescere nel tempo senza richiedere una ricompilazione.
 *
 * <p>Possiede la relazione verso {@link Medico} (GRASP: information expert di
 * "quali medici ho"). La relazione resta LAZY (default JPA): è compito di chi
 * la interroga ({@link RegistroSpecializzazioni}) caricare manualmente i medici
 * con una query separata e ricollegarli con {@link #setMedici}, così come fa
 * {@code RegistroRimessaggio.cercaProprietarioPerIdProprietario_full} per
 * {@code Proprietario}/{@code Imbarcazione}.</p>
 */
@Entity
public class Specializzazione {

    @Id
    private String nome;

    @OneToMany(mappedBy = "specializzazione")
    private List<Medico> medici = new ArrayList<>();

    protected Specializzazione() {
    }

    public Specializzazione(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /** Medici associati a questa specializzazione. */
    public List<Medico> getMedici() {
        return medici;
    }

    public void setMedici(List<Medico> medici) {
        this.medici = medici;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Specializzazione that)) return false;
        return Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nome);
    }
}
