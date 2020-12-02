import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.Point; 
import java.awt.*; 

import fr.dgac.ivy.*; 
import fr.dgac.ivy.tools.*; 
import gnu.getopt.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Palette extends PApplet {

/*
 * Palette Graphique - prélude au projet multimodal 3A SRI
 * 3 objets gérés : cercle, rectangle(carré) et triangle
 * (c) 05/11/2019
 * Dernière révision : 28/04/2020
 */
 


ArrayList<Forme> formes; // liste de formes stockées
FSM mae; // Finite Sate Machine
int indice_forme;
PImage sketch_icon;
Ivy busIvy;

public void setup() {
  
  surface.setResizable(true);
  surface.setTitle("Palette multimodale");
  surface.setLocation(20,20);
  sketch_icon = loadImage("Palette.jpg");
  surface.setIcon(sketch_icon);
  formes= new ArrayList(); // nous créons une liste vide
  noStroke();
  mae = FSM.INITIAL;
  indice_forme = -1;

  initIvy();
}

public void draw() {
  background(0);
  //println("MAE : " + mae + " indice forme active ; " + indice_forme);
  switch (mae) {
    case INITIAL:  // Etat INITIAL
      background(255);
      fill(0);
      text("Etat initial (c(ercle)/l(osange)/r(ectangle)/t(riangle) pour créer la forme à la position courante)", 50,50);
      text("m(ove)+ click pour sélectionner un objet et click pour sa nouvelle position", 50,80);
      text("click sur un objet pour changer sa couleur de manière aléatoire", 50,110);
      break;
      
    case AFFICHER_FORMES:  // 
    case DEPLACER_FORMES_SELECTION: 
    case DEPLACER_FORMES_DESTINATION: 
      affiche();
      break;   
      
    default:
      break;
  }  
}

// fonction d'affichage des formes m
public void affiche() {
  background(255);
  /* afficher tous les objets */
  for (int i=0;i<formes.size();i++) // on affiche les objets de la liste
    (formes.get(i)).update();
}

public void mousePressed() { // sur l'événement clic
  Point p = new Point(mouseX,mouseY);
  
  switch (mae) {
    case AFFICHER_FORMES:
      for (int i=0;i<formes.size();i++) { // we're trying every object in the list
        // println((formes.get(i)).isClicked(p));
        if ((formes.get(i)).isClicked(p)) {
          (formes.get(i)).setColor(color(random(0,255),random(0,255),random(0,255)));
        }
      } 
      break;
      
   case DEPLACER_FORMES_SELECTION:
     for (int i=0;i<formes.size();i++) { // we're trying every object in the list        
        if ((formes.get(i)).isClicked(p)) {
          indice_forme = i;
          mae = FSM.DEPLACER_FORMES_DESTINATION;
        }         
     }
     if (indice_forme == -1)
       mae= FSM.AFFICHER_FORMES;
     break;
     
   case DEPLACER_FORMES_DESTINATION:
     if (indice_forme !=-1)
       (formes.get(indice_forme)).setLocation(new Point(mouseX,mouseY));
     indice_forme=-1;
     mae=FSM.AFFICHER_FORMES;
     break;
     
    default:
      break;
  }
}


public void keyPressed() {
  Point p = new Point(mouseX,mouseY);
  switch(key) {
    case 'r':
      Forme f= new Rectangle(p);
      formes.add(f);
      mae=FSM.AFFICHER_FORMES;
      break;
      
    case 'c':
      Forme f2=new Cercle(p);
      formes.add(f2);
      mae=FSM.AFFICHER_FORMES;
      break;
    
    case 't':
      Forme f3=new Triangle(p);
      formes.add(f3);
       mae=FSM.AFFICHER_FORMES;
      break;   
      
    case 'm' : // move
      mae=FSM.DEPLACER_FORMES_SELECTION;
      break;
  }
}


public void initIvy() {
    try {
		busIvy = new Ivy("Palette", null, null);
      busIvy.start("127.255.255.255:2010");
      System.out.println("Ivy Palette started");
      busIvy.bindMsg("^sra5 Parsed=(.*) Confidence=.*", new IvyMessageListener() {
          public void receive(IvyClient client, String[] args) {
              sraListener(args[0]);
          }
      });

      busIvy.bindMsg("^cmdController send=(.*)", new IvyMessageListener() {
          public void receive(IvyClient client, String[] args) {
              cmdListener(args[0]);
          }
      });
  } catch (IvyException e) {
      e.printStackTrace();
  }
}


public void sraListener(String cmd){
	String msg = cmd.split(";")[0];
        if (msg.equals("ici")) {
			String toSend = "ici;" + mouseX + "," + mouseY;
			sendMessage(toSend);
		}

		if(msg.equals("ca")) {
      Point p = new Point(mouseX,mouseY);
	    for (int i=0;i<formes.size();i++) { // we're trying every object in the list
        if ((formes.get(i)).isClicked(p)) {
          Forme f = formes.get(i);
          sendMessage("ca;" + p.getX() + "," + p.getY() + ";" + (f.getClass().toString().split("\\$")[1]).toLowerCase());
        }
      }
		}

		if(msg.equals("ce")) {
			Point p = new Point(mouseX,mouseY);
      for (int i=0;i<formes.size();i++) { // we're trying every object in the list
        Forme f = formes.get(i);
        if (f.isClicked(p) && (f.getClass().toString().split("\\$")[1]).toLowerCase().equals(cmd.split(";")[1])) {
          sendMessage("ca;" + p.getX() + "," + p.getY() + ";" + (f.getClass().toString().split("\\$")[1]).toLowerCase() + ";" + red(f.getColor()) + "," + green(f.getColor()) + "," + blue(f.getColor()));
        }
      }
		}

		if(msg.equals("pipette")) {
			sendMessage("pipette;" + red(get(mouseX,mouseY)) + "," + green(get(mouseX,mouseY)) + "," + blue(get(mouseX,mouseY)));
		}
}


public void cmdListener(String cmd){
	ActionComplete act = ActionComplete.parseActionComplete(cmd);
	println("CMD received : " + act.toString());
	switch(act.act) {
		case CREER :
			creerForme(act);
      break;
      
		case DEPLACER :
			deplacerForme(act);
      break;

    case SUPPRIMER :
      supprimerForme(act);
      break;
	}
  mae=FSM.AFFICHER_FORMES;
}

public void sendMessage(String msg) {
	try {
        busIvy.sendMsg("palette " + msg);
    } catch (IvyException e) {
        e.printStackTrace();
    }
}


public void creerForme(ActionComplete act) {
  Point p = act.pointEnd;
  int c = color(127);
  if(act.c != null)
        c = color(act.c.getRed(), act.c.getGreen(), act.c.getBlue());
        
	switch(act.forme) {
		case RECTANGLE:
			Forme f= new Rectangle(p);
			f.setColor(c);
			formes.add(f);
			mae=FSM.AFFICHER_FORMES;
			break;
		
		case CERCLE:
			Forme f2=new Cercle(p);
      f2.setColor(c);
			formes.add(f2);
			mae=FSM.AFFICHER_FORMES;
			break;
		
		case TRIANGLE:
			Forme f3=new Triangle(p);
      f3.setColor(c);
			formes.add(f3);
			mae=FSM.AFFICHER_FORMES;
			break; 
  }
}


public void deplacerForme(ActionComplete act) {
  for (int i=0;i<formes.size();i++) { // we're trying every object in the list
    Forme f = formes.get(i);
    if (f.isClicked(act.pointStart) && (f.getClass().toString().split("\\$")[1]).toLowerCase().equals(act.forme.toString().toLowerCase())) {
      if(act.c != null) {
        int c = color(act.c.getRed(), act.c.getGreen(), act.c.getBlue());
        if(f.getColor() == c) {
          f.setLocation(act.pointEnd);
        }
      }else f.setLocation(act.pointEnd);
    }
  }
}

public void supprimerForme(ActionComplete act) {
  for (int i=0;i<formes.size();i++) { // we're trying every object in the list
    Forme f = formes.get(i);
    if (f.isClicked(act.pointStart) && (f.getClass().toString().split("\\$")[1]).toLowerCase().equals(act.forme.toString().toLowerCase())) {
      if(act.c != null) {
        int c = color(act.c.getRed(), act.c.getGreen(), act.c.getBlue());
        if(f.getColor() == c) {
          formes.remove(i);
        }
      }else formes.remove(i);
    }
  }
}

enum Action {
	NONE,
	CREER,
	SUPPRIMER,
	DEPLACER;

	public static Action parseAction(String s) {
		switch (s) {
			case "CREER" :
				return Action.CREER;
				
			case "SUPPRIMER" :
				return Action.SUPPRIMER;
				
			case "DEPLACER" :
				return Action.DEPLACER;

			default :
				return Action.NONE;
		}
	}
}


public static class ActionComplete {
    public Action act = Action.NONE;
    public Formes forme = Formes.NONE;
    public Color c = null;
    public Point pointStart = null;
    public Point pointEnd = null;

    @Override
    public String toString() {
        String toString = act.toString();
            toString += ";" + forme;
            toString += ";" + (c != null ? (c.getRed() + "," + c.getGreen() + "," + c.getBlue()) : ("null"));
            toString += ";" + (pointStart != null ? (pointStart.getX() + "," + pointStart.getY()) : ("null"));
            toString += ";" + (pointEnd != null ? (pointEnd.getX() + "," + pointEnd.getY()) : ("null"));
            return toString;
    }

    public static ActionComplete parseActionComplete(String s) {
        ActionComplete act = new ActionComplete();
        String[] vals = s.split(";");
        act.act     = Action.parseAction(vals[0]);
        act.forme   = Formes.parseFormes(vals[1]);

        if(vals[2].equals("null"))
            act.c   = null;
        else {
            String[] args = vals[2].split(",");
            act.c   = new Color((int)Double.parseDouble(args[0]), (int)Double.parseDouble(args[1]), (int)Double.parseDouble(args[2]));
        }

        if(vals[3].equals("null"))
            act.pointStart   = null;
        else {
            String[] args = vals[3].split(",");
            act.pointStart  = new Point((int)Double.parseDouble(args[0]), (int)Double.parseDouble(args[1]));
        }

        if(vals[4].equals("null"))
            act.pointEnd   = null;
        else {
            String[] args = vals[4].split(",");
            act.pointEnd    = new Point((int)Double.parseDouble(args[0]), (int)Double.parseDouble(args[1]));
        }
        return act;
    }
}
/*
 * Classe Cercle
 */ 
 
public class Cercle extends Forme {
  
  int rayon;
  
  public Cercle(Point p) {
    super(p);
    this.rayon=80;
  }
   
  public void update() {
    fill(this.c);
    circle((int) this.origin.getX(),(int) this.origin.getY(),this.rayon);
  }  
   
  public boolean isClicked(Point p) {
    // vérifier que le cercle est cliqué
   PVector OM= new PVector( (int) (p.getX() - this.origin.getX()),(int) (p.getY() - this.origin.getY())); 
   if (OM.mag() <= this.rayon/2)
     return(true);
   else 
     return(false);
  }
  
  protected double perimetre() {
    return(2*PI*this.rayon);
  }
  
  protected double aire(){
    return(PI*this.rayon*this.rayon);
  }
}
/*
 * Enumération de a Machine à Etats (Finite State Machine)
 *
 *
 */
 
public enum FSM {
  INITIAL, /* Etat Initial */ 
  AFFICHER_FORMES, 
  DEPLACER_FORMES_SELECTION,
  DEPLACER_FORMES_DESTINATION
}
/*****
 * Création d'un nouvelle classe objet : Forme (Cercle, Rectangle, Triangle
 * 
 * Date dernière modification : 28/10/2019
 */

abstract class Forme {
 Point origin;
 int c;
 
 Forme(Point p) {
   this.origin=p;
   this.c = color(127);
 }
 
 public void setColor(int c) {
   this.c=c;
 }
 
 public int getColor(){
   return(this.c);
 }
 
 public abstract void update();
 
 public Point getLocation() {
   return(this.origin);
 }
 
 public void setLocation(Point p) {
   this.origin = p;
 }
 
 public abstract boolean isClicked(Point p);
 
 // Calcul de la distance entre 2 points
 protected double distance(Point A, Point B) {
    PVector AB = new PVector( (int) (B.getX() - A.getX()),(int) (B.getY() - A.getY())); 
    return(AB.mag());
 }
 
 protected abstract double perimetre();
 protected abstract double aire();
}

enum Formes{
	NONE,
	RECTANGLE,
	CERCLE,
	TRIANGLE;

	public static Formes parseFormes(String s) {
		switch (s) {
			case "RECTANGLE" :
				return Formes.RECTANGLE;
				
			case "CERCLE" :
				return Formes.CERCLE;
				
			case "TRIANGLE" :
				return Formes.TRIANGLE;

			default :
				return Formes.NONE;
		}
	}
}
/*
 * Classe Rectangle
 */ 
 
public class Rectangle extends Forme {
  
  int longueur;
  
  public Rectangle(Point p) {
    super(p);
    this.longueur=60;
  }
   
  public void update() {
    fill(this.c);
    square((int) this.origin.getX(),(int) this.origin.getY(),this.longueur);
  }  
  
  public boolean isClicked(Point p) {
    int x= (int) p.getX();
    int y= (int) p.getY();
    int x0 = (int) this.origin.getX();
    int y0 = (int) this.origin.getY();
    
    // vérifier que le rectangle est cliqué
    if ((x>x0) && (x<x0+this.longueur) && (y>y0) && (y<y0+this.longueur))
      return(true);
    else  
      return(false);
  }
  
  // Calcul du périmètre du carré
  protected double perimetre() {
    return(this.longueur*4);
  }
  
  protected double aire(){
    return(this.longueur*this.longueur);
  }
}
/*
 * Classe Triangle
 */ 
 
public class Triangle extends Forme {
  Point A, B,C;
  public Triangle(Point p) {
    super(p);
    // placement des points
    A = new Point();    
    A.setLocation(p);
    B = new Point();    
    B.setLocation(A);
    C = new Point();    
    C.setLocation(A);
    B.translate(40,60);
    C.translate(-40,60);
  }
  
    public void setLocation(Point p) {
      super.setLocation(p);
      // redéfinition de l'emplacement des points
      A.setLocation(p);   
      B.setLocation(A);  
      C.setLocation(A);
      B.translate(40,60);
      C.translate(-40,60);   
  }
  
  public void update() {
    fill(this.c);
    triangle((float) A.getX(), (float) A.getY(), (float) B.getX(), (float) B.getY(), (float) C.getX(), (float) C.getY());
  }  
  
  public boolean isClicked(Point M) {
    // vérifier que le triangle est cliqué
    
    PVector AB= new PVector( (int) (B.getX() - A.getX()),(int) (B.getY() - A.getY())); 
    PVector AC= new PVector( (int) (C.getX() - A.getX()),(int) (C.getY() - A.getY())); 
    PVector AM= new PVector( (int) (M.getX() - A.getX()),(int) (M.getY() - A.getY())); 
    
    PVector BA= new PVector( (int) (A.getX() - B.getX()),(int) (A.getY() - B.getY())); 
    PVector BC= new PVector( (int) (C.getX() - B.getX()),(int) (C.getY() - B.getY())); 
    PVector BM= new PVector( (int) (M.getX() - B.getX()),(int) (M.getY() - B.getY())); 
    
    PVector CA= new PVector( (int) (A.getX() - C.getX()),(int) (A.getY() - C.getY())); 
    PVector CB= new PVector( (int) (B.getX() - C.getX()),(int) (B.getY() - C.getY())); 
    PVector CM= new PVector( (int) (M.getX() - C.getX()),(int) (M.getY() - C.getY())); 
    
    if ( ((AB.cross(AM)).dot(AM.cross(AC)) >=0) && ((BA.cross(BM)).dot(BM.cross(BC)) >=0) && ((CA.cross(CM)).dot(CM.cross(CB)) >=0) ) { 
      return(true);
    }
    else
      return(false);
  }
  
  protected double perimetre() {
    //
    PVector AB= new PVector( (int) (B.getX() - A.getX()),(int) (B.getY() - A.getY())); 
    PVector AC= new PVector( (int) (C.getX() - A.getX()),(int) (C.getY() - A.getY())); 
    PVector BC= new PVector( (int) (C.getX() - B.getX()),(int) (C.getY() - B.getY())); 
    
    return( AB.mag()+AC.mag()+BC.mag()); 
  }
   
  // Calcul de l'aire du triangle par la méthode de Héron 
  protected double aire(){
    double s = perimetre()/2;
    double aire = s*(s-distance(B,C))*(s-distance(A,C))*(s-distance(A,B));
    return(sqrt((float) aire));
  }
}
  public void settings() {  size(800,600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Palette" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
