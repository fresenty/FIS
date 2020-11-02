/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Proyecto.Detalle;
import Proyecto.DetallePK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Proyecto.Facturacion;
import Proyecto.Producto;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dao.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Brayan
 */
public class DetalleJpaController implements Serializable {

    public DetalleJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detalle detalle) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (detalle.getDetallePK() == null) {
            detalle.setDetallePK(new DetallePK());
        }
        detalle.getDetallePK().setIdFactura(detalle.getFacturacion().getIdFactura());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facturacion facturacion = detalle.getFacturacion();
            if (facturacion != null) {
                facturacion = em.getReference(facturacion.getClass(), facturacion.getIdFactura());
                detalle.setFacturacion(facturacion);
            }
            Producto idProducto = detalle.getIdProducto();
            if (idProducto != null) {
                idProducto = em.getReference(idProducto.getClass(), idProducto.getIdProducto());
                detalle.setIdProducto(idProducto);
            }
            em.persist(detalle);
            if (facturacion != null) {
                facturacion.getDetalleCollection().add(detalle);
                facturacion = em.merge(facturacion);
            }
            if (idProducto != null) {
                idProducto.getDetalleCollection().add(detalle);
                idProducto = em.merge(idProducto);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDetalle(detalle.getDetallePK()) != null) {
                throw new PreexistingEntityException("Detalle " + detalle + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detalle detalle) throws NonexistentEntityException, RollbackFailureException, Exception {
        detalle.getDetallePK().setIdFactura(detalle.getFacturacion().getIdFactura());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Detalle persistentDetalle = em.find(Detalle.class, detalle.getDetallePK());
            Facturacion facturacionOld = persistentDetalle.getFacturacion();
            Facturacion facturacionNew = detalle.getFacturacion();
            Producto idProductoOld = persistentDetalle.getIdProducto();
            Producto idProductoNew = detalle.getIdProducto();
            if (facturacionNew != null) {
                facturacionNew = em.getReference(facturacionNew.getClass(), facturacionNew.getIdFactura());
                detalle.setFacturacion(facturacionNew);
            }
            if (idProductoNew != null) {
                idProductoNew = em.getReference(idProductoNew.getClass(), idProductoNew.getIdProducto());
                detalle.setIdProducto(idProductoNew);
            }
            detalle = em.merge(detalle);
            if (facturacionOld != null && !facturacionOld.equals(facturacionNew)) {
                facturacionOld.getDetalleCollection().remove(detalle);
                facturacionOld = em.merge(facturacionOld);
            }
            if (facturacionNew != null && !facturacionNew.equals(facturacionOld)) {
                facturacionNew.getDetalleCollection().add(detalle);
                facturacionNew = em.merge(facturacionNew);
            }
            if (idProductoOld != null && !idProductoOld.equals(idProductoNew)) {
                idProductoOld.getDetalleCollection().remove(detalle);
                idProductoOld = em.merge(idProductoOld);
            }
            if (idProductoNew != null && !idProductoNew.equals(idProductoOld)) {
                idProductoNew.getDetalleCollection().add(detalle);
                idProductoNew = em.merge(idProductoNew);
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
                DetallePK id = detalle.getDetallePK();
                if (findDetalle(id) == null) {
                    throw new NonexistentEntityException("The detalle with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DetallePK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Detalle detalle;
            try {
                detalle = em.getReference(Detalle.class, id);
                detalle.getDetallePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalle with id " + id + " no longer exists.", enfe);
            }
            Facturacion facturacion = detalle.getFacturacion();
            if (facturacion != null) {
                facturacion.getDetalleCollection().remove(detalle);
                facturacion = em.merge(facturacion);
            }
            Producto idProducto = detalle.getIdProducto();
            if (idProducto != null) {
                idProducto.getDetalleCollection().remove(detalle);
                idProducto = em.merge(idProducto);
            }
            em.remove(detalle);
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

    public List<Detalle> findDetalleEntities() {
        return findDetalleEntities(true, -1, -1);
    }

    public List<Detalle> findDetalleEntities(int maxResults, int firstResult) {
        return findDetalleEntities(false, maxResults, firstResult);
    }

    private List<Detalle> findDetalleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detalle.class));
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

    public Detalle findDetalle(DetallePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detalle.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detalle> rt = cq.from(Detalle.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
