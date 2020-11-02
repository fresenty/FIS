/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Brayan
 */
@Embeddable
public class DetallePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_FACTURA")
    private int idFactura;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_DETALLEFACT")
    private int idDetallefact;

    public DetallePK() {
    }

    public DetallePK(int idFactura, int idDetallefact) {
        this.idFactura = idFactura;
        this.idDetallefact = idDetallefact;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdDetallefact() {
        return idDetallefact;
    }

    public void setIdDetallefact(int idDetallefact) {
        this.idDetallefact = idDetallefact;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idFactura;
        hash += (int) idDetallefact;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetallePK)) {
            return false;
        }
        DetallePK other = (DetallePK) object;
        if (this.idFactura != other.idFactura) {
            return false;
        }
        if (this.idDetallefact != other.idDetallefact) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Proyecto.DetallePK[ idFactura=" + idFactura + ", idDetallefact=" + idDetallefact + " ]";
    }
    
}
