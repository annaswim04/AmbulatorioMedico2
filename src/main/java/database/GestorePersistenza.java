package database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;

/**
 * Facade generica sul livello di persistenza JPA.
 *
 * Pattern: FACADE. Nasconde ai {@code Registro*} del livello entity i dettagli
 * di JPA/Hibernate (EntityManager, transazioni, JPQL), esponendo operazioni
 * CRUD generiche valide per qualsiasi @Entity. È l'UNICA porta verso il DB
 * usata dal dominio.
 */
public class GestorePersistenza {

    /** Salva (persist) un'entità. Restituisce true se l'operazione riesce. */
    public boolean salva(Object entita) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entita);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
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

    /** Cerca tutte le entità che soddisfano l'uguaglianza su un insieme di campi. */
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

    /** Trova un'entità per chiave primaria. */
    public <T> T trovaPerId(Class<T> classe, Object id) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            return em.find(classe, id);
        } finally {
            em.close();
        }
    }

    /** Aggiorna specifici campi di un'entità identificata dall'id. */
    public <T> boolean aggiornaCampi(Class<T> classe, Object id, Map<String, Object> campi) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            StringBuilder jpql = new StringBuilder("UPDATE ")
                    .append(classe.getSimpleName()).append(" e SET ");
            int i = 0;
            for (String campo : campi.keySet()) {
                if (i++ > 0) jpql.append(", ");
                String param = campo.replace(".", "_");
                jpql.append("e.").append(campo).append(" = :").append(param);
            }
            jpql.append(" WHERE e.id = :id");

            Query query = em.createQuery(jpql.toString());
            for (Map.Entry<String, Object> e : campi.entrySet()) {
                query.setParameter(e.getKey().replace(".", "_"), e.getValue());
            }
            query.setParameter("id", id);

            int righe = query.executeUpdate();
            em.getTransaction().commit();
            return righe > 0;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /** Elimina un'entità per classe e id. */
    public <T> boolean elimina(Class<T> classe, Object id) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            T oggetto = em.find(classe, id);
            if (oggetto != null) {
                em.remove(oggetto);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().commit();
            return false;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}
