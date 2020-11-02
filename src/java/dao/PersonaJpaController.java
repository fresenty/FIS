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
import Proyecto.TipoPersona;
import Proyecto.Empleados;
import Proyecto.Persona;
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
public class PersonaJpaController implements Serializable {

    public PersonaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Persona persona) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (persona.getEmpleadosCollection() == null) {
            persona.setEmpleadosCollection(new ArrayList<Empleados>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            TipoPersona tipoPersona = persona.getTipoPersona();
            if (tipoPersona != null) {
                tipoPersona = em.getReference(tipoPersona.getClass(), tipoPersona.getIdTipoPersona());
                persona.setTipoPersona(tipoPersona);
            }
            Collection<Empleados> attachedEmpleadosCollection = new ArrayList<Empleados>();
            for (Empleados empleadosCollectionEmpleadosToAttach : persona.getEmpleadosCollection()) {
                empleadosCollectionEmpleadosToAttach = em.getReference(empleadosCollectionEmpleadosToAttach.getClass(), empleadosCollectionEmpleadosToAttach.getIdEmpleado());
                attachedEmpleadosCollection.add(empleadosCollectionEmpleadosToAttach);
            }
            persona.setEmpleadosCollection(attachedEmpleadosCollection);
            em.persist(persona);
            if (tipoPersona != null) {
                tipoPersona.getPersonaCollection().add(persona);
                tipoPersona = em.merge(tipoPersona);
            }
            for (Empleados empleadosCollectionEmpleados : persona.getEmpleadosCollection()) {
                Persona oldCedulaOfEmpleadosCollectionEmpleados = empleadosCollectionEmpleados.getCedula();
                empleadosCollectionEmpleados.setCedula(persona);
                empleadosCollectionEmpleados = em.merge(empleadosCollectionEmpleados);
                if (oldCedulaOfEmpleadosCollectionEmpleados != null) {
                    oldCedulaOfEmpleadosCollectionEmpleados.getEmpleadosCollection().remove(empleadosCollectionEmpleados);
                    oldCedulaOfEmpleadosCollectionEmpleados = em.merge(oldCedulaOfEmpleadosCollectionEmpleados);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPersona(persona.getCedula()) != null) {
                throw new PreexistingEntityException("Persona " + persona + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Persona persona) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Persona persistentPersona = em.find(Persona.class, persona.getCedula());
            TipoPersona tipoPersonaOld = persistentPersona.getTipoPersona();
            TipoPersona tipoPersonaNew = persona.getTipoPersona();
            Collection<Empleados> empleadosCollectionOld = persistentPersona.getEmpleadosCollection();
            Collection<Empleados> empleadosCollectionNew = persona.getEmpleadosCollection();
            List<String> illegalOrphanMessages = null;
            for (Empleados empleadosCollectionOldEmpleados : empleadosCollectionOld) {
                if (!empleadosCollectionNew.contains(empleadosCollectionOldEmpleados)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Empleados " + empleadosCollectionOldEmpleados + " since its cedula field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (tipoPersonaNew != null) {
                tipoPersonaNew = em.getReference(tipoPersonaNew.getClass(), tipoPersonaNew.getIdTipoPersona());
                persona.setTipoPersona(tipoPersonaNew);
            }
            Collection<Empleados> attachedEmpleadosCollectionNew = new ArrayList<Empleados>();
            for (Empleados empleadosCollectionNewEmpleadosToAttach : empleadosCollectionNew) {
                empleadosCollectionNewEmpleadosToAttach = em.getReference(empleadosCollectionNewEmpleadosToAttach.getClass(), empleadosCollectionNewEmpleadosToAttach.getIdEmpleado());
                attachedEmpleadosCollectionNew.add(empleadosCollectionNewEmpleadosToAttach);
            }
            empleadosCollectionNew = attachedEmpleadosCollectionNew;
            persona.setEmpleadosCollection(empleadosCollectionNew);
            persona = em.merge(persona);
            if (tipoPersonaOld != null && !tipoPersonaOld.equals(tipoPersonaNew)) {
                tipoPersonaOld.getPersonaCollection().remove(persona);
                tipoPersonaOld = em.merge(tipoPersonaOld);
            }
            if (tipoPersonaNew != null && !tipoPersonaNew.equals(tipoPersonaOld)) {
                tipoPersonaNew.getPersonaCollection().add(persona);
                tipoPersonaNew = em.merge(tipoPersonaNew);
            }
            for (Empleados empleadosCollectionNewEmpleados : empleadosCollectionNew) {
                if (!empleadosCollectionOld.contains(empleadosCollectionNewEmpleados)) {
                    Persona oldCedulaOfEmpleadosCollectionNewEmpleados = empleadosCollectionNewEmpleados.getCedula();
                    empleadosCollectionNewEmpleados.setCedula(persona);
                    empleadosCollectionNewEmpleados = em.merge(empleadosCollectionNewEmpleados);
                    if (oldCedulaOfEmpleadosCollectionNewEmpleados != null && !oldCedulaOfEmpleadosCollectionNewEmpleados.equals(persona)) {
                        oldCedulaOfEmpleadosCollectionNewEmpleados.getEmpleadosCollection().remove(empleadosCollectionNewEmpleados);
                        oldCedulaOfEmpleadosCollectionNewEmpleados = em.merge(oldCedulaOfEmpleadosCollectionNewEmpleados);
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
                Integer id = persona.getCedula();
                if (findPersona(id) == null) {
                    throw new NonexistentEntityException("The persona with id " + id + " no longer exists.");
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
            Persona persona;
            try {
                persona = em.getReference(Persona.class, id);
                persona.getCedula();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The persona with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Empleados> empleadosCollectionOrphanCheck = persona.getEmpleadosCollection();
            for (Empleados empleadosCollectionOrphanCheckEmpleados : empleadosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persona (" + persona + ") cannot be destroyed since the Empleados " + empleadosCollectionOrphanCheckEmpleados + " in its empleadosCollection field has a non-nullable cedula field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TipoPersona tipoPersona = persona.getTipoPersona();
            if (tipoPersona != null) {
                tipoPersona.getPersonaCollection().remove(persona);
                tipoPersona = em.merge(tipoPersona);
            }
            em.remove(persona);
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

    public List<Persona> findPersonaEntities() {
        return findPersonaEntities(true, -1, -1);
    }

    public List<Persona> findPersonaEntities(int maxResults, int firstResult) {
        return findPersonaEntities(false, maxResults, firstResult);
    }

    private List<Persona> findPersonaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Persona.class));
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

    public Persona findPersona(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Persona.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Persona> rt = cq.from(Persona.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
