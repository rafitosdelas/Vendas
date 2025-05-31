/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projeto.controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import projeto.controller.exceptions.NonexistentEntityException;
import projeto.entities.ItensVenda;

/**
 *
 * @author docar
 */
public class ItensVendaJpaController implements Serializable {

    public ItensVendaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    // Dentro da sua classe ItensVendaJpaController.java

    public List<ItensVenda> findItensVendaByIdVenda(Integer idVendaDaBusca) { // Mudei o nome do parâmetro para clareza
        EntityManager em = getEntityManager();
        try {
            // A query agora compara o campo 'idVenda' da entidade ItensVenda
            // diretamente com o parâmetro recebido.
            javax.persistence.TypedQuery<ItensVenda> query = em.createQuery(
                "SELECT i FROM ItensVenda i WHERE i.idVenda = :idVendaParam", ItensVenda.class);
            query.setParameter("idVendaParam", idVendaDaBusca); // Usa o parâmetro do método
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ItensVenda itensVenda) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(itensVenda);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ItensVenda itensVenda) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            itensVenda = em.merge(itensVenda);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = itensVenda.getIdItemVenda();
                if (findItensVenda(id) == null) {
                    throw new NonexistentEntityException("The itensVenda with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ItensVenda itensVenda;
            try {
                itensVenda = em.getReference(ItensVenda.class, id);
                itensVenda.getIdItemVenda();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The itensVenda with id " + id + " no longer exists.", enfe);
            }
            em.remove(itensVenda);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ItensVenda> findItensVendaEntities() {
        return findItensVendaEntities(true, -1, -1);
    }

    public List<ItensVenda> findItensVendaEntities(int maxResults, int firstResult) {
        return findItensVendaEntities(false, maxResults, firstResult);
    }

    private List<ItensVenda> findItensVendaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ItensVenda.class));
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

    public ItensVenda findItensVenda(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ItensVenda.class, id);
        } finally {
            em.close();
        }
    }

    public int getItensVendaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ItensVenda> rt = cq.from(ItensVenda.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
