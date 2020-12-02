import java.awt.*;

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
