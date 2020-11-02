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
import Proyecto.Desprendible;
import Proyecto.Nomina;
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
public class NominaJpaController implements Serializable {

    public NominaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Nomina nomina) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (nomina.getDesprendibleCollection() == null) {
            nomina.setDesprendibleCollection(new ArrayList<Desprendible>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Desprendible> attachedDesprendibleCollection = new ArrayList<Desprendible>();
            for (Desprendible desprendibleCollectionDesprendibleToAttach : nomina.getDesprendibleCollection()) {
                desprendibleCollectionDesprendibleToAttach = em.getReference(desprendibleCollectionDesprendibleToAttach.getClass(), desprendibleCollectionDesprendibleToAttach.getDesprendiblePK());
                attachedDesprendibleCollection.add(desprendibleCollectionDesprendibleToAttach);
            }
            nomina.setDesprendibleCollection(attachedDesprendibleCollection);
            em.persist(nomina);
            for (Desprendible desprendibleCollectionDesprendible : nomina.getDesprendibleCollection()) {
                Nomina oldIdNominaOfDesprendibleCollectionDesprendible = desprendibleCollectionDesprendible.getIdNomina();
                desprendibleCollectionDesprendible.setIdNomina(nomina);
                desprendibleCollectionDesprendible = em.merge(desprendibleCollectionDesprendible);
                if (oldIdNominaOfDesprendibleCollectionDesprendible != null) {
                    oldIdNominaOfDesprendibleCollectionDesprendible.getDesprendibleCollection().remove(desprendibleCollectionDesprendible);
                    oldIdNominaOfDesprendibleCollectionDesprendible = em.merge(oldIdNominaOfDesprendibleCollectionDesprendible);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findNomina(nomina.getIdNomina()) != null) {
                throw new PreexistingEntityException("Nomina " + nomina + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Nomina nomina) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Nomina persistentNomina = em.find(Nomina.class, nomina.getIdNomina());
            Collection<Desprendible> desprendibleCollectionOld = persistentNomina.getDesprendibleCollection();
            Collection<Desprendible> desprendibleCollectionNew = nomina.getDesprendibleCollection();
            List<String> illegalOrphanMessages = null;
            for (Desprendible desprendibleCollectionOldDesprendible : desprendibleCollectionOld) {
                if (!desprendibleCollectionNew.contains(desprendibleCollectionOldDesprendible)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Desprendible " + desprendibleCollectionOldDesprendible + " since its idNomina field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Desprendible> attachedDesprendibleCollectionNew = new ArrayList<Desprendible>();
            for (Desprendible desprendibleCollectionNewDesprendibleToAttach : desprendibleCollectionNew) {
                desprendibleCollectionNewDesprendibleToAttach = em.getReference(desprendibleCollectionNewDesprendibleToAttach.getClass(), desprendibleCollectionNewDesprendibleToAttach.getDesprendiblePK());
                attachedDesprendibleCollectionNew.add(desprendibleCollectionNewDesprendibleToAttach);
            }
            desprendibleCollectionNew = attachedDesprendibleCollectionNew;
            nomina.setDesprendibleCollection(desprendibleCollectionNew);
            nomina = em.merge(nomina);
            for (Desprendible desprendibleCollectionNewDesprendible : desprendibleCollectionNew) {
                if (!desprendibleCollectionOld.contains(desprendibleCollectionNewDesprendible)) {
                    Nomina oldIdNominaOfDesprendibleCollectionNewDesprendible = desprendibleCollectionNewDesprendible.getIdNomina();
                    desprendibleCollectionNewDesprendible.setIdNomina(nomina);
                    desprendibleCollectionNewDesprendible = em.merge(desprendibleCollectionNewDesprendible);
                    if (oldIdNominaOfDesprendibleCollectionNewDesprendible != null && !oldIdNominaOfDesprendibleCollectionNewDesprendible.equals(nomina)) {
                        oldIdNominaOfDesprendibleCollectionNewDesprendible.getDesprendibleCollection().remove(desprendibleCollectionNewDesprendible);
                        oldIdNominaOfDesprendibleCollectionNewDesprendible = em.merge(oldIdNominaOfDesprendibleCollectionNewDesprendible);
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
                Integer id = nomina.getIdNomina();
                if (findNomina(id) == null) {
                    throw new NonexistentEntityException("The nomina with id " + id + " no longer exists.");
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
            Nomina nomina;
            try {
                nomina = em.getReference(Nomina.class, id);
                nomina.getIdNomina();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The nomina with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Desprendible> desprendibleCollectionOrphanCheck = nomina.getDesprendibleCollection();
            for (Desprendible desprendibleCollectionOrphanCheckDesprendible : desprendibleCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Nomina (" + nomina + ") cannot be destroyed since the Desprendible " + desprendibleCollectionOrphanCheckDesprendible + " in its desprendibleCollection field has a non-nullable idNomina field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(nomina);
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

    public List<Nomina> findNominaEntities() {
        return findNominaEntities(true, -1, -1);
    }

    public List<Nomina> findNominaEntities(int maxResults, int firstResult) {
        return findNominaEntities(false, maxResults, firstResult);
    }

    private List<Nomina> findNominaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Nomina.class));
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

    public Nomina findNomina(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Nomina.class, id);
        } finally {
            em.close();
        }
    }

    public int getNominaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Nomina> rt = cq.from(Nomina.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
