import java.util.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.event.*; 
import java.awt.Font;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*; 
import java.io.*;
import javax.sound.sampled.*;

public class LancerBalle extends JFrame implements ActionListener, ChangeListener, KeyListener, MouseListener{
		
	// attributs IHM
	public JComboBox choixProj;
	public JComboBox choixLieu;
	public JCheckBox choixFrott;
	public JCheckBox affTempsReel;
	public JLabel texteAngle;
	public JLabel texteVitInit;
	public JLabel dispAngle;
	public JTextField editVitesse;
	public JSlider editAngle;
	public JCheckBox choixAnim;
	public JButton stopAnim;
	public JButton startAnim;
	public JButton boutonEntrer;
	public JButton boutonEffacer;
	public JButton boutonCache;
	public JButton boutonCache2;
	public JButton boutonCache3;
	// attributs données
	public Lieu [] tabLieu;
	public Projectile [] tabProj;
	// attributs paramètres traj
	public Projectile projChoisi;// = new Projectile ("d'une balle de tennis", 0.06, 0.033,new ImageIcon("img/tennis.png").getImage());;
	public Lieu lieuChoisi;//= new Lieu ("sur la Terre", 9.81, 0.000018,1.3, new ImageIcon("img/terre.png").getImage()); //(nom, gravité, coefficient de viscosité, masse volumique, image);
	public int indiceProj;// = 0;
	public int indiceLieu;//= 0;
	public double v0;
	public double alpha;
	public double alpha_deg;
	// attributs fenetreTraj
	public FenetreTraj [] tabFenetre = new FenetreTraj [7];
	public FenetreTraj currentFenetre = tabFenetre[0];
	

