import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;
import java.io.*; 
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.*;
import java.awt.event.WindowEvent;  
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;


public class FenetreTraj extends JFrame implements ActionListener, KeyListener, MouseListener, WindowListener{

	public LinkedList<Traj> listeTraj = new LinkedList<Traj>(); // liste des traj
	public Traj currentTraj;
	public int currentIndex;
	public boolean animation;

	public String hommePath = "img/homme.png";
	public String projPath;
	public Image arrPlan;
	public Image arrPlan_Scaled;
  public Image imgProj;
  public Image imgHomme;
  public Image drapeau = new ImageIcon("img/drapeau.png").getImage(); 
  public Image drapeauScaled;
  public JLabel titre;
  public Double grossissementGlobal;
  public Double echelleGlobale;

  public boolean erreurGrossissement = true;

  public double grossMinProjDrapeau = 20.0;
  public double grossMinHomme = 1.0;

  public String textPosition = "";

	public Graphics2D  g_buff;
	public Graphics2D g2D;
	public BufferedImage buff_img;

	public int windowWidth = 800;
	public int windowHeight = 500;
	public static int margeX = 60;
	public static int margeY = 85;
	public int x0DrawingArea = margeX;
	public int y0DrawingArea = windowHeight-margeY;

	public Timer mt;

  public LancerBalle fenetreParent;
	
	public FenetreTraj(Traj maTraj, boolean choixAnim, LancerBalle parent){
    fenetreParent = parent;
		// assignation de la traj
		currentTraj = maTraj;
		currentIndex = 0;
    animation = choixAnim;
		// assignation du grossissement et de l'échelle
		grossissementGlobal = currentTraj.grossissement;
		echelleGlobale = currentTraj.echelle;
		// création et mise à l'échelle des images
    arrPlan = maTraj.lieu.img;
    arrPlan_Scaled= arrPlan.getScaledInstance(windowWidth, windowHeight, Image.SCALE_DEFAULT);
	  setImagesScaled();

		// création du timer
    mt = new Timer((int)(currentTraj.pas*1000),this);

		// création de la fenêtre
    setTitle("Affichage de la trajectoire");
    setBounds(100, 50, windowWidth, windowHeight);
    setVisible(true);
    setLayout(null);
    setResizable(false);
    addKeyListener(this);
    addMouseListener(this);
    addWindowListener(this);
    // création du buffer
    buff_img= new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
    g_buff =  (Graphics2D) buff_img.getGraphics();

    // légende
    titre = new JLabel();
    titre.setBounds(25, 5, 750, 30);
   	titre.setFont(new Font("Arial",Font.BOLD,10));
   	titre.setText(getTexteTitre());
    titre.setHorizontalAlignment(JLabel.CENTER);
		add(titre);
  }
			
	// la traj courrante devient la nouvelle traj 
	public void addTraj(Traj newTraj){
		// ajouter l'ancienne traj courrante à la liste des autres traj
		listeTraj.add(currentTraj);
		// rendre courante la nouvelle Traj
		currentTraj = newTraj;
		currentIndex = 0;
		// mise à jour de la légende de la traj courante
		titre.setText(getTexteTitre());
		// mise à jour de l'échelle
		grossissementGlobal = Double.min(grossissementGlobal, currentTraj.grossissement);
		echelleGlobale = Double.max(echelleGlobale, currentTraj.echelle);
		// mise à jour des images
		setImagesScaled();
		// mise à jour de l'affichage
		repaint();
	}
		
  public void paint(Graphics g) {
   g2D = (Graphics2D) g;
    	// Affichage du background
   drawBackGround();
    	// Affichage des autres traj 	
   drawListTraj();
    	// Affichage de la traj courante
   if(!animation){
     drawTraj();
   }else{
     animeTraj();
   }
		// Affichage le buffer
   g2D.drawImage(buff_img,0,0,this);
		// Affichage du titre
   titre.paintImmediately(titre.getVisibleRect());
  }

  public void drawTraj(){
    	// affichage des points de la traj courante.
    int rayonProj = imgProj.getWidth(null)/2;
    double posX = x0DrawingArea;
    double posY = y0DrawingArea;
    for(int i = 0; i < currentTraj.listePts.size(); i++){
     posX = x0DrawingArea + grossissementGlobal*currentTraj.listePts.get(i).x;
     posY = y0DrawingArea - grossissementGlobal*currentTraj.listePts.get(i).y;
     // rotation du projectile
     AffineTransform tr = new AffineTransform();
     tr.translate(posX-rayonProj, posY-rayonProj);
     tr.rotate(Math.toRadians(10*i), rayonProj, rayonProj);
     g_buff.drawImage(imgProj, tr, this);
     // sans rotation du projectile
     // g_buff.drawImage(imgProj, (int) posX-rayonProj, (int) posY-rayonProj, this);
   }
      	// Affichage du drapeau
   drawDrapeau(posX);
  }

