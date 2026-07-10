package entity;

import java.util.List;
import java.util.Objects;

/**
 * Pattern Composite (LEAF). Singolo orario prenotabile, es. "09:00".
 */
public class SlotOrario extends FasciaOraria {

    private final String orario;

    public SlotOrario(String orario) {
        this.orario = orario;
    }

    public String getOrario() {
        return orario;
    }

    @Override
    public String getNome() {
        return orario;
    }

    @Override
    public List<SlotOrario> getSlot() {
        return List.of(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlotOrario that)) return false;
        return Objects.equals(orario, that.orario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orario);
    }

    @Override
    public String toString() {
        return orario;
    }
}
