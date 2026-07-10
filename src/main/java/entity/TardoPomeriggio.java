package entity;

/**
 * Periodo "Tardo pomeriggio" (Composite concreto): slot dalle 16:00 alle 17:30.
 */
public class TardoPomeriggio extends PeriodoDiGiornata {

    public TardoPomeriggio() {
        aggiungiOrari("16:00", "16:30", "17:00", "17:30");
    }

    @Override
    public String getNome() {
        return "Tardo pomeriggio";
    }
}
