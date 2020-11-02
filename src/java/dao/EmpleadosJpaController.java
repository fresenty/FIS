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
import Proyecto.Persona;
import Proyecto.Facturacion;
import java.util.ArrayList;
import java.util.Collection;
import Proyecto.Desprendible;
import Proyecto.Empleados;
import dao.exceptions.IllegalOrphanException;
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
public class EmpleadosJpaController implements Serializable {

    public EmpleadosJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empleados empleados) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (empleados.getFacturacionCollection() == null) {
            empleados.setFacturacionCollection(new ArrayList<Facturacion>());
        }
        if (empleados.getDesprendibleCollection() == null) {
            empleados.setDesprendibleCollection(new ArrayList<Desprendible>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Persona cedula = empleados.getCedula();
            if (cedula != null) {
                cedula = em.getReference(cedula.getClass(), cedula.getCedula());
                empleados.setCedula(cedula);
            }
            Collection<Facturacion> attachedFacturacionCollection = new ArrayList<Facturacion>();
            for (Facturacion facturacionCollectionFacturacionToAttach : empleados.getFacturacionCollection()) {
                facturacionCollectionFacturacionToAttach = em.getReference(facturacionCollectionFacturacionToAttach.getClass(), facturacionCollectionFacturacionToAttach.getIdFactura());
                attachedFacturacionCollection.add(facturacionCollectionFacturacionToAttach);
            }
            empleados.setFacturacionCollection(attachedFacturacionCollection);
            Collection<Desprendible> attachedDesprendibleCollection = new ArrayList<Desprendible>();
            for (Desprendible desprendibleCollectionDesprendibleToAttach : empleados.getDesprendibleCollection()) {
                desprendibleCollectionDesprendibleToAttach = em.getReference(desprendibleCollectionDesprendibleToAttach.getClass(), desprendibleCollectionDesprendibleToAttach.getDesprendiblePK());
                attachedDesprendibleCollection.add(desprendibleCollectionDesprendibleToAttach);
            }
            empleados.setDesprendibleCollection(attachedDesprendibleCollection);
            em.persist(empleados);
            if (cedula != null) {
                cedula.getEmpleadosCollection().add(empleados);
                cedula = em.merge(cedula);
            }
            for (Facturacion facturacionCollectionFacturacion : empleados.getFacturacionCollection()) {
                Empleados oldIdEmpleadoOfFacturacionCollectionFacturacion = facturacionCollectionFacturacion.getIdEmpleado();
                facturacionCollectionFacturacion.setIdEmpleado(empleados);
                facturacionCollectionFacturacion = em.merge(facturacionCollectionFacturacion);
                if (oldIdEmpleadoOfFacturacionCollectionFacturacion != null) {
                    oldIdEmpleadoOfFacturacionCollectionFacturacion.getFacturacionCollection().remove(facturacionCollectionFacturacion);
                    oldIdEmpleadoOfFacturacionCollectionFacturacion = em.merge(oldIdEmpleadoOfFacturacionCollectionFacturacion);
                }
            }
            for (Desprendible desprendibleCollectionDesprendible : empleados.getDesprendibleCollection()) {
                Empleados oldEmpleadosOfDesprendibleCollectionDesprendible = desprendibleCollectionDesprendible.getEmpleados();
                desprendibleCollectionDesprendible.setEmpleados(empleados);
                desprendibleCollectionDesprendible = em.merge(desprendibleCollectionDesprendible);
                if (oldEmpleadosOfDesprendibleCollectionDesprendible != null) {
                    oldEmpleadosOfDesprendibleCollectionDesprendible.getDesprendibleCollection().remove(desprendibleCollectionDesprendible);
                    oldEmpleadosOfDesprendibleCollectionDesprendible = em.merge(oldEmpleadosOfDesprendibleCollectionDesprendible);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEmpleados(empleados.getIdEmpleado()) != null) {
                throw new PreexistingEntityException("Empleados " + empleados + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Empleados empleados) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Empleados persistentEmpleados = em.find(Empleados.class, empleados.getIdEmpleado());
            Persona cedulaOld = persistentEmpleados.getCedula();
            Persona cedulaNew = empleados.getCedula();
            Collection<Facturacion> facturacionCollectionOld = persistentEmpleados.getFacturacionCollection();
            Collection<Facturacion> facturacionCollectionNew = empleados.getFacturacionCollection();
            Collection<Desprendible> desprendibleCollectionOld = persistentEmpleados.getDesprendibleCollection();
            Collection<Desprendible> desprendibleCollectionNew = empleados.getDesprendibleCollection();
            List<String> illegalOrphanMessages = null;
            for (Facturacion facturacionCollectionOldFacturacion : facturacionCollectionOld) {
                if (!facturacionCollectionNew.contains(facturacionCollectionOldFacturacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Facturacion " + facturacionCollectionOldFacturacion + " since its idEmpleado field is not nullable.");
                }
            }
            for (Desprendible desprendibleCollectionOldDesprendible : desprendibleCollectionOld) {
                if (!desprendibleCollectionNew.contains(desprendibleCollectionOldDesprendible)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Desprendible " + desprendibleCollectionOldDesprendible + " since its empleados field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cedulaNew != null) {
                cedulaNew = em.getReference(cedulaNew.getClass(), cedulaNew.getCedula());
                empleados.setCedula(cedulaNew);
            }
            Collection<Facturacion> attachedFacturacionCollectionNew = new ArrayList<Facturacion>();
            for (Facturacion facturacionCollectionNewFacturacionToAttach : facturacionCollectionNew) {
                facturacionCollectionNewFacturacionToAttach = em.getReference(facturacionCollectionNewFacturacionToAttach.getClass(), facturacionCollectionNewFacturacionToAttach.getIdFactura());
                attachedFacturacionCollectionNew.add(facturacionCollectionNewFacturacionToAttach);
            }
            facturacionCollectionNew = attachedFacturacionCollectionNew;
            empleados.setFacturacionCollection(facturacionCollectionNew);
            Collection<Desprendible> attachedDesprendibleCollectionNew = new ArrayList<Desprendible>();
            for (Desprendible desprendibleCollectionNewDesprendibleToAttach : desprendibleCollectionNew) {
                desprendibleCollectionNewDesprendibleToAttach = em.getReference(desprendibleCollectionNewDesprendibleToAttach.getClass(), desprendibleCollectionNewDesprendibleToAttach.getDesprendiblePK());
                attachedDesprendibleCollectionNew.add(desprendibleCollectionNewDesprendibleToAttach);
            }
            desprendibleCollectionNew = attachedDesprendibleCollectionNew;
            empleados.setDesprendibleCollection(desprendibleCollectionNew);
            empleados = em.merge(empleados);
            if (cedulaOld != null && !cedulaOld.equals(cedulaNew)) {
                cedulaOld.getEmpleadosCollection().remove(empleados);
                cedulaOld = em.merge(cedulaOld);
            }
            if (cedulaNew != null && !cedulaNew.equals(cedulaOld)) {
                cedulaNew.getEmpleadosCollection().add(empleados);
                cedulaNew = em.merge(cedulaNew);
            }
            for (Facturacion facturacionCollectionNewFacturacion : facturacionCollectionNew) {
                if (!facturacionCollectionOld.contains(facturacionCollectionNewFacturacion)) {
                    Empleados oldIdEmpleadoOfFacturacionCollectionNewFacturacion = facturacionCollectionNewFacturacion.getIdEmpleado();
                    facturacionCollectionNewFacturacion.setIdEmpleado(empleados);
                    facturacionCollectionNewFacturacion = em.merge(facturacionCollectionNewFacturacion);
                    if (oldIdEmpleadoOfFacturacionCollectionNewFacturacion != null && !oldIdEmpleadoOfFacturacionCollectionNewFacturacion.equals(empleados)) {
                        oldIdEmpleadoOfFacturacionCollectionNewFacturacion.getFacturacionCollection().remove(facturacionCollectionNewFacturacion);
                        oldIdEmpleadoOfFacturacionCollectionNewFacturacion = em.merge(oldIdEmpleadoOfFacturacionCollectionNewFacturacion);
                    }
                }
            }
            for (Desprendible desprendibleCollectionNewDesprendible : desprendibleCollectionNew) {
                if (!desprendibleCollectionOld.contains(desprendibleCollectionNewDesprendible)) {
                    Empleados oldEmpleadosOfDesprendibleCollectionNewDesprendible = desprendibleCollectionNewDesprendible.getEmpleados();
                    desprendibleCollectionNewDesprendible.setEmpleados(empleados);
                    desprendibleCollectionNewDesprendible = em.merge(desprendibleCollectionNewDesprendible);
                    if (oldEmpleadosOfDesprendibleCollectionNewDesprendible != null && !oldEmpleadosOfDesprendibleCollectionNewDesprendible.equals(empleados)) {
                        oldEmpleadosOfDesprendibleCollectionNewDesprendible.getDesprendibleCollection().remove(desprendibleCollectionNewDesprendible);
                        oldEmpleadosOfDesprendibleCollectionNewDesprendible = em.merge(oldEmpleadosOfDesprendibleCollectionNewDesprendible);
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
                Integer id = empleados.getIdEmpleado();
                if (findEmpleados(id) == null) {
                    throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.");
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
            Empleados empleados;
            try {
                empleados = em.getReference(Empleados.class, id);
                empleados.getIdEmpleado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Facturacion> facturacionCollectionOrphanCheck = empleados.getFacturacionCollection();
            for (Facturacion facturacionCollectionOrphanCheckFacturacion : facturacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Empleados (" + empleados + ") cannot be destroyed since the Facturacion " + facturacionCollectionOrphanCheckFacturacion + " in its facturacionCollection field has a non-nullable idEmpleado field.");
            }
            Collection<Desprendible> desprendibleCollectionOrphanCheck = empleados.getDesprendibleCollection();
            for (Desprendible desprendibleCollectionOrphanCheckDesprendible : desprendibleCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Empleados (" + empleados + ") cannot be destroyed since the Desprendible " + desprendibleCollectionOrphanCheckDesprendible + " in its desprendibleCollection field has a non-nullable empleados field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Persona cedula = empleados.getCedula();
            if (cedula != null) {
                cedula.getEmpleadosCollection().remove(empleados);
                cedula = em.merge(cedula);
            }
            em.remove(empleados);
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

    public List<Empleados> findEmpleadosEntities() {
        return findEmpleadosEntities(true, -1, -1);
    }

    public List<Empleados> findEmpleadosEntities(int maxResults, int firstResult) {
        return findEmpleadosEntities(false, maxResults, firstResult);
    }

    private List<Empleados> findEmpleadosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empleados.class));
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

    public Empleados findEmpleados(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Empleados.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpleadosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Empleados> rt = cq.from(Empleados.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
