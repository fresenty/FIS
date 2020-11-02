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
import Proyecto.Cliente;
import Proyecto.Empleados;
import Proyecto.Pago;
import Proyecto.Detalle;
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
public class FacturacionJpaController implements Serializable {

    public FacturacionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Facturacion facturacion) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (facturacion.getDetalleCollection() == null) {
            facturacion.setDetalleCollection(new ArrayList<Detalle>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cliente idCliente = facturacion.getIdCliente();
            if (idCliente != null) {
                idCliente = em.getReference(idCliente.getClass(), idCliente.getIdCliente());
                facturacion.setIdCliente(idCliente);
            }
            Empleados idEmpleado = facturacion.getIdEmpleado();
            if (idEmpleado != null) {
                idEmpleado = em.getReference(idEmpleado.getClass(), idEmpleado.getIdEmpleado());
                facturacion.setIdEmpleado(idEmpleado);
            }
            Pago numPago = facturacion.getNumPago();
            if (numPago != null) {
                numPago = em.getReference(numPago.getClass(), numPago.getNumPago());
                facturacion.setNumPago(numPago);
            }
            Collection<Detalle> attachedDetalleCollection = new ArrayList<Detalle>();
            for (Detalle detalleCollectionDetalleToAttach : facturacion.getDetalleCollection()) {
                detalleCollectionDetalleToAttach = em.getReference(detalleCollectionDetalleToAttach.getClass(), detalleCollectionDetalleToAttach.getDetallePK());
                attachedDetalleCollection.add(detalleCollectionDetalleToAttach);
            }
            facturacion.setDetalleCollection(attachedDetalleCollection);
            em.persist(facturacion);
            if (idCliente != null) {
                idCliente.getFacturacionCollection().add(facturacion);
                idCliente = em.merge(idCliente);
            }
            if (idEmpleado != null) {
                idEmpleado.getFacturacionCollection().add(facturacion);
                idEmpleado = em.merge(idEmpleado);
            }
            if (numPago != null) {
                numPago.getFacturacionCollection().add(facturacion);
                numPago = em.merge(numPago);
            }
            for (Detalle detalleCollectionDetalle : facturacion.getDetalleCollection()) {
                Facturacion oldFacturacionOfDetalleCollectionDetalle = detalleCollectionDetalle.getFacturacion();
                detalleCollectionDetalle.setFacturacion(facturacion);
                detalleCollectionDetalle = em.merge(detalleCollectionDetalle);
                if (oldFacturacionOfDetalleCollectionDetalle != null) {
                    oldFacturacionOfDetalleCollectionDetalle.getDetalleCollection().remove(detalleCollectionDetalle);
                    oldFacturacionOfDetalleCollectionDetalle = em.merge(oldFacturacionOfDetalleCollectionDetalle);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findFacturacion(facturacion.getIdFactura()) != null) {
                throw new PreexistingEntityException("Facturacion " + facturacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Facturacion facturacion) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facturacion persistentFacturacion = em.find(Facturacion.class, facturacion.getIdFactura());
            Cliente idClienteOld = persistentFacturacion.getIdCliente();
            Cliente idClienteNew = facturacion.getIdCliente();
            Empleados idEmpleadoOld = persistentFacturacion.getIdEmpleado();
            Empleados idEmpleadoNew = facturacion.getIdEmpleado();
            Pago numPagoOld = persistentFacturacion.getNumPago();
            Pago numPagoNew = facturacion.getNumPago();
            Collection<Detalle> detalleCollectionOld = persistentFacturacion.getDetalleCollection();
            Collection<Detalle> detalleCollectionNew = facturacion.getDetalleCollection();
            List<String> illegalOrphanMessages = null;
            for (Detalle detalleCollectionOldDetalle : detalleCollectionOld) {
                if (!detalleCollectionNew.contains(detalleCollectionOldDetalle)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalle " + detalleCollectionOldDetalle + " since its facturacion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idClienteNew != null) {
                idClienteNew = em.getReference(idClienteNew.getClass(), idClienteNew.getIdCliente());
                facturacion.setIdCliente(idClienteNew);
            }
            if (idEmpleadoNew != null) {
                idEmpleadoNew = em.getReference(idEmpleadoNew.getClass(), idEmpleadoNew.getIdEmpleado());
                facturacion.setIdEmpleado(idEmpleadoNew);
            }
            if (numPagoNew != null) {
                numPagoNew = em.getReference(numPagoNew.getClass(), numPagoNew.getNumPago());
                facturacion.setNumPago(numPagoNew);
            }
            Collection<Detalle> attachedDetalleCollectionNew = new ArrayList<Detalle>();
            for (Detalle detalleCollectionNewDetalleToAttach : detalleCollectionNew) {
                detalleCollectionNewDetalleToAttach = em.getReference(detalleCollectionNewDetalleToAttach.getClass(), detalleCollectionNewDetalleToAttach.getDetallePK());
                attachedDetalleCollectionNew.add(detalleCollectionNewDetalleToAttach);
            }
            detalleCollectionNew = attachedDetalleCollectionNew;
            facturacion.setDetalleCollection(detalleCollectionNew);
            facturacion = em.merge(facturacion);
            if (idClienteOld != null && !idClienteOld.equals(idClienteNew)) {
                idClienteOld.getFacturacionCollection().remove(facturacion);
                idClienteOld = em.merge(idClienteOld);
            }
            if (idClienteNew != null && !idClienteNew.equals(idClienteOld)) {
                idClienteNew.getFacturacionCollection().add(facturacion);
                idClienteNew = em.merge(idClienteNew);
            }
            if (idEmpleadoOld != null && !idEmpleadoOld.equals(idEmpleadoNew)) {
                idEmpleadoOld.getFacturacionCollection().remove(facturacion);
                idEmpleadoOld = em.merge(idEmpleadoOld);
            }
            if (idEmpleadoNew != null && !idEmpleadoNew.equals(idEmpleadoOld)) {
                idEmpleadoNew.getFacturacionCollection().add(facturacion);
                idEmpleadoNew = em.merge(idEmpleadoNew);
            }
            if (numPagoOld != null && !numPagoOld.equals(numPagoNew)) {
                numPagoOld.getFacturacionCollection().remove(facturacion);
                numPagoOld = em.merge(numPagoOld);
            }
            if (numPagoNew != null && !numPagoNew.equals(numPagoOld)) {
                numPagoNew.getFacturacionCollection().add(facturacion);
                numPagoNew = em.merge(numPagoNew);
            }
            for (Detalle detalleCollectionNewDetalle : detalleCollectionNew) {
                if (!detalleCollectionOld.contains(detalleCollectionNewDetalle)) {
                    Facturacion oldFacturacionOfDetalleCollectionNewDetalle = detalleCollectionNewDetalle.getFacturacion();
                    detalleCollectionNewDetalle.setFacturacion(facturacion);
                    detalleCollectionNewDetalle = em.merge(detalleCollectionNewDetalle);
                    if (oldFacturacionOfDetalleCollectionNewDetalle != null && !oldFacturacionOfDetalleCollectionNewDetalle.equals(facturacion)) {
                        oldFacturacionOfDetalleCollectionNewDetalle.getDetalleCollection().remove(detalleCollectionNewDetalle);
                        oldFacturacionOfDetalleCollectionNewDetalle = em.merge(oldFacturacionOfDetalleCollectionNewDetalle);
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
                Integer id = facturacion.getIdFactura();
                if (findFacturacion(id) == null) {
                    throw new NonexistentEntityException("The facturacion with id " + id + " no longer exists.");
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
            Facturacion facturacion;
            try {
                facturacion = em.getReference(Facturacion.class, id);
                facturacion.getIdFactura();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facturacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detalle> detalleCollectionOrphanCheck = facturacion.getDetalleCollection();
            for (Detalle detalleCollectionOrphanCheckDetalle : detalleCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facturacion (" + facturacion + ") cannot be destroyed since the Detalle " + detalleCollectionOrphanCheckDetalle + " in its detalleCollection field has a non-nullable facturacion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cliente idCliente = facturacion.getIdCliente();
            if (idCliente != null) {
                idCliente.getFacturacionCollection().remove(facturacion);
                idCliente = em.merge(idCliente);
            }
            Empleados idEmpleado = facturacion.getIdEmpleado();
            if (idEmpleado != null) {
                idEmpleado.getFacturacionCollection().remove(facturacion);
                idEmpleado = em.merge(idEmpleado);
            }
            Pago numPago = facturacion.getNumPago();
            if (numPago != null) {
                numPago.getFacturacionCollection().remove(facturacion);
                numPago = em.merge(numPago);
            }
            em.remove(facturacion);
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

    public List<Facturacion> findFacturacionEntities() {
        return findFacturacionEntities(true, -1, -1);
    }

    public List<Facturacion> findFacturacionEntities(int maxResults, int firstResult) {
        return findFacturacionEntities(false, maxResults, firstResult);
    }

    private List<Facturacion> findFacturacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facturacion.class));
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

    public Facturacion findFacturacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facturacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacturacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facturacion> rt = cq.from(Facturacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
