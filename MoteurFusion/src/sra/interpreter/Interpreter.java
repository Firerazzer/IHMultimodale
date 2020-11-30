package sra.interpreter;

import java.awt.*;

public class Interpreter {

  public static ObjType getType(String str) {
    ObjType arg = new ObjType();
    Point pnt = new Point();

    switch (str) {
      case "creer":
        arg.setObj(Action.CREER);
        arg.setType(Type.ACTION);
        break;

      case "supprimer":
        arg.setObj(Action.SUPPRIMER);
        arg.setType(Type.ACTION);
        break;
      case "deplacer":
        arg.setObj(Action.DEPLACER);
        arg.setType(Type.ACTION);
        break;

      case "rectangle":
        arg.setObj(Forme.RECTANGLE);
        arg.setType(Type.FORME);
        break;
      case "cercle":
        arg.setObj(Forme.CERCLE);
        arg.setType(Type.FORME);
        break;
      case "triangle":
        arg.setObj(Forme.TRIANGLE);
        arg.setType(Type.FORME);
        break;

      case "rouge":
        arg.setObj(Color.RED);
        arg.setType(Type.COULEUR);
        break;
      case "vert":
        arg.setObj(Color.GREEN);
        arg.setType(Type.COULEUR);
        break;
      case "bleu":
        arg.setObj(Color.BLUE);
        arg.setType(Type.COULEUR);
        break;
      case "noir":
        arg.setObj(Color.BLACK);
        arg.setType(Type.COULEUR);
        break;
      case "rose":
        arg.setObj(Color.PINK);
        arg.setType(Type.COULEUR);
        break;
      case "jaune":
        arg.setObj(Color.YELLOW);
        arg.setType(Type.COULEUR);
        break;

      case "ici":
        arg.setObj(pnt); // DefPoint
        arg.setType(Type.LOCALISATION);
        break;

      case "ca":
        arg.setObj(pnt); // DefPoint
        arg.setType(Type.POINTAGE);
        break;

      default:
        arg.setObj(""); // DefPoint
        arg.setType(Type.NONE);
        break;
    }
    return arg;
  }
}
