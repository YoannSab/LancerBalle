import java.awt.Color;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*; 


public class Traj{

	// Attributs
	public ArrayList<Point> listePts;
	public Color couleur;
	public Projectile proj;
	public Lieu lieu;
	public boolean frottements;
	public double alpha;
	public double alpha_deg;
	public double v0;
	public Double grossissement = 1.0;
	public Double echelle = 1.0;
	public double pas;
	public int nbPoint;
	public boolean milieuLiquide;

	// Constructeur
	public Traj (Projectile leProj, Lieu leLieu, boolean frott, double angle_deg, double vitInit){
		// assignation des attributs
		this.couleur = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
		this.proj = leProj;
		this.lieu = leLieu;
		this.frottements = frott;
		this.alpha_deg = angle_deg;
		this.alpha = alpha_deg*Math.PI/180;
		this.v0 = vitInit;

		/*-----vérifie si milieu solide ou liquide ----*/
		if(lieu.nom == "sur la Terre"||lieu.nom == "sur Mars"||lieu.nom == "sur la Lune"||lieu.nom == "sur le Mont Everest"){
			milieuLiquide = false;
		}else{
			milieuLiquide = true;
		}
		
		/*----- calcul de l'échelle -----*/
		calculTraj();
		setEchelle();
	}


	//Calcul de la trajctoire avec ou sans frottements
	public void calculTraj (){

		listePts = new ArrayList<Point>();
		// paramètres
		double demiLongueur = proj.demiLongueur;
		double masse = proj.masse;
		double coeffFrott = lieu.coeffFrott;
		double k = 3*proj.getFrottement()*coeffFrott;
		double to = masse/k;
		// conditions initiales
		// c'est un homme qui lance la balle
	    double x0 = 1.5; // longueur de l'homme le bras tendu en avant
	    double y0 = 2; // hauteur de l'homme la main levée
		// calcul du g en prenant en compte la poussée d'archimède
		double gravite = (1-(lieu.rho/proj.rho))*lieu.gravite; 
		// Par défaut, 1 pixel correspond à 1 m
		int i = 0;
		pas = 0.1;
		Point unPt;
		double limiteY; //ou est-ce qu'on s'arrete en y?

		// définition des limites en y
		if(milieuLiquide){
			limiteY = -v0/3; //= fin de la fenetre....
		}else{
			limiteY = 0; 
		}
		//definition du nombre de points en fonction des cas limites
		if(flotte()||(milieuLiquide&&frottements&&(lieu.coeffFrott>=0.1))){
			 nbPoint=20;
		}else{
			nbPoint=10000; //garde-fou
		}
		// calcul des points
		if (frottements){  // calcul points traj avec frottements
			do{
				unPt = new Point();
				unPt.x = v0*Math.cos(alpha)*to*(1-Math.exp((-i*pas)/to)) + x0;
				unPt.y = -to*to*gravite*(Math.exp((-i*pas)/to)-1) + (v0*Math.sin(alpha)-to*gravite)*i*pas + y0;
				listePts.add(unPt);
				i++;
			}while(i < nbPoint && unPt.y > limiteY); 
		}else{	// calcul points traj avec frottements
			do{
				unPt = new Point();
				unPt.x = v0*Math.cos(alpha)*i*pas + x0;
				unPt.y = -0.5*gravite*Math.pow(i*pas,2)+v0*Math.sin(alpha)*i*pas + y0;
				listePts.add(unPt);
				i++;
			}while(i < nbPoint && unPt.y > limiteY);
		}
		// créer le dernier point à l'intersection de la traj avec le sol
		if(i != nbPoint && !milieuLiquide){
			Point lastPt = listePts.get(i-1);
			Point penultimatePt = listePts.get(i-2);
			double coeffPente = (lastPt.y - penultimatePt.y)/((lastPt.x - penultimatePt.x));
			double newX = penultimatePt.x - penultimatePt.y/coeffPente;
			double newY = 0;
			listePts.set(i-1, new Point(newX, newY));
		}
	}

	// retourne les coordonnées d'un point 
    // dont le y est celui du point le plus haut de la trajectoire
    // et le x est celui du point le plus loin de la trajectoire tout en étant au dessus du sol
    public Point getMax (){
		// coordonnées du point
		Point max = new Point(listePts.get(0).x, listePts.get(0).y);
		// parcours des points de la trajectoire et assignement des coordonnées
		for(Point pt : listePts){
			if(pt.x >= max.x){
				max.x = pt.x;
			}
			if(pt.y >= max.y){
				max.y = pt.y;
			}
		}
		return max;
	}

	//renvoie un boolean vérifiant si la balle flotte dans le lieu ou non 
	public boolean flotte(){ 
		if(proj.rho<lieu.rho){
			return true;
		}else{
			return false;
		}
	}

	// Calcul l'échelle nécessaire pour un affichage optimal
	// Par défaut, 1 pixel correspond à 1 m
	public void setEchelle(){
			// point extrême de la courbe
			Point max = getMax();
			// taille de la fenêtre
			int windowWidth = 800;
			int windowHeight = 500;
			// nombre de pixels de marge selon les x et les y
			int margeX = FenetreTraj.margeX;
			int margeY = FenetreTraj.margeY;
			// taille de la zone de dessin
			int drawingAreaWidth = windowWidth - 2*margeX;
			int drawingAreHeight = windowHeight - 2*margeY;
			// calcul du grossissement nécessaire pour afficher le point
			// le plus loin/haut au niveau de la marge désirée
			Double grossissementX = new Double(((double)(drawingAreaWidth))/max.x);
			Double grossissementY = new Double(((double)(drawingAreHeight))/max.y);
			// on garde le plus petit des 2 grossissements autrement la courbe dépasserai sur l'autre axe
			grossissement = Double.min(grossissementX, grossissementY);
			// calcul de l'échelle
			echelle = 1.0/grossissement;
	}

    public String toString() {
		String res = "";
		res += lieu.toString();
		res += "\n";
		res += proj.toString();
		res += "\n";
		if(frottements){
			res += "Les frottements sont pris en compte";
		}else{
			res += "Les frottements ne sont pas pris en compte";
		}
		res += "\n Le projectile part avec un angle "+alpha_deg+" \u00b0 et avec une vitesse initiale de "+v0+" m/s";

        return res;
    }  
}
