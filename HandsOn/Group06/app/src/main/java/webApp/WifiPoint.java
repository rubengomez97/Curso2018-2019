/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webApp;

/**
 *
 * @author Ruben
 */

public class WifiPoint {
    String nombre;
    String calle;
    String tlf;
    public WifiPoint(String nombre, String calle, String tlf,String url){
        this.nombre=nombre;
        this.calle=calle;
        this.tlf=tlf;
        
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setTlf(String tlf) {
        this.tlf = tlf;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCalle() {
        return calle;
    }

    public String getTlf() {
        return tlf;
    }
    
    
}
