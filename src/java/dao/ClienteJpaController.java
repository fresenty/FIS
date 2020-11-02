/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Proyecto.Cliente;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Proyecto.Facturacion;
import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dao.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Brayan
 */
public class ClienteJpaController implements Serializable {

    public ClienteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (cliente.getFacturacionCollection() == null) {
            cliente.setFacturacionCollection(new ArrayList<Facturacion>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Facturacion> attachedFacturacionCollection = new ArrayList<Facturacion>();
            for (Facturacion facturacionCollectionFacturacionToAttach : cliente.getFacturacionCollection()) {
                facturacionCollectionFacturacionToAttach = em.getReference(facturacionCollectionFacturacionToAttach.getClass(), facturacionCollectionFacturacionToAttach.getIdFactura());
                attachedFacturacionCollection.add(facturacionCollectionFacturacionToAttach);
            }
            cliente.setFacturacionCollection(attachedFacturacionCollection);
            em.persist(cliente);
            for (Facturacion facturacionCollectionFacturacion : cliente.getFacturacionCollection()) {
                Cliente oldIdClienteOfFacturacionCollectionFacturacion = facturacionCollectionFacturacion.getIdCliente();
                facturacionCollectionFacturacion.setIdCliente(cliente);
                facturacionCollectionFacturacion = em.merge(facturacionCollectionFacturacion);
                if (oldIdClienteOfFacturacionCollectionFacturacion != null) {
                    oldIdClienteOfFacturacionCollectionFacturacion.getFacturacionCollection().remove(facturacionCollectionFacturacion);
                    oldIdClienteOfFacturacionCollectionFacturacion = em.merge(oldIdClienteOfFacturacionCollectionFacturacion);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCliente(cliente.getIdCliente()) != null) {
                throw new PreexistingEntityException("Cliente " + cliente + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cliente persistentCliente = em.find(Cliente.class, cliente.getIdCliente());
            Collection<Facturacion> facturacionCollectionOld = persistentCliente.getFacturacionCollection();
            Collection<Facturacion> facturacionCollectionNew = cliente.getFacturacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Facturacion facturacionCollectionOldFacturacion : facturacionCollectionOld) {
                if (!facturacionCollectionNew.contains(facturacionCollectionOldFacturacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Facturacion " + facturacionCollectionOldFacturacion + " since its idCliente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Facturacion> attachedFacturacionCollectionNew = new ArrayList<Facturacion>();
            for (Facturacion facturacionCollectionNewFacturacionToAttach : facturacionCollectionNew) {
                facturacionCollectionNewFacturacionToAttach = em.getReference(facturacionCollectionNewFacturacionToAttach.getClass(), facturacionCollectionNewFacturacionToAttach.getIdFactura());
                attachedFacturacionCollectionNew.add(facturacionCollectionNewFacturacionToAttach);
            }
            facturacionCollectionNew = attachedFacturacionCollectionNew;
            cliente.setFacturacionCollection(facturacionCollectionNew);
            cliente = em.merge(cliente);
            for (Facturacion facturacionCollectionNewFacturacion : facturacionCollectionNew) {
                if (!facturacionCollectionOld.contains(facturacionCollectionNewFacturacion)) {
                    Cliente oldIdClienteOfFacturacionCollectionNewFacturacion = facturacionCollectionNewFacturacion.getIdCliente();
                    facturacionCollectionNewFacturacion.setIdCliente(cliente);
                    facturacionCollectionNewFacturacion = em.merge(facturacionCollectionNewFacturacion);
                    if (oldIdClienteOfFacturacionCollectionNewFacturacion != null && !oldIdClienteOfFacturacionCollectionNewFacturacion.equals(cliente)) {
                        oldIdClienteOfFacturacionCollectionNewFacturacion.getFacturacionCollection().remove(facturacionCollectionNewFacturacion);
                        oldIdClienteOfFacturacionCollectionNewFacturacion = em.merge(oldIdClienteOfFacturacionCollectionNewFacturacion);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cliente.getIdCliente();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getIdCliente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Facturacion> facturacionCollectionOrphanCheck = cliente.getFacturacionCollection();
            for (Facturacion facturacionCollectionOrphanCheckFacturacion : facturacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Facturacion " + facturacionCollectionOrphanCheckFacturacion + " in its facturacionCollection field has a non-nullable idCliente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cliente);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
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

    public Cliente findCliente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