    // Affichage de la traj au fil du temps
  public void animeTraj (){
    	// lancement du chrono au premier point
   if(currentIndex == 0){
    startChrono();
    }
    	// Affichage des points
    int rayonProj = imgProj.getWidth(null)/2;
    double posX = x0DrawingArea;
    double posY = y0DrawingArea;
    for(int i = 0; i <= currentIndex; i++){
      posX = x0DrawingArea + grossissementGlobal*currentTraj.listePts.get(i).x;
      posY = y0DrawingArea - grossissementGlobal*currentTraj.listePts.get(i).y;
      // rotation du projectile
      AffineTransform tr = new AffineTransform();
      tr.translate(posX-rayonProj, posY-rayonProj);
      tr.rotate(Math.toRadians(10*i), rayonProj, rayonProj);
      g_buff.drawImage(imgProj, tr, this);
      // sans rotation du projectile
      //g_buff.drawImage(imgProj, (int) posX-rayonProj, (int) posY-rayonProj, this);
    }
       	// Affichage du drapeau
    if (currentIndex == currentTraj.listePts.size()-1){ //si nous sommes au derniers point, on affiche le drapeau
      drawDrapeau(posX);
    }
  }

    // Affichage des traj non courantes
  public void drawListTraj(){
   if(!listeTraj.isEmpty()){
     for(Traj uneTraj : listeTraj){
       			// format des lignes
        g_buff.setColor(uneTraj.couleur);
        g_buff.setStroke(new BasicStroke(3));
         			// premier point    
        double x0 = x0DrawingArea + grossissementGlobal*uneTraj.listePts.get(0).x;
        double y0 = y0DrawingArea - grossissementGlobal*uneTraj.listePts.get(0).y;	
        Point ptAvant = new Point (x0, y0);
       			// dessin de la trajectoire
        for(Point pt : uneTraj.listePts){
         				// position du point courant
           double x = x0DrawingArea + grossissementGlobal*pt.x;
           double y = y0DrawingArea - grossissementGlobal*pt.y;
           				// dessin de la ligne
           g_buff.drawLine((int)ptAvant.x, (int)ptAvant.y, (int)x, (int)y);
    					// mémorisation du point
           ptAvant = new Point (x, y);		
        }
      }
    }   	
  }

  public void drawBackGround(){
    	// image d'arrière plan		
    g_buff.drawImage(arrPlan_Scaled,0,0,this);

    g_buff.setColor(Color.black);
		g_buff.setStroke(new BasicStroke(3)); // la barre d'échelle fait 50 pixels de large
		g_buff.setFont(new Font("Arial",Font.BOLD,18));

		/* --- affichage de la position --- */
		int textLength = (int)g_buff.getFontMetrics().getStringBounds(textPosition, g_buff).getWidth();
    int startText = 400 - textLength/2;
    g_buff.drawString(textPosition, startText, 480);

    /* --- affichage de l'origine --- */
    g_buff.drawLine(x0DrawingArea-5, y0DrawingArea, x0DrawingArea+5, y0DrawingArea);
    g_buff.drawLine(x0DrawingArea, y0DrawingArea-5, x0DrawingArea, y0DrawingArea+5);
    g_buff.drawString("O", x0DrawingArea-15, y0DrawingArea-15);

    /* --- affichage de l'echelle --- */
    g_buff.drawLine(700,480,750,480);
    g_buff.drawLine(700,475,700,485);
    g_buff.drawLine(750,475,750,485);

   		// valeur de l'échelle
    double valeurEchelle = 50*echelleGlobale;
    String texteEchelle = String.format("%.2f", valeurEchelle)+" m";

   		// affichage de la valeur
    int stringLength = (int)g_buff.getFontMetrics().getStringBounds(texteEchelle, g_buff).getWidth();
    int start = 725 - stringLength/2;
    g_buff.drawString(texteEchelle, start, 460);

   		// afficher le lanceur à l'échelle si le grossisement le permet
   		if (grossissementGlobal>grossMinHomme){
			int margeX = 30;	// nombre de pixels de marge selon les x
			int margeY = 90;	// nombre de pixels de marge selon les y
			int hauteurHomme = imgHomme.getHeight(null);
			g_buff.drawImage(imgHomme, x0DrawingArea, y0DrawingArea-hauteurHomme, this);
		}
  }
  //affichage du drapeau si le milieu n'est pas liquide
  public void drawDrapeau(double posX){
  	if (!currentTraj.milieuLiquide){
	  	int largeurDrapeau = drapeauScaled.getWidth(null);
	    int hauteurDrapeau = drapeauScaled.getHeight(null);
	    g_buff.drawImage(drapeauScaled, (int) (posX-largeurDrapeau/2.0), (int) (y0DrawingArea-hauteurDrapeau), this);
	 }
  }

