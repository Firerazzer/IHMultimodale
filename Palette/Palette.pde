/*
 * Palette Graphique - prélude au projet multimodal 3A SRI
 * 3 objets gérés : cercle, rectangle(carré) et triangle
 * (c) 05/11/2019
 * Dernière révision : 28/04/2020
 */
 
import java.awt.Point;

ArrayList<Forme> formes; // liste de formes stockées
FSM mae; // Finite Sate Machine
int indice_forme;
PImage sketch_icon;
Ivy busIvy;

void setup() {
  size(800,600);
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

void draw() {
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
void affiche() {
  background(255);
  /* afficher tous les objets */
  for (int i=0;i<formes.size();i++) // on affiche les objets de la liste
    (formes.get(i)).update();
}

void mousePressed() { // sur l'événement clic
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


void keyPressed() {
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


void initIvy() {
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


void sraListener(String cmd){
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
          i = formes.size();
        }
      }
		}

		if(msg.equals("ce")) {
			Point p = new Point(mouseX,mouseY);
      for (int i=0;i<formes.size();i++) { // we're trying every object in the list
        Forme f = formes.get(i);
        if (f.isClicked(p) && (f.getClass().toString().split("\\$")[1]).toLowerCase().equals(cmd.split(";")[1])) {
          sendMessage("ca;" + p.getX() + "," + p.getY() + ";" + (f.getClass().toString().split("\\$")[1]).toLowerCase() + ";" + red(f.getColor()) + "," + green(f.getColor()) + "," + blue(f.getColor()));
          i = formes.size();
        }
      }
		}

		if(msg.equals("pipette")) {
			sendMessage("pipette;" + red(get(mouseX,mouseY)) + "," + green(get(mouseX,mouseY)) + "," + blue(get(mouseX,mouseY)));
		}
}


void cmdListener(String cmd){
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

void sendMessage(String msg) {
	try {
        busIvy.sendMsg("palette " + msg);
    } catch (IvyException e) {
        e.printStackTrace();
    }
}


void creerForme(ActionComplete act) {
  Point p = act.pointEnd;
  color c = color(127);
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


void deplacerForme(ActionComplete act) {
  for (int i=0;i<formes.size();i++) { // we're trying every object in the list
    Forme f = formes.get(i);
    if (f.isClicked(act.pointStart) && (f.getClass().toString().split("\\$")[1]).toLowerCase().equals(act.forme.toString().toLowerCase())) {
      if(act.c != null) {
        color c = color(act.c.getRed(), act.c.getGreen(), act.c.getBlue());
        if(f.getColor() == c) {
          f.setLocation(act.pointEnd);
          i = formes.size();
        }
      }else{ f.setLocation(act.pointEnd); i = formes.size();}
    }
  }
}

void supprimerForme(ActionComplete act) {
  for (int i=0;i<formes.size();i++) { // we're trying every object in the list
    Forme f = formes.get(i);
    if (f.isClicked(act.pointStart) && (f.getClass().toString().split("\\$")[1]).toLowerCase().equals(act.forme.toString().toLowerCase())) {
      if(act.c != null) {
        color c = color(act.c.getRed(), act.c.getGreen(), act.c.getBlue());
        if(f.getColor() == c) {
          formes.remove(i);
          i = formes.size();
        }
      }else { formes.remove(i); i = formes.size();}
    }
  }
}
