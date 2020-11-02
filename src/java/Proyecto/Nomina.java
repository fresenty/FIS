/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Brayan
 */
@Entity
@Table(name = "NOMINA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Nomina.findAll", query = "SELECT n FROM Nomina n")
    , @NamedQuery(name = "Nomina.findByIdNomina", query = "SELECT n FROM Nomina n WHERE n.idNomina = :idNomina")
    , @NamedQuery(name = "Nomina.findBySalario", query = "SELECT n FROM Nomina n WHERE n.salario = :salario")
    , @NamedQuery(name = "Nomina.findByPension", query = "SELECT n FROM Nomina n WHERE n.pension = :pension")
    , @NamedQuery(name = "Nomina.findBySalud", query = "SELECT n FROM Nomina n WHERE n.salud = :salud")
    , @NamedQuery(name = "Nomina.findByATransporte", query = "SELECT n FROM Nomina n WHERE n.aTransporte = :aTransporte")
    , @NamedQuery(name = "Nomina.findByDetNomina", query = "SELECT n FROM Nomina n WHERE n.detNomina = :detNomina")})
public class Nomina implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_NOMINA")
    private Integer idNomina;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SALARIO")
    private float salario;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PENSION")
    private float pension;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SALUD")
    private float salud;
    @Basic(optional = false)
    @NotNull
    @Column(name = "A_TRANSPORTE")
    private float aTransporte;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "DET_NOMINA")
    private String detNomina;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idNomina")
    private Collection<Desprendible> desprendibleCollection;

    public Nomina() {
    }

    public Nomina(Integer idNomina) {
        this.idNomina = idNomina;
    }

    public Nomina(Integer idNomina, float salario, float pension, float salud, float aTransporte, String detNomina) {
        this.idNomina = idNomina;
        this.salario = salario;
        this.pension = pension;
        this.salud = salud;
        this.aTransporte = aTransporte;
        this.detNomina = detNomina;
    }

    public Integer getIdNomina() {
        return idNomina;
    }

    public void setIdNomina(Integer idNomina) {
        this.idNomina = idNomina;
    }

    public float getSalario() {
        return salario;
    }

    public void setSalario(float salario) {
        this.salario = salario;
    }

    public float getPension() {
        return pension;
    }

    public void setPension(float pension) {
        this.pension = pension;
    }

    public float getSalud() {
        return salud;
    }

    public void setSalud(float salud) {
        this.salud = salud;
    }

    public float getATransporte() {
        return aTransporte;
    }

    public void setATransporte(float aTransporte) {
        this.aTransporte = aTransporte;
    }

    public String getDetNomina() {
        return detNomina;
    }

    public void setDetNomina(String detNomina) {
        this.detNomina = detNomina;
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
        hash += (idNomina != null ? idNomina.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Nomina)) {
            return false;
        }
        Nomina other = (Nomina) object;
        if ((this.idNomina == null && other.idNomina != null) || (this.idNomina != null && !this.idNomina.equals(other.idNomina))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Proyecto.Nomina[ idNomina=" + idNomina + " ]";
    }
    
}
