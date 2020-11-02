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
@Table(name = "PAGO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pago.findAll", query = "SELECT p FROM Pago p")
    , @NamedQuery(name = "Pago.findByNumPago", query = "SELECT p FROM Pago p WHERE p.numPago = :numPago")
    , @NamedQuery(name = "Pago.findByNomPago", query = "SELECT p FROM Pago p WHERE p.nomPago = :nomPago")
    , @NamedQuery(name = "Pago.findByDetPago", query = "SELECT p FROM Pago p WHERE p.detPago = :detPago")})
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "NUM_PAGO")
    private Integer numPago;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "NOM_PAGO")
    private String nomPago;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "DET_PAGO")
    private String detPago;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "numPago")
    private Collection<Facturacion> facturacionCollection;

    public Pago() {
    }

    public Pago(Integer numPago) {
        this.numPago = numPago;
    }

    public Pago(Integer numPago, String nomPago, String detPago) {
        this.numPago = numPago;
        this.nomPago = nomPago;
        this.detPago = detPago;
    }

    public Integer getNumPago() {
        return numPago;
    }

    public void setNumPago(Integer numPago) {
        this.numPago = numPago;
    }

    public String getNomPago() {
        return nomPago;
    }

    public void setNomPago(String nomPago) {
        this.nomPago = nomPago;
    }

    public String getDetPago() {
        return detPago;
    }

    public void setDetPago(String detPago) {
        this.detPago = detPago;
    }

    @XmlTransient
    public Collection<Facturacion> getFacturacionCollection() {
        return facturacionCollection;
    }

    public void setFacturacionCollection(Collection<Facturacion> facturacionCollection) {
        this.facturacionCollection = facturacionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numPago != null ? numPago.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pago)) {
            return false;
        }
        Pago other = (Pago) object;
        if ((this.numPago == null && other.numPago != null) || (this.numPago != null && !this.numPago.equals(other.numPago))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Proyecto.Pago[ numPago=" + numPago + " ]";
    }
    
}
