import javax.swing.*; 
import java.awt.*;
import java.util.*;
import java.io.*; 

public abstract class Projectile{

	// Attributs communs
	public double volume;
	public String nom;
	public double masse;
	protected double demiLongueur;
	public Image img;
	public double rho; // masse volumique du projectile

	// Constructeur
	public Projectile (String leNom, double laMasse, double laDemiLongueur, Image imgProj){
		this.nom = leNom;
		this.masse = laMasse;
		this.demiLongueur = laDemiLongueur;
		this.img = imgProj;
		this.volume = getVolume();
		this.rho = masse/volume;
	}

	public Projectile (double laMasse, double laDemiLongueur, Image imgProj){
		this.masse = laMasse;
		this.demiLongueur = laDemiLongueur;
		this.img = imgProj;
		this.volume = getVolume();
		this.rho = masse/volume;
	}

	// Projectile par défaut
	public Projectile (){
		this.nom = "balle de tennis";
		this.masse = 0.06;
		this.demiLongueur = 0.033;
		this.img = new ImageIcon("img/tennis.png").getImage();
		this.rho = 3*masse/(4*Math.PI*Math.pow(this.demiLongueur,3));
	}

	public abstract double getFrottement();
	public abstract double getVolume();

	// Affiche les attributs du lieu
    public String toString() {
		String res = "";
        res += "Le projectile est un(e) "+ this.nom	+" de masse " + this.masse +" (kg)";
        return res;  
    } 
}
