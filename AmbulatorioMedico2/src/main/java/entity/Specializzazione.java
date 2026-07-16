package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Specializzazione medica offerta dall'ambulatorio.
 * Possiede la relazione verso Medico.
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

    /** Information expert di Medico: restituisce i medici associati a questa specializzazione. */
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
