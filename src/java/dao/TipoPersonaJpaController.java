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
import Proyecto.TipoPersona;
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
public class TipoPersonaJpaController implements Serializable {

    public TipoPersonaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoPersona tipoPersona) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (tipoPersona.getPersonaCollection() == null) {
            tipoPersona.setPersonaCollection(new ArrayList<Persona>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Persona> attachedPersonaCollection = new ArrayList<Persona>();
            for (Persona personaCollectionPersonaToAttach : tipoPersona.getPersonaCollection()) {
                personaCollectionPersonaToAttach = em.getReference(personaCollectionPersonaToAttach.getClass(), personaCollectionPersonaToAttach.getCedula());
                attachedPersonaCollection.add(personaCollectionPersonaToAttach);
            }
            tipoPersona.setPersonaCollection(attachedPersonaCollection);
            em.persist(tipoPersona);
            for (Persona personaCollectionPersona : tipoPersona.getPersonaCollection()) {
                TipoPersona oldTipoPersonaOfPersonaCollectionPersona = personaCollectionPersona.getTipoPersona();
                personaCollectionPersona.setTipoPersona(tipoPersona);
                personaCollectionPersona = em.merge(personaCollectionPersona);
                if (oldTipoPersonaOfPersonaCollectionPersona != null) {
                    oldTipoPersonaOfPersonaCollectionPersona.getPersonaCollection().remove(personaCollectionPersona);
                    oldTipoPersonaOfPersonaCollectionPersona = em.merge(oldTipoPersonaOfPersonaCollectionPersona);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findTipoPersona(tipoPersona.getIdTipoPersona()) != null) {
                throw new PreexistingEntityException("TipoPersona " + tipoPersona + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoPersona tipoPersona) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            TipoPersona persistentTipoPersona = em.find(TipoPersona.class, tipoPersona.getIdTipoPersona());
            Collection<Persona> personaCollectionOld = persistentTipoPersona.getPersonaCollection();
            Collection<Persona> personaCollectionNew = tipoPersona.getPersonaCollection();
            List<String> illegalOrphanMessages = null;
            for (Persona personaCollectionOldPersona : personaCollectionOld) {
                if (!personaCollectionNew.contains(personaCollectionOldPersona)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Persona " + personaCollectionOldPersona + " since its tipoPersona field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Persona> attachedPersonaCollectionNew = new ArrayList<Persona>();
            for (Persona personaCollectionNewPersonaToAttach : personaCollectionNew) {
                personaCollectionNewPersonaToAttach = em.getReference(personaCollectionNewPersonaToAttach.getClass(), personaCollectionNewPersonaToAttach.getCedula());
                attachedPersonaCollectionNew.add(personaCollectionNewPersonaToAttach);
            }
            personaCollectionNew = attachedPersonaCollectionNew;
            tipoPersona.setPersonaCollection(personaCollectionNew);
            tipoPersona = em.merge(tipoPersona);
            for (Persona personaCollectionNewPersona : personaCollectionNew) {
                if (!personaCollectionOld.contains(personaCollectionNewPersona)) {
                    TipoPersona oldTipoPersonaOfPersonaCollectionNewPersona = personaCollectionNewPersona.getTipoPersona();
                    personaCollectionNewPersona.setTipoPersona(tipoPersona);
                    personaCollectionNewPersona = em.merge(personaCollectionNewPersona);
                    if (oldTipoPersonaOfPersonaCollectionNewPersona != null && !oldTipoPersonaOfPersonaCollectionNewPersona.equals(tipoPersona)) {
                        oldTipoPersonaOfPersonaCollectionNewPersona.getPersonaCollection().remove(personaCollectionNewPersona);
                        oldTipoPersonaOfPersonaCollectionNewPersona = em.merge(oldTipoPersonaOfPersonaCollectionNewPersona);
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
                String id = tipoPersona.getIdTipoPersona();
                if (findTipoPersona(id) == null) {
                    throw new NonexistentEntityException("The tipoPersona with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            TipoPersona tipoPersona;
            try {
                tipoPersona = em.getReference(TipoPersona.class, id);
                tipoPersona.getIdTipoPersona();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoPersona with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Persona> personaCollectionOrphanCheck = tipoPersona.getPersonaCollection();
            for (Persona personaCollectionOrphanCheckPersona : personaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TipoPersona (" + tipoPersona + ") cannot be destroyed since the Persona " + personaCollectionOrphanCheckPersona + " in its personaCollection field has a non-nullable tipoPersona field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoPersona);
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

    public List<TipoPersona> findTipoPersonaEntities() {
        return findTipoPersonaEntities(true, -1, -1);
    }

    public List<TipoPersona> findTipoPersonaEntities(int maxResults, int firstResult) {
        return findTipoPersonaEntities(false, maxResults, firstResult);
    }

    private List<TipoPersona> findTipoPersonaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoPersona.class));
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

    public TipoPersona findTipoPersona(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoPersona.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoPersonaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoPersona> rt = cq.from(TipoPersona.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
