import javax.swing.*; 
import java.awt.*;
import java.util.*;
import java.io.*; 

public class Marteau extends Projectile{
	//attributs
	public double volume;

	// Constructeur
	public Marteau (double laMasse, double laDemiLongueur,Image imgProj){
		super(laMasse, laDemiLongueur, imgProj);
		this.nom = "d'un marteau";
	}

	// Constructeur par défaut
	public Marteau (){
		super(1, 0.08, new ImageIcon("img/marteau.png").getImage());
		this.nom = "d'un marteau";

	}
	public double getFrottement(){
		return(demiLongueur*demiLongueur/4);
	}
	public double getVolume(){
		return (Math.PI*Math.pow((demiLongueur/4),2)*2*demiLongueur); //approximation par un cylindre de rayon h/8

	}

	// Affiche les attributs du projectile
    public String toString() {
		String res = super.toString();
        res += " et de longueur "+ this.demiLongueur*2 + " m ";
        return res;  
    } 

    public static void main(String[] args){
    	Projectile test = new Marteau (0.7,0.03,new ImageIcon("img/pingpong.png").getImage());

    }

}
