import java.util.ArrayList;
public class Point {
    
	// Attributs (coordonnées)
    public double x;
    public double y;
    
    // Constructeur
    public Point(double leX, double leY){
        this.x = leX;
        this.y = leY;
    }

    // Constructeur par défaut
    public Point(){
        this.x = 0;
        this.y = 0;
    }
    
    /*
    Calcule la distance euclidienne par rapport à un autre point
    @param le point à partir duquel il faut calculer la distance
    @return la distance euclidienne
    */        
    public double distance( Point otherPoint ) {
        double dx = x - otherPoint.x;
        double dy = y - otherPoint.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    
    /*
    Affiche les coordonnées du point
    @return les coordonnées du point sous la forme [x=1.0,y=1.0]
    */
    public String toString() {
		String res = "";
        res += "[x=" + x + ",y=" + y + "]";
        return res;  
    }    
}

