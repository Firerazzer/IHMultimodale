package controller;

import controller.MainController.CmdCompleteEventHandler;
import sra.interpreter.*;

import java.awt.*;

public class CommandController {
    public interface CmdEventHandler {
        public void invoke(ObjType cmd);
    }

    public static class ActionComplete {
        public Action act = Action.NONE;
        public Forme forme = Forme.NONE;
        public Color color = null;
        public Point pointStart = null;
        public Point pointEnd = null;

        public ActionComplete(){
            act = Action.NONE;
            forme = Forme.NONE;
            color = null;
            pointStart = null;
            pointEnd = null;
        }

        @Override
        public String toString() {
            String toString = act.toString();
            toString += ";" + forme;
            toString += ";" + (color != null ? (color.getRed() + "," + color.getGreen() + "," + color.getBlue())
                    : ("null"));
            toString += ";" + (pointStart != null ? (pointStart.getX() + "," + pointStart.getY()) : ("null"));
            toString += ";" + (pointEnd != null ? (pointEnd.getX() + "," + pointEnd.getY()) : ("null"));
            return toString;
        }

        public static ActionComplete parseActionComplete(String s) {
            ActionComplete act = new ActionComplete();
            return act;
        }
    }

    private IvyController ivyController;
    private CmdCompleteEventHandler cmdCompleteEventHandler;
    private ActionComplete act;

    public CommandController(CmdCompleteEventHandler cmdCompleteEventHandler) {
        this.cmdCompleteEventHandler = cmdCompleteEventHandler;
        this.act = new ActionComplete();
    }

    public void start(IvyController ivyController) {
        this.ivyController = ivyController;
        this.ivyController.start();
    }

    public void stop() {
        this.ivyController.stop();
    }

    public void cmdEventHandler(ObjType cmd) {
        System.out.println("cmd : " + cmd.getType());
        if (cmd.getType().equals(Type.LOCALISATION) || cmd.getType().equals(Type.POINTAGE))
            System.out.println(cmd.getObj());
        majCmd(cmd);
    }

    private void majCmd(ObjType cmd) {
        if(act == null) act = new ActionComplete();
        switch (cmd.getType()) {
            case ACTION:
                this.act.act = (Action) cmd.getObj();
                break;

            case FORME:
                this.act.forme = (Forme) cmd.getObj();
                break;

            case COULEUR:
                this.act.color = (Color) cmd.getObj();
                break;

            case LOCALISATION:
                this.act.pointEnd = (Point) cmd.getObj();
                break;

            case POINTAGE:
                this.act.pointStart = (Point) cmd.getObj();
                break;

            case NONE:
                throw new IllegalArgumentException("Command inconnu");
        }
        isCmdComplete();
    }

    private void isCmdComplete() {
        boolean isComplete;
        switch(act.act) {
            case CREER:
                isComplete = !act.forme.equals(Forme.NONE) && act.pointEnd != null;
                break;

            case DEPLACER:
                isComplete = act.pointStart != null && act.pointEnd != null;
                break;

            case SUPPRIMER:
                isComplete = act.pointStart != null;
                break;

            default:
                isComplete = false;
                break;
        }
        if(isComplete) {
            cmdCompleteEventHandler.invoke(this.act);
            this.act = new ActionComplete();
        }
    }
}
