import javax.swing.*; 
import java.awt.*;
import java.util.*;
import java.io.*; 

public class Balle extends Projectile{

	// Constructeur
	public Balle (String leNom, double laMasse, double laDemiLongueur,Image imgProj){
		super(leNom, laMasse, laDemiLongueur, imgProj);
	}

	// Constructeur par défaut
	public Balle (){
		super();
	}
	public double getFrottement(){
		return(2*Math.PI*this.demiLongueur);
	}
	public double getVolume(){
		return ((4*Math.PI*Math.pow(this.demiLongueur,3))/3);
	}

	// Affiche les attributs du lieu
    public String toString() {
		String res = super.toString();
        res += " et de rayon "+ this.demiLongueur + " m ";
        return res;  
    } 
}
