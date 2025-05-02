import javax.swing.*; 
import java.awt.*;
import java.util.*;
import java.io.*; 


public class Lieu{

	// Attributs
	public String nom;
	public double gravite;
	public double coeffFrott;
	public double rho; //masse volumique du milieu
	public Image img;

	// Constructeur
	public Lieu (String leNom, double laGravite, double leCoeffFrott, double rho, Image imgLieu ){
		this.nom = leNom;
		this.gravite = laGravite;
		this.coeffFrott = leCoeffFrott;
		this.rho = rho;
		this.img = imgLieu;
	}

	// Constructeur par défaut
	public Lieu (){
		this.nom = "Terre";
		this.gravite = 9.81;
		this.coeffFrott = 18*Math.pow(10, -6);
		this.rho = 1.225; // kg/m3
		this.img = new ImageIcon("img/terre.png").getImage();
	}

	// Affiche les attributs du lieu
    public String toString() {
		String res = "";
        res += "Le lancer s'effectue  sur "+ nom +" ou la gravite est de "+ gravite 
        	   +"(m/s^2) et le coefficient de frottements de l'atmosphere est de"+ coeffFrott +"(kg/s)";
        return res;  
    }
}