	public LancerBalle(){
		setTitle("Fenetre des parametres");
		setBounds(0,0, 350, 650);
		setLayout(null);
        setResizable(false);
        setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		// couleurs
		Color mainColor = new Color(4, 139, 154);

		// conteneur principal
		JPanel mainContainer = new JPanel();
		mainContainer.setBounds(0, 0, 350, 650);
		mainContainer.setLayout(null);
		mainContainer.setBackground(new Color(237, 237, 237));

		
		// texte de consignes : "Definissez vos parametres"
		JLabel consignes = new JLabel("Definissez vos parametres");
		consignes.setBounds(10,10,325,40);
		consignes.setFont(new Font("Arial",Font.BOLD,18));
		consignes.setForeground(new Color(4, 139, 154));
		mainContainer.add(consignes);
		
		// JComboBox choix de la balle
		String [] listeProj = {"Balle de tennis", "Balle de ping pong", "Balle de golf","Balle de Petanque","Marteau", "Ballon de rugby" };
		choixProj = new JComboBox(listeProj);
		choixProj.setBounds(10, 60, 300, 50);
		choixProj.setBackground(mainColor);
		choixProj.setForeground(Color.white);
		choixProj.addActionListener(this);
		choixProj.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mainContainer.add(choixProj);
		
		// JComboBox choix du Lieu
		String [] listeLieux = {"Terre", "Eau", "Miel", "Huile", "Mars", "Lune","Everest"};
		choixLieu = new JComboBox(listeLieux);
		choixLieu.setBounds(10, 135, 300, 50);
		choixLieu.setBackground(mainColor);
		choixLieu.setForeground(Color.white);
		choixLieu.addActionListener(this);
		choixLieu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mainContainer.add(choixLieu);
		
		// JCheckBox pour prendre en compte les frottements
		choixFrott = new JCheckBox("       Frottement");
		choixFrott.setBounds(15, 215, 145, 50);
		choixFrott.setBackground(mainColor);
		choixFrott.setForeground(Color.white);
		choixFrott.addActionListener(this);
		choixFrott.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mainContainer.add(choixFrott);

		// JCheckBox pour choisir l'affichage en temps réel
		affTempsReel = new JCheckBox("      Temps r\u00e9el ?");
		affTempsReel.setBounds(175, 215, 145, 50);
		affTempsReel.setBackground(mainColor);
		affTempsReel.setForeground(Color.white);
		affTempsReel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		affTempsReel.addActionListener(this);
		mainContainer.add(affTempsReel);

		// JCheckBox pour prendre en compte l'animation'
		choixAnim = new JCheckBox("       Animation");
		choixAnim.setBounds(15, 295, 145, 50);
		choixAnim.setBackground(mainColor);
		choixAnim.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		choixAnim.setForeground(Color.white);
		choixAnim.addActionListener(this);
		mainContainer.add(choixAnim);

		// JButton pour arreter l'animation'
		stopAnim = new JButton("Stop");
		stopAnim.setBounds(175, 295, 70, 50);
		stopAnim.setBackground(mainColor);
		stopAnim.setForeground(Color.white);
		stopAnim.addActionListener(this);
		stopAnim.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mainContainer.add(stopAnim);

		// JButton pour lancer / recommencer l'animation'
		startAnim = new JButton("Start");
		startAnim.setBounds(250, 295, 70, 50);
		startAnim.setBackground(mainColor);
		startAnim.setForeground(Color.white);
		startAnim.addActionListener(this);
		startAnim.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mainContainer.add(startAnim);

		// légende du JSlider du choix de l'angle
		texteAngle = new JLabel("Angle initial :");
		texteAngle.setBounds(10, 365, 120, 50);
		texteAngle.setFont(new Font("Arial",Font.BOLD,16));
		texteAngle.setForeground(mainColor);
		mainContainer.add(texteAngle);
		
		// JSlider pour le choix de l'angle
		editAngle = new JSlider(-90,90);
		editAngle.setBounds(160, 380, 140, 50);
        editAngle.setFont(new Font("Arial", Font.ITALIC, 15));
		editAngle.setMajorTickSpacing(90);
		editAngle.setMinorTickSpacing(-90);
		editAngle.setPaintTicks(true);
		editAngle.setPaintLabels(true);
		editAngle.addChangeListener(this);
		editAngle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mainContainer.add(editAngle);
		
		// affichage de l'angle courant
		dispAngle = new JLabel("0 \u00b0");
		dispAngle.setFont(new Font("Arial",Font.BOLD,13));
		dispAngle.setBounds(222, 347, 120, 50);
		dispAngle.setForeground(mainColor);
		mainContainer.add(dispAngle);

		// légende du JTextField pour le choix de l'angle
		texteVitInit = new JLabel("Vitesse Initiale :");
		texteVitInit.setBounds(10, 455, 120, 50);
		texteVitInit.setFont(new Font("Arial",Font.BOLD,16));
		texteVitInit.setForeground(mainColor);
		mainContainer.add(texteVitInit);
		
		// JTextField pour le choix de la vitesse initiale
		editVitesse = new JTextField("10");
		editVitesse.setHorizontalAlignment(JTextField.CENTER);
		editVitesse.setBounds(160, 455, 140, 50);
		mainContainer.add(editVitesse);

		
		// bouton entrer pour afficher la trajectoire avec les paramètres sélectionnés
		boutonEntrer = new JButton("Entrer");
		boutonEntrer.setBounds(175, 535, 145, 50);
		boutonEntrer.setBackground(mainColor);
		boutonEntrer.setForeground(Color.white);
		boutonEntrer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		boutonEntrer.addActionListener(this);
		mainContainer.add(boutonEntrer);
		
		// bouton effacer pour effacer les trajectoires
        boutonEffacer = new JButton("Effacer");
		boutonEffacer.setBounds(15, 535, 145, 50);
		boutonEffacer.setBackground(mainColor);
		boutonEffacer.setForeground(Color.white);
		boutonEffacer.addActionListener(this);
		boutonEffacer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mainContainer.add(boutonEffacer);

		// bouton secret à vous de les trouver sans vous aider de la position ;)
        boutonCache = new JButton("");
		boutonCache.setBounds(246, 32, 4, 4);
		boutonCache.setBackground(mainColor);
		boutonCache.setForeground(Color.white);
		boutonCache.addActionListener(this);
		mainContainer.add(boutonCache);

		// bouton secret2
        boutonCache2 = new JButton("");
		boutonCache2.setBounds(251, 32, 4, 4);
		boutonCache2.setBackground(mainColor);
		boutonCache2.setForeground(Color.white);
		boutonCache2.addActionListener(this);
		mainContainer.add(boutonCache2);

		// bouton secret3
        boutonCache3 = new JButton("");
		boutonCache3.setBounds(256, 32, 4, 4);
		boutonCache3.setBackground(mainColor);
		boutonCache3.setForeground(Color.white);
		boutonCache3.addActionListener(this);
		mainContainer.add(boutonCache3);



		//Création des lieux
		tabLieu = new Lieu [7];		
		tabLieu[0] = new Lieu ("sur la Terre", 9.81, 0.000018,1.3, new ImageIcon("img/terre.png").getImage()); //(nom, gravité, coefficient de viscosité, masse volumique, image)
		tabLieu[1] = new Lieu (" dans l'eau", 9.81, 0.001,1000, new ImageIcon("img/eau.png").getImage());
		tabLieu[2] = new Lieu ("dans du miel", 9.81, 8,1400,new ImageIcon("img/Miel.jpg").getImage());
		tabLieu[3] = new Lieu("dans l'huile", 9.81, 0.1,900,new ImageIcon("img/Huile.jpg").getImage());
		tabLieu[4] = new Lieu ("sur Mars", 3.72, 0.0001, 0.02,new ImageIcon("img/Mars.jpg").getImage());//coeff_frott a redéfinir
		tabLieu[5] = new Lieu ("sur la Lune", 1.625, 0.00001,0.00012,new ImageIcon("img/lune.jpg").getImage()); //coeff_frott a redéfinir
		tabLieu[6] = new Lieu ("sur le Mont Everest" , 9.773, 0.000018,1.3,new ImageIcon("img/Everest.jpg").getImage()); 
		lieuChoisi = tabLieu[0]; //par défaut
		
		//Création des projectiles
		tabProj = new Projectile [6];
		tabProj[0] = new Balle ("d'une balle de tennis", 0.06, 0.033,new ImageIcon("img/tennis.png").getImage());
		tabProj[1] = new Balle ("d'une balle de ping-pong", 0.0027, 0.020,new ImageIcon("img/pingpong.png").getImage());
		tabProj[2] = new Balle ("d'une balle de golf", 0.045, 0.021,new ImageIcon("img/golf.png").getImage());
		tabProj[3] = new Balle ("d'une boule de petanque", 0.7, 0.040,new ImageIcon("img/petanque.png").getImage());
		tabProj[4] = new Marteau(0.7,0.08, new ImageIcon("img/marteau.png").getImage());
		tabProj[5] = new BallonRugby(0.45,0.09, new ImageIcon("img/rugby.png").getImage());
		projChoisi = tabProj[0]; //par défaut		
		
		this.add(mainContainer);
		setVisible(true);
	}
	
		
	//vérifie si un String est bien composé de chiffres
	public static boolean isNumeric(String Num) {
		try{
			double d = Double.parseDouble(Num);
		}catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public void run(){
		// assignation des paramètres
		indiceLieu = choixLieu.getSelectedIndex();
		lieuChoisi = tabLieu[indiceLieu];
		indiceProj = choixProj.getSelectedIndex();
		projChoisi = tabProj[indiceProj];
		// traitement des erreurs
		if(editVitesse.getText().isEmpty()){
			JOptionPane.showMessageDialog(this,"Veuillez choisir une vitesse initiale");
			editVitesse.setText("");
		}else if(!isNumeric(editVitesse.getText())){
			JOptionPane.showMessageDialog(this,"Veuillez entrer uniquement des chiffres");
			editVitesse.setText("");
		}else if(Double.parseDouble(editVitesse.getText())>11200){
			JOptionPane.showMessageDialog(this,"Hmm, le projectile sort de l'attraction de la terre (limite : 11,2km/s)");
			editVitesse.setText("");
		}
		else{
			// création de la traj
			v0 = Double.parseDouble(editVitesse.getText());
			Traj newTraj = new Traj(projChoisi, lieuChoisi, choixFrott.isSelected(), alpha_deg, v0);
			// création de la fenetre
			currentFenetre = tabFenetre[indiceLieu];
			if(currentFenetre != null){
				currentFenetre.animation = choixAnim.isSelected();
				currentFenetre.addTraj(newTraj);
				currentFenetre.toFront(); 
			}else{
				currentFenetre = new FenetreTraj(newTraj, choixAnim.isSelected(), this);
				tabFenetre[indiceLieu] = currentFenetre;
				
			}
		}
	}

		
	public void actionPerformed(ActionEvent e){
		// lancement du chrono
		if(e.getSource()==stopAnim && currentFenetre != null){
			currentFenetre.stopChrono();
		}
		// arrêt du chrono
		if(e.getSource()==startAnim && currentFenetre != null){
			currentFenetre.startChrono();
		}

		// easter eggs
		if(e.getSource()==boutonCache && currentFenetre != null){
			currentFenetre.hommePath = "img/charline2.png";
		}
		if(e.getSource()==boutonCache2 && currentFenetre != null){
			currentFenetre.hommePath = "img/yoann1.png";
		}
		if(e.getSource()==boutonCache3 && currentFenetre != null){
			currentFenetre.hommePath = "img/vianney1.png";
		}
		//met a jour choix animation : oui/non
		if(e.getSource() == choixAnim && currentFenetre!= null){
			currentFenetre.animation= choixAnim.isSelected();
		}
			
		// bouton entrer
		if(e.getSource()==boutonEntrer){
			run();
		}

		// effacer traj
		if(e.getSource() == boutonEffacer){
			if(currentFenetre != null){
				currentFenetre.effacerTraj();
			}
		}

		// redonner le focus à la fenetre pour détecter les touches du clavier
		requestFocus();
	}

	public void keyTyped(KeyEvent ke){
	}
    public void keyPressed(KeyEvent ke) {
    }
    
    public void keyReleased(KeyEvent ke){
    	if(ke.getKeyCode() == KeyEvent.VK_SPACE && currentFenetre != null){
    		// si le timer run on le stoppe et inversement
			if(currentFenetre.mt.isRunning()){
				currentFenetre.stopChrono();
			}else{
				currentFenetre.startChrono();
			}
		}
  		if (ke.getKeyCode() == KeyEvent.VK_ENTER){
  			run();
   		}
    }
 

	public void tempsReel (){
		if(affTempsReel.isSelected() && currentFenetre != null){ // fonction temps réel : on met à jour en temps reel la trajectoire lorsqu'on change l'angle du slider
			currentFenetre.addTraj(new Traj(projChoisi, lieuChoisi, choixFrott.isSelected(), alpha_deg, v0));
			currentFenetre.effacerTraj();
			currentFenetre.repaint();
		}
	}
	
	public void stateChanged(ChangeEvent ce){ //methode appelée à chaque mouvement du slider
		dispAngle.setText(""+editAngle.getValue()+"	\u00b0");
		alpha_deg = editAngle.getValue();
		tempsReel();
		requestFocus();
	}

	public void mouseClicked(MouseEvent e) {  
        requestFocus(); 
    }  
    public void mouseEntered(MouseEvent e) {  
    }  
    public void mouseExited(MouseEvent e) {  
    }  
    public void mousePressed(MouseEvent e) {  
    }  
    public void mouseReleased(MouseEvent e) {  
    }

    public void updateOpenedWindows(Lieu l){
    	int i = 0;
    	while(i < tabLieu.length && tabLieu[i] != l){
    		i++;
    	}
    	if(i < tabLieu.length){
    		tabFenetre[i] = null;
    	}else{
    	}
    }

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		LancerBalle simLancer = new LancerBalle();
		File file = new File("Animal-Crossing-Asdek-Remix.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);

		clip.start();
	}
}	
