package entity;

/**
 * Composite di livello più alto: l'intera giornata lavorativa dell'ambulatorio,
 * composta dai tre periodi (Mattina, Primo pomeriggio, Tardo pomeriggio).
 *
 * Rappresenta il "template" orario standard, usato per calcolare le disponibilità
 * e l'occupazione delle fasce (monitoraggio).
 */
public class Giornata extends PeriodoDiGiornata {

    public Giornata() {
        aggiungi(new Mattina());
        aggiungi(new PrimoPomeriggio());
        aggiungi(new TardoPomeriggio());
    }

    @Override
    public String getNome() {
        return "Giornata";
    }

    /** Periodi di cui si compone la giornata (per iterare sulle fasce principali). */
    public java.util.List<PeriodoDiGiornata> getPeriodi() {
        java.util.List<PeriodoDiGiornata> periodi = new java.util.ArrayList<>();
        for (FasciaOraria f : getComponenti()) {
            if (f instanceof PeriodoDiGiornata p) {
                periodi.add(p);
            }
        }
        return periodi;
    }
}
