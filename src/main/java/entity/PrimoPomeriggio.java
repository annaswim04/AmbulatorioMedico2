package entity;

/**
 * Fascia oraria del primo pomeriggio (sottoclasse di {@link FasciaOraria}).
 */
public class PrimoPomeriggio extends FasciaOraria {

    @Override
    public String getNome() {
        return "Primo pomeriggio";
    }
}
