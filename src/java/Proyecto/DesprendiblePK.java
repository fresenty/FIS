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
public class DesprendiblePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_EMPLEADO")
    private int idEmpleado;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_DESPRENDIBLE")
    private int idDesprendible;

    public DesprendiblePK() {
    }

    public DesprendiblePK(int idEmpleado, int idDesprendible) {
        this.idEmpleado = idEmpleado;
        this.idDesprendible = idDesprendible;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdDesprendible() {
        return idDesprendible;
    }

    public void setIdDesprendible(int idDesprendible) {
        this.idDesprendible = idDesprendible;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idEmpleado;
        hash += (int) idDesprendible;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DesprendiblePK)) {
            return false;
        }
        DesprendiblePK other = (DesprendiblePK) object;
        if (this.idEmpleado != other.idEmpleado) {
            return false;
        }
        if (this.idDesprendible != other.idDesprendible) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Proyecto.DesprendiblePK[ idEmpleado=" + idEmpleado + ", idDesprendible=" + idDesprendible + " ]";
    }
    
}
