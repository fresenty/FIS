/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Brayan
 */
@Entity
@Table(name = "DESPRENDIBLE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Desprendible.findAll", query = "SELECT d FROM Desprendible d")
    , @NamedQuery(name = "Desprendible.findByIdEmpleado", query = "SELECT d FROM Desprendible d WHERE d.desprendiblePK.idEmpleado = :idEmpleado")
    , @NamedQuery(name = "Desprendible.findByIdDesprendible", query = "SELECT d FROM Desprendible d WHERE d.desprendiblePK.idDesprendible = :idDesprendible")
    , @NamedQuery(name = "Desprendible.findBySueldo", query = "SELECT d FROM Desprendible d WHERE d.sueldo = :sueldo")
    , @NamedQuery(name = "Desprendible.findByFechaPago", query = "SELECT d FROM Desprendible d WHERE d.fechaPago = :fechaPago")})
public class Desprendible implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DesprendiblePK desprendiblePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SUELDO")
    private float sueldo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_PAGO")
    @Temporal(TemporalType.DATE)
    private Date fechaPago;
    @JoinColumn(name = "ID_EMPLEADO", referencedColumnName = "ID_EMPLEADO", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Empleados empleados;
    @JoinColumn(name = "ID_NOMINA", referencedColumnName = "ID_NOMINA")
    @ManyToOne(optional = false)
    private Nomina idNomina;

    public Desprendible() {
    }

    public Desprendible(DesprendiblePK desprendiblePK) {
        this.desprendiblePK = desprendiblePK;
    }

    public Desprendible(DesprendiblePK desprendiblePK, float sueldo, Date fechaPago) {
        this.desprendiblePK = desprendiblePK;
        this.sueldo = sueldo;
        this.fechaPago = fechaPago;
    }

    public Desprendible(int idEmpleado, int idDesprendible) {
        this.desprendiblePK = new DesprendiblePK(idEmpleado, idDesprendible);
    }

    public DesprendiblePK getDesprendiblePK() {
        return desprendiblePK;
    }

    public void setDesprendiblePK(DesprendiblePK desprendiblePK) {
        this.desprendiblePK = desprendiblePK;
    }

    public float getSueldo() {
        return sueldo;
    }

    public void setSueldo(float sueldo) {
        this.sueldo = sueldo;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Empleados getEmpleados() {
        return empleados;
    }

    public void setEmpleados(Empleados empleados) {
        this.empleados = empleados;
    }

    public Nomina getIdNomina() {
        return idNomina;
    }

    public void setIdNomina(Nomina idNomina) {
        this.idNomina = idNomina;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (desprendiblePK != null ? desprendiblePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Desprendible)) {
            return false;
        }
        Desprendible other = (Desprendible) object;
        if ((this.desprendiblePK == null && other.desprendiblePK != null) || (this.desprendiblePK != null && !this.desprendiblePK.equals(other.desprendiblePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Proyecto.Desprendible[ desprendiblePK=" + desprendiblePK + " ]";
    }
    
}
