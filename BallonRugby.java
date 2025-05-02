import javax.swing.*; 
import java.awt.*;
import java.util.*;
import java.io.*; 

public class BallonRugby extends Projectile{

	// Constructeur
	public BallonRugby (double laMasse, double laDemiLongueur,Image imgProj){
		super(laMasse, laDemiLongueur, imgProj);
		this.nom = "d'un ballon de rugby";
	}

	// Constructeur par défaut
	public BallonRugby (){
		super(0.5, 0.1, new ImageIcon("img.rugby.png").getImage());
		this.nom = "d'un ballon de rugby";
		
	}
	public double getFrottement(){
		return(Math.PI*Math.pow((demiLongueur),2)/8);
	}
	public double getVolume(){
		return ((4*Math.PI*Math.pow((demiLongueur/2),2)*demiLongueur)/3);
	}

	// Affiche les attributs du projectile
    public String toString() {
		String res = super.toString();
        res += " et de longueur "+ this.demiLongueur*2 + " m ";
        return res;  
    } 
}
