/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Proyecto.Facturacion;
import Proyecto.Pago;
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
public class PagoJpaController implements Serializable {

    public PagoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pago pago) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (pago.getFacturacionCollection() == null) {
            pago.setFacturacionCollection(new ArrayList<Facturacion>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Facturacion> attachedFacturacionCollection = new ArrayList<Facturacion>();
            for (Facturacion facturacionCollectionFacturacionToAttach : pago.getFacturacionCollection()) {
                facturacionCollectionFacturacionToAttach = em.getReference(facturacionCollectionFacturacionToAttach.getClass(), facturacionCollectionFacturacionToAttach.getIdFactura());
                attachedFacturacionCollection.add(facturacionCollectionFacturacionToAttach);
            }
            pago.setFacturacionCollection(attachedFacturacionCollection);
            em.persist(pago);
            for (Facturacion facturacionCollectionFacturacion : pago.getFacturacionCollection()) {
                Pago oldNumPagoOfFacturacionCollectionFacturacion = facturacionCollectionFacturacion.getNumPago();
                facturacionCollectionFacturacion.setNumPago(pago);
                facturacionCollectionFacturacion = em.merge(facturacionCollectionFacturacion);
                if (oldNumPagoOfFacturacionCollectionFacturacion != null) {
                    oldNumPagoOfFacturacionCollectionFacturacion.getFacturacionCollection().remove(facturacionCollectionFacturacion);
                    oldNumPagoOfFacturacionCollectionFacturacion = em.merge(oldNumPagoOfFacturacionCollectionFacturacion);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPago(pago.getNumPago()) != null) {
                throw new PreexistingEntityException("Pago " + pago + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pago pago) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pago persistentPago = em.find(Pago.class, pago.getNumPago());
            Collection<Facturacion> facturacionCollectionOld = persistentPago.getFacturacionCollection();
            Collection<Facturacion> facturacionCollectionNew = pago.getFacturacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Facturacion facturacionCollectionOldFacturacion : facturacionCollectionOld) {
                if (!facturacionCollectionNew.contains(facturacionCollectionOldFacturacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Facturacion " + facturacionCollectionOldFacturacion + " since its numPago field is not nullable.");
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
            pago.setFacturacionCollection(facturacionCollectionNew);
            pago = em.merge(pago);
            for (Facturacion facturacionCollectionNewFacturacion : facturacionCollectionNew) {
                if (!facturacionCollectionOld.contains(facturacionCollectionNewFacturacion)) {
                    Pago oldNumPagoOfFacturacionCollectionNewFacturacion = facturacionCollectionNewFacturacion.getNumPago();
                    facturacionCollectionNewFacturacion.setNumPago(pago);
                    facturacionCollectionNewFacturacion = em.merge(facturacionCollectionNewFacturacion);
                    if (oldNumPagoOfFacturacionCollectionNewFacturacion != null && !oldNumPagoOfFacturacionCollectionNewFacturacion.equals(pago)) {
                        oldNumPagoOfFacturacionCollectionNewFacturacion.getFacturacionCollection().remove(facturacionCollectionNewFacturacion);
                        oldNumPagoOfFacturacionCollectionNewFacturacion = em.merge(oldNumPagoOfFacturacionCollectionNewFacturacion);
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
                Integer id = pago.getNumPago();
                if (findPago(id) == null) {
                    throw new NonexistentEntityException("The pago with id " + id + " no longer exists.");
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
            Pago pago;
            try {
                pago = em.getReference(Pago.class, id);
                pago.getNumPago();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pago with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Facturacion> facturacionCollectionOrphanCheck = pago.getFacturacionCollection();
            for (Facturacion facturacionCollectionOrphanCheckFacturacion : facturacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pago (" + pago + ") cannot be destroyed since the Facturacion " + facturacionCollectionOrphanCheckFacturacion + " in its facturacionCollection field has a non-nullable numPago field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(pago);
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

    public List<Pago> findPagoEntities() {
        return findPagoEntities(true, -1, -1);
    }

    public List<Pago> findPagoEntities(int maxResults, int firstResult) {
        return findPagoEntities(false, maxResults, firstResult);
    }

    private List<Pago> findPagoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pago.class));
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

    public Pago findPago(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pago.class, id);
        } finally {
            em.close();
        }
    }

    public int getPagoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pago> rt = cq.from(Pago.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
