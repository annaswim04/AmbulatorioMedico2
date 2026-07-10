package entity;

/**
 * Periodo "Primo pomeriggio" (Composite concreto): slot dalle 14:00 alle 15:30.
 */
public class PrimoPomeriggio extends PeriodoDiGiornata {

    public PrimoPomeriggio() {
        aggiungiOrari("14:00", "14:30", "15:00", "15:30");
    }

    @Override
    public String getNome() {
        return "Primo pomeriggio";
    }
}
