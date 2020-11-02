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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "PRODUCTO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Producto.findAll", query = "SELECT p FROM Producto p")
    , @NamedQuery(name = "Producto.findByIdProducto", query = "SELECT p FROM Producto p WHERE p.idProducto = :idProducto")
    , @NamedQuery(name = "Producto.findByNomProducto", query = "SELECT p FROM Producto p WHERE p.nomProducto = :nomProducto")
    , @NamedQuery(name = "Producto.findByStock", query = "SELECT p FROM Producto p WHERE p.stock = :stock")
    , @NamedQuery(name = "Producto.findByPrecioFact", query = "SELECT p FROM Producto p WHERE p.precioFact = :precioFact")
    , @NamedQuery(name = "Producto.findByPrecioPub", query = "SELECT p FROM Producto p WHERE p.precioPub = :precioPub")
    , @NamedQuery(name = "Producto.findByPrecioMayPub", query = "SELECT p FROM Producto p WHERE p.precioMayPub = :precioMayPub")})
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_PRODUCTO")
    private Integer idProducto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "NOM_PRODUCTO")
    private String nomProducto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STOCK")
    private int stock;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRECIO_FACT")
    private float precioFact;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRECIO_PUB")
    private float precioPub;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRECIO_MAY_PUB")
    private float precioMayPub;
    @JoinColumn(name = "CATEGORIA", referencedColumnName = "ID_CATEGORIA")
    @ManyToOne(optional = false)
    private Categoria categoria;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idProducto")
    private Collection<Detalle> detalleCollection;

    public Producto() {
    }

    public Producto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Producto(Integer idProducto, String nomProducto, int stock, float precioFact, float precioPub, float precioMayPub) {
        this.idProducto = idProducto;
        this.nomProducto = nomProducto;
        this.stock = stock;
        this.precioFact = precioFact;
        this.precioPub = precioPub;
        this.precioMayPub = precioMayPub;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNomProducto() {
        return nomProducto;
    }

    public void setNomProducto(String nomProducto) {
        this.nomProducto = nomProducto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getPrecioFact() {
        return precioFact;
    }

    public void setPrecioFact(float precioFact) {
        this.precioFact = precioFact;
    }

    public float getPrecioPub() {
        return precioPub;
    }

    public void setPrecioPub(float precioPub) {
        this.precioPub = precioPub;
    }

    public float getPrecioMayPub() {
        return precioMayPub;
    }

    public void setPrecioMayPub(float precioMayPub) {
        this.precioMayPub = precioMayPub;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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
        hash += (idProducto != null ? idProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.idProducto == null && other.idProducto != null) || (this.idProducto != null && !this.idProducto.equals(other.idProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Proyecto.Producto[ idProducto=" + idProducto + " ]";
    }
    
}
