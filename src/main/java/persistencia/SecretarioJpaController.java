/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import logica.Secretario;
import persistencia.exceptions.NonexistentEntityException;

/**
 *
 * @author Luisina
 */
public class SecretarioJpaController implements Serializable {

    public SecretarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
     public SecretarioJpaController() {
        emf = Persistence.createEntityManagerFactory("ConsultorioOdontologico_PU");
    }

    public void create(Secretario secretario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(secretario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Secretario secretario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            secretario = em.merge(secretario);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = secretario.getId();
                if (findSecretario(id) == null) {
                    throw new NonexistentEntityException("The secretario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Secretario secretario;
            try {
                secretario = em.getReference(Secretario.class, id);
                secretario.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The secretario with id " + id + " no longer exists.", enfe);
            }
            em.remove(secretario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Secretario> findSecretarioEntities() {
        return findSecretarioEntities(true, -1, -1);
    }

    public List<Secretario> findSecretarioEntities(int maxResults, int firstResult) {
        return findSecretarioEntities(false, maxResults, firstResult);
    }

    private List<Secretario> findSecretarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Secretario.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Secretario findSecretario(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Secretario.class, id);
        } finally {
            em.close();
        }
    }

    public int getSecretarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Secretario> rt = cq.from(Secretario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
