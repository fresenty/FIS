/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Brayan
 */
@Entity
@Table(name = "DETALLE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalle.findAll", query = "SELECT d FROM Detalle d")
    , @NamedQuery(name = "Detalle.findByIdFactura", query = "SELECT d FROM Detalle d WHERE d.detallePK.idFactura = :idFactura")
    , @NamedQuery(name = "Detalle.findByIdDetallefact", query = "SELECT d FROM Detalle d WHERE d.detallePK.idDetallefact = :idDetallefact")
    , @NamedQuery(name = "Detalle.findByCantFact", query = "SELECT d FROM Detalle d WHERE d.cantFact = :cantFact")
    , @NamedQuery(name = "Detalle.findByPrecio", query = "SELECT d FROM Detalle d WHERE d.precio = :precio")})
public class Detalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DetallePK detallePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CANT_FACT")
    private float cantFact;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRECIO")
    private float precio;
    @JoinColumn(name = "ID_FACTURA", referencedColumnName = "ID_FACTURA", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Facturacion facturacion;
    @JoinColumn(name = "ID_PRODUCTO", referencedColumnName = "ID_PRODUCTO")
    @ManyToOne(optional = false)
    private Producto idProducto;

    public Detalle() {
    }

    public Detalle(DetallePK detallePK) {
        this.detallePK = detallePK;
    }

    public Detalle(DetallePK detallePK, float cantFact, float precio) {
        this.detallePK = detallePK;
        this.cantFact = cantFact;
        this.precio = precio;
    }

    public Detalle(int idFactura, int idDetallefact) {
        this.detallePK = new DetallePK(idFactura, idDetallefact);
    }

    public DetallePK getDetallePK() {
        return detallePK;
    }

    public void setDetallePK(DetallePK detallePK) {
        this.detallePK = detallePK;
    }

    public float getCantFact() {
        return cantFact;
    }

    public void setCantFact(float cantFact) {
        this.cantFact = cantFact;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public Facturacion getFacturacion() {
        return facturacion;
    }

    public void setFacturacion(Facturacion facturacion) {
        this.facturacion = facturacion;
    }

    public Producto getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (detallePK != null ? detallePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detalle)) {
            return false;
        }
        Detalle other = (Detalle) object;
        if ((this.detallePK == null && other.detallePK != null) || (this.detallePK != null && !this.detallePK.equals(other.detallePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Proyecto.Detalle[ detallePK=" + detallePK + " ]";
    }
    
}
