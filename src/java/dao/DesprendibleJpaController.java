/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Proyecto.Desprendible;
import Proyecto.DesprendiblePK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Proyecto.Empleados;
import Proyecto.Nomina;
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
public class DesprendibleJpaController implements Serializable {

    public DesprendibleJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Desprendible desprendible) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (desprendible.getDesprendiblePK() == null) {
            desprendible.setDesprendiblePK(new DesprendiblePK());
        }
        desprendible.getDesprendiblePK().setIdEmpleado(desprendible.getEmpleados().getIdEmpleado());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Empleados empleados = desprendible.getEmpleados();
            if (empleados != null) {
                empleados = em.getReference(empleados.getClass(), empleados.getIdEmpleado());
                desprendible.setEmpleados(empleados);
            }
            Nomina idNomina = desprendible.getIdNomina();
            if (idNomina != null) {
                idNomina = em.getReference(idNomina.getClass(), idNomina.getIdNomina());
                desprendible.setIdNomina(idNomina);
            }
            em.persist(desprendible);
            if (empleados != null) {
                empleados.getDesprendibleCollection().add(desprendible);
                empleados = em.merge(empleados);
            }
            if (idNomina != null) {
                idNomina.getDesprendibleCollection().add(desprendible);
                idNomina = em.merge(idNomina);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDesprendible(desprendible.getDesprendiblePK()) != null) {
                throw new PreexistingEntityException("Desprendible " + desprendible + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Desprendible desprendible) throws NonexistentEntityException, RollbackFailureException, Exception {
        desprendible.getDesprendiblePK().setIdEmpleado(desprendible.getEmpleados().getIdEmpleado());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Desprendible persistentDesprendible = em.find(Desprendible.class, desprendible.getDesprendiblePK());
            Empleados empleadosOld = persistentDesprendible.getEmpleados();
            Empleados empleadosNew = desprendible.getEmpleados();
            Nomina idNominaOld = persistentDesprendible.getIdNomina();
            Nomina idNominaNew = desprendible.getIdNomina();
            if (empleadosNew != null) {
                empleadosNew = em.getReference(empleadosNew.getClass(), empleadosNew.getIdEmpleado());
                desprendible.setEmpleados(empleadosNew);
            }
            if (idNominaNew != null) {
                idNominaNew = em.getReference(idNominaNew.getClass(), idNominaNew.getIdNomina());
                desprendible.setIdNomina(idNominaNew);
            }
            desprendible = em.merge(desprendible);
            if (empleadosOld != null && !empleadosOld.equals(empleadosNew)) {
                empleadosOld.getDesprendibleCollection().remove(desprendible);
                empleadosOld = em.merge(empleadosOld);
            }
            if (empleadosNew != null && !empleadosNew.equals(empleadosOld)) {
                empleadosNew.getDesprendibleCollection().add(desprendible);
                empleadosNew = em.merge(empleadosNew);
            }
            if (idNominaOld != null && !idNominaOld.equals(idNominaNew)) {
                idNominaOld.getDesprendibleCollection().remove(desprendible);
                idNominaOld = em.merge(idNominaOld);
            }
            if (idNominaNew != null && !idNominaNew.equals(idNominaOld)) {
                idNominaNew.getDesprendibleCollection().add(desprendible);
                idNominaNew = em.merge(idNominaNew);
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
                DesprendiblePK id = desprendible.getDesprendiblePK();
                if (findDesprendible(id) == null) {
                    throw new NonexistentEntityException("The desprendible with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DesprendiblePK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Desprendible desprendible;
            try {
                desprendible = em.getReference(Desprendible.class, id);
                desprendible.getDesprendiblePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The desprendible with id " + id + " no longer exists.", enfe);
            }
            Empleados empleados = desprendible.getEmpleados();
            if (empleados != null) {
                empleados.getDesprendibleCollection().remove(desprendible);
                empleados = em.merge(empleados);
            }
            Nomina idNomina = desprendible.getIdNomina();
            if (idNomina != null) {
                idNomina.getDesprendibleCollection().remove(desprendible);
                idNomina = em.merge(idNomina);
            }
            em.remove(desprendible);
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

    public List<Desprendible> findDesprendibleEntities() {
        return findDesprendibleEntities(true, -1, -1);
    }

    public List<Desprendible> findDesprendibleEntities(int maxResults, int firstResult) {
        return findDesprendibleEntities(false, maxResults, firstResult);
    }

    private List<Desprendible> findDesprendibleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Desprendible.class));
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

    public Desprendible findDesprendible(DesprendiblePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Desprendible.class, id);
        } finally {
            em.close();
        }
    }

    public int getDesprendibleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Desprendible> rt = cq.from(Desprendible.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