    // met à l'échelle l'image de l'homme et lui donne une taille minimum dans le cas où c'est trop dézoomé
 public void setImagesScaled(){
	  if(grossissementGlobal<grossMinProjDrapeau){
  		if(erreurGrossissement){
  			JOptionPane.showMessageDialog(this,"Attention, pass\u00e9 une certaine valeur de grossissement, les projectiles ne sont plus a l'echelle");
  			erreurGrossissement=false;
  		}
  		drapeauScaled = drapeau.getScaledInstance((int) grossMinProjDrapeau, (int) grossMinProjDrapeau, Image.SCALE_DEFAULT);
  		imgProj= currentTraj.proj.img.getScaledInstance((int)(6*grossMinProjDrapeau*currentTraj.proj.demiLongueur), (int)(6*grossMinProjDrapeau*currentTraj.proj.demiLongueur), Image.SCALE_DEFAULT);
  	}else{
  		drapeauScaled = drapeau.getScaledInstance(grossissementGlobal.intValue(), grossissementGlobal.intValue(), Image.SCALE_DEFAULT);
  		imgProj= currentTraj.proj.img.getScaledInstance((int)(6*grossissementGlobal*currentTraj.proj.demiLongueur), (int)(6*grossissementGlobal*currentTraj.proj.demiLongueur), Image.SCALE_DEFAULT);
  	}
  	if(grossissementGlobal>grossMinHomme){
  		imgHomme= new ImageIcon(hommePath).getImage().getScaledInstance((int)(1.5*grossissementGlobal), (int)(2*grossissementGlobal), Image.SCALE_DEFAULT);
  	}
}

  public String getTexteTitre(){
    String texteTitre = "Trajectoire de "+currentTraj.proj.nom+currentTraj.lieu.nom;
		if(currentTraj.frottements){
			texteTitre += " avec frottements";
		}else{
			texteTitre += " sans frottement";
		}
		texteTitre += " avec un angle initial " +currentTraj.alpha_deg+ " degr\u00e9s et une vitesse initiale de "+currentTraj.v0+" m/s";
    return texteTitre;
  }

	public void effacerTraj(){
		// effacer les autres courbes
		listeTraj.clear();
		// mise à jour de l'échelle
		grossissementGlobal = currentTraj.grossissement;
		echelleGlobale = currentTraj.echelle;
		// mise à jour des images
		setImagesScaled();
		// mise à jour de l'affichage
		repaint();
	}

	public void startChrono(){
		//si on a fini l'animation, on ré-anime !
		if(currentIndex == currentTraj.listePts.size()-1){ 
			currentIndex = 0;
		}
		mt.start();
	}

	public void stopChrono(){
		mt.stop();
	}


	public void actionPerformed (ActionEvent e){
		// stoppe le chrono si on arrive à la fin de la traj
		if(currentIndex == currentTraj.listePts.size()-1){
			stopChrono();
		}else{
			currentIndex++;
			repaint();	
		}
	}

	public void keyTyped(KeyEvent ke){
	}
  public void keyPressed(KeyEvent ke) {
  }  
  public void keyReleased(KeyEvent ke){
    if(ke.getKeyCode() == KeyEvent.VK_SPACE){
      // si le timer run on le stoppe et inversement
      if(mt.isRunning()){ 
         stopChrono();
      }else{
          startChrono();
      }
	}
      if(ke.getKeyCode()==KeyEvent.VK_ENTER){
		 fenetreParent.run();
	 }
  }
  public void mouseClicked(MouseEvent e) {  
    String xClick = String.format("%.2f", echelleGlobale*(e.getX()-x0DrawingArea));
    String yClick = String.format("%.2f", echelleGlobale*(y0DrawingArea-e.getY()));
    textPosition = "x = "+xClick+" m ; y = "+yClick+" m";
    // repeindre la fenetre en affichant la position
    // on ne veut pas recommencer l'animation
    animation = false;
    repaint();
  }

  public void mouseEntered(MouseEvent e) {  
  }  
  public void mouseExited(MouseEvent e) {  
  }  
  public void mousePressed(MouseEvent e) {  
  }  
  public void mouseReleased(MouseEvent e) {  
  }

  public void windowActivated(WindowEvent arg0) {  
  }  
  public void windowClosed(WindowEvent arg0) {  
  }  
  public void windowClosing(WindowEvent arg0) {  
    fenetreParent.updateOpenedWindows(currentTraj.lieu);
  }  
  public void windowDeactivated(WindowEvent arg0) {  
  }  
  public void windowDeiconified(WindowEvent arg0) {  
  }  
  public void windowIconified(WindowEvent arg0) {  
  }  
  public void windowOpened(WindowEvent arg0) {  
  }  
}
