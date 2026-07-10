package entity;

/**
 * Periodo "Mattina" (Composite concreto): slot dalle 09:00 alle 11:30.
 */
public class Mattina extends PeriodoDiGiornata {

    public Mattina() {
        aggiungiOrari("09:00", "09:30", "10:00", "10:30", "11:00", "11:30");
    }

    @Override
    public String getNome() {
        return "Mattina";
    }
}
