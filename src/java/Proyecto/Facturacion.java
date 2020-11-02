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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Brayan
 */
@Entity
@Table(name = "FACTURACION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Facturacion.findAll", query = "SELECT f FROM Facturacion f")
    , @NamedQuery(name = "Facturacion.findByIdFactura", query = "SELECT f FROM Facturacion f WHERE f.idFactura = :idFactura")
    , @NamedQuery(name = "Facturacion.findByFechaFact", query = "SELECT f FROM Facturacion f WHERE f.fechaFact = :fechaFact")})
public class Facturacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_FACTURA")
    private Integer idFactura;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_FACT")
    @Temporal(TemporalType.DATE)
    private Date fechaFact;
    @JoinColumn(name = "ID_CLIENTE", referencedColumnName = "ID_CLIENTE")
    @ManyToOne(optional = false)
    private Cliente idCliente;
    @JoinColumn(name = "ID_EMPLEADO", referencedColumnName = "ID_EMPLEADO")
    @ManyToOne(optional = false)
    private Empleados idEmpleado;
    @JoinColumn(name = "NUM_PAGO", referencedColumnName = "NUM_PAGO")
    @ManyToOne(optional = false)
    private Pago numPago;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facturacion")
    private Collection<Detalle> detalleCollection;

    public Facturacion() {
    }

    public Facturacion(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Facturacion(Integer idFactura, Date fechaFact) {
        this.idFactura = idFactura;
        this.fechaFact = fechaFact;
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Date getFechaFact() {
        return fechaFact;
    }

    public void setFechaFact(Date fechaFact) {
        this.fechaFact = fechaFact;
    }

    public Cliente getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Cliente idCliente) {
        this.idCliente = idCliente;
    }

    public Empleados getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Empleados idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Pago getNumPago() {
        return numPago;
    }

    public void setNumPago(Pago numPago) {
        this.numPago = numPago;
    }

    @XmlTransient
    public Collection<Detalle> getDetalleCollection() {
        return detalleCollection;
    }

    public void setDetalleCollection(Collection<Detalle> detalleCollection) {
        this.detalleCollection = detalleCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFactura != null ? idFactura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Facturacion)) {
            return false;
        }
        Facturacion other = (Facturacion) object;
        if ((this.idFactura == null && other.idFactura != null) || (this.idFactura != null && !this.idFactura.equals(other.idFactura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Proyecto.Facturacion[ idFactura=" + idFactura + " ]";
    }
    
}
