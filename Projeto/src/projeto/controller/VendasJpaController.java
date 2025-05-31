package projeto.controller;

import java.io.Serializable;
import java.util.Calendar; // Import para Calendar
import java.util.Date;    // Import para java.util.Date
import java.util.HashMap; // Import para HashMap
import java.util.List;    // Este já deve existir
import java.util.Map;     // Import para Map
import javax.persistence.EntityManager; // Este já deve existir
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;  // Import para TypedQuery
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import projeto.controller.exceptions.NonexistentEntityException;
import projeto.entities.Vendas;

/**
 *
 * @author docar (ou seu nome)
 */
public class VendasJpaController implements Serializable {

    public VendasJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vendas vendas) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(vendas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Vendas vendas) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            vendas = em.merge(vendas);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = vendas.getIdVenda();
                if (findVendas(id) == null) {
                    throw new NonexistentEntityException("The vendas with id " + id + " no longer exists.");
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
            Vendas vendas;
            try {
                vendas = em.getReference(Vendas.class, id);
                vendas.getIdVenda(); // Para forçar o carregamento ou lançar EntityNotFoundException
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vendas with id " + id + " no longer exists.", enfe);
            }
            em.remove(vendas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Vendas> findVendasEntities() {
        return findVendasEntities(true, -1, -1);
    }

    public List<Vendas> findVendasEntities(int maxResults, int firstResult) {
        return findVendasEntities(false, maxResults, firstResult);
    }

    private List<Vendas> findVendasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vendas.class));
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

    public Vendas findVendas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vendas.class, id);
        } finally {
            em.close();
        }
    }

    public int getVendasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vendas> rt = cq.from(Vendas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    // --- MÉTODO ADICIONADO PARA BUSCA COM FILTROS ---
    public List<Vendas> findVendasComFiltros(Date dataDe, Date dataAte, Integer idCliente) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder jpqlString = new StringBuilder("SELECT v FROM Vendas v WHERE 1=1");
            Map<String, Object> parameters = new HashMap<>();

            if (dataDe != null) {
                jpqlString.append(" AND v.dataVenda >= :dataDe");
                parameters.put("dataDe", dataDe);
            }
            if (dataAte != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(dataAte);
                c.add(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                Date dataAteProximoDia = c.getTime();

                jpqlString.append(" AND v.dataVenda < :dataAteProximoDia");
                parameters.put("dataAteProximoDia", dataAteProximoDia);
            }
            if (idCliente != null) {
                // ATENÇÃO: Verifique o nome do atributo na sua entidade Vendas.java
                // Se o atributo que guarda o ID do cliente (ou o objeto Cliente) se chama 'idCliente':
                jpqlString.append(" AND v.idCliente = :idCliente");
                // Se o atributo for um objeto Cliente (ex: 'private Clientes cliente;') e você quer comparar pelo ID dele:
                // jpqlString.append(" AND v.cliente.idCliente = :idCliente");
                parameters.put("idCliente", idCliente);
            }

            jpqlString.append(" ORDER BY v.dataVenda DESC");

            TypedQuery<Vendas> query = em.createQuery(jpqlString.toString(), Vendas.class);

            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    // --- FIM DO MÉTODO ADICIONADO ---
}