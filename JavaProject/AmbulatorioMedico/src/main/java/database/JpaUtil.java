package database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Punto di accesso unico alla persistenza JPA/Hibernate.
 *
 * Pattern: SINGLETON. Esiste una sola {@link EntityManagerFactory} per tutta
 * l'applicazione (crearla è costoso); da essa si ricavano gli EntityManager.
 */
public class JpaUtil {

    private static JpaUtil instance;

    private final EntityManagerFactory emf;

    private JpaUtil() {
        // Il nome deve coincidere con la persistence-unit in persistence.xml
        this.emf = Persistence.createEntityManagerFactory("ambulatoriomedico");
    }

    public static JpaUtil getInstance() {
        if (instance == null) {
            instance = new JpaUtil();
        }
        return instance;
    }

    /** Restituisce un nuovo EntityManager (da chiudere dopo l'uso). */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /** Da chiamare alla chiusura dell'applicazione. */
    public void chiudi() {
        emf.close();
    }
}
