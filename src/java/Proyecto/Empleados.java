/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Brayan
 */
@Entity
@Table(name = "EMPLEADOS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Empleados.findAll", query = "SELECT e FROM Empleados e")
    , @NamedQuery(name = "Empleados.findByIdEmpleado", query = "SELECT e FROM Empleados e WHERE e.idEmpleado = :idEmpleado")
    , @NamedQuery(name = "Empleados.findByContrase\u00f1a", query = "SELECT e FROM Empleados e WHERE e.contrase\u00f1a = :contrase\u00f1a")
    , @NamedQuery(name = "Empleados.findByFechaInicio", query = "SELECT e FROM Empleados e WHERE e.fechaInicio = :fechaInicio")
    , @NamedQuery(name = "Empleados.findByFechaFinal", query = "SELECT e FROM Empleados e WHERE e.fechaFinal = :fechaFinal")})
public class Empleados implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_EMPLEADO")
    private Integer idEmpleado;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CONTRASE\u00d1A")
    private String contraseña;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_INICIO")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Column(name = "FECHA_FINAL")
    @Temporal(TemporalType.DATE)
    private Date fechaFinal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idEmpleado")
    private Collection<Facturacion> facturacionCollection;
    @JoinColumn(name = "CEDULA", referencedColumnName = "CEDULA")
    @ManyToOne(optional = false)
    private Persona cedula;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empleados")
    private Collection<Desprendible> desprendibleCollection;

    public Empleados() {
    }

    public Empleados(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Empleados(Integer idEmpleado, String contraseña, Date fechaInicio) {
        this.idEmpleado = idEmpleado;
        this.contraseña = contraseña;
        this.fechaInicio = fechaInicio;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    @XmlTransient
    public Collection<Facturacion> getFacturacionCollection() {
        return facturacionCollection;
    }

    public void setFacturacionCollection(Collection<Facturacion> facturacionCollection) {
        this.facturacionCollection = facturacionCollection;
    }

    public Persona getCedula() {
        return cedula;
    }

    public void setCedula(Persona cedula) {
        this.cedula = cedula;
    }

    @XmlTransient
    public Collection<Desprendible> getDesprendibleCollection() {
        return desprendibleCollection;
    }

    public void setDesprendibleCollection(Collection<Desprendible> desprendibleCollection) {
        this.desprendibleCollection = desprendibleCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEmpleado != null ? idEmpleado.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Empleados)) {
            return false;
        }
        Empleados other = (Empleados) object;
        if ((this.idEmpleado == null && other.idEmpleado != null) || (this.idEmpleado != null && !this.idEmpleado.equals(other.idEmpleado))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Proyecto.Empleados[ idEmpleado=" + idEmpleado + " ]";
    }
    
}
