package database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;

/**
 * Pattern: FACADE. Nasconde al livello entity i dettagli
 * di JPA/Hibernate (EntityManager, transazioni, JPQL).
 */
public class GestorePersistenza {

    /**
     * Salva nel database un oggetto persistente.
     *
     * Il parametro è di tipo Object perché il gestore della persistenza
     * deve rimanere generico: non deve conoscere direttamente le classi
     * specifiche del dominio.
     *
     * L'oggetto passato deve però essere una Entity, cioè una classe
     * annotata con @Entity.
     */
    public boolean salva(Object entita) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            /*
             * Ogni operazione che modifica il database deve essere eseguita
             * all'interno di una transazione.
             */
            em.getTransaction().begin();
            /*
             * persist rende l'oggetto gestito da Hibernate.
             * Al commit della transazione, Hibernate tradurrà l'oggetto
             * in una riga della tabella corrispondente.
             */
            em.persist(entita);
            /*
             * Conferma la transazione.
             * Da questo momento le modifiche diventano effettive nel database.
             */
            em.getTransaction().commit();
            return true;

        } catch (RuntimeException e) {
            /*
             * Se qualcosa va storto durante l'operazione, annulliamo
             * la transazione per evitare modifiche parziali al database.
             */
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            /*
             * L'EntityManager deve essere chiuso dopo l'operazione.
             * La EntityManagerFactory resta invece aperta e viene chiusa
             * solo alla fine dell'applicazione.
             */
            em.close();
        }
    }

    /** Restituisce tutte le istanze persistenti di una classe (SELECT *). */
    public <T> List<T> cercaTutti(Class<T> classe) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            String jpql = "SELECT e FROM " + classe.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, classe);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cerca tutti gli oggetti persistenti che soddisfano un insieme di condizioni.
     *
     * La query JPQL viene costruita nel livello database.
     */
    public <T> List<T> cercaPerCampi(Class<T> classe, Map<String, Object> campi) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT e FROM ")
                    .append(classe.getSimpleName()).append(" e");

            if (!campi.isEmpty()) {
                jpql.append(" WHERE ");
                int i = 0;
                for (String campo : campi.keySet()) {
                    if (i++ > 0) jpql.append(" AND ");
                    String param = campo.replace(".", "_");
                    jpql.append("e.").append(campo).append(" = :").append(param);
                }
            }

            TypedQuery<T> query = em.createQuery(jpql.toString(), classe);
            for (Map.Entry<String, Object> e : campi.entrySet()) {
                query.setParameter(e.getKey().replace(".", "_"), e.getValue());
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cerca un oggetto persistente a partire dalla sua classe e dal suo id.
     *
     * Il metodo è generico: può essere usato con qualunque Entity.
     */
    public <T> T trovaPerId(Class<T> classe, Object id) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            /*
             * find cerca nel database una riga della tabella associata
             * alla classe indicata, usando l'id come chiave primaria.
             */
            return em.find(classe, id);
        } finally {
            em.close();
        }
    }
}
