package controller;

import controller.CommandController.CmdEventHandler;
import fr.dgac.ivy.*;
import sra.interpreter.Interpreter;
import sra.interpreter.ObjType;
import sra.interpreter.Type;

import java.awt.*;

public class IvyController {
    private Ivy busIvy;
    CmdEventHandler cmdEventHandler;

    public IvyController(CmdEventHandler cmdEventHandler) {
        this.cmdEventHandler = cmdEventHandler;
        busIvy = new Ivy("CommandController", null, null);
    }

    public void start() {
        try {
            busIvy.start("127.255.255.255:2010");
            System.out.println("Ivy controller started");
            busIvy.bindMsg("^sra5 Parsed=(.*) Confidence=.*", new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    try {
                        sraListener(args[0]);                        
                    } catch (Exception e) {
                        System.out.println("Erreur message");
                        e.printStackTrace();
                    }
                }
            });

            busIvy.bindMsg("^palette (.*)", new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    try {
                        paletteListener(args[0]);                        
                    } catch (Exception e) {
                        System.out.println("Erreur message");
                        e.printStackTrace();
                    }
                }
            });

            busIvy.bindMsg("^ICAR Gesture=(.*)", new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    try {
                        icarListener(args[0]);                        
                    } catch (Exception e) {
                        System.out.println("Erreur message");
                        e.printStackTrace();
                    }
                }
            });

        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        busIvy.stop();
    }

    public void sendToPalette(String msg) {
        try {
            busIvy.sendMsg("cmdController send=" + msg);
        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    private void sraListener(String msg) {
        System.out.println("sra : " + msg);
        msg = msg.split(";")[0];
        if (!msg.equals("ici") && !msg.equals("ca") && !msg.equals("ce") && !msg.equals("pipette")) {
            ObjType cmd = Interpreter.getType(msg);
            cmdEventHandler.invoke(cmd);
        }

    }

    private void paletteListener(String msg) {
        System.out.println("palette : " + msg);
        String[] args = msg.split(";");

        if(args[0].equals("pipette")) {
            ObjType cmd = new ObjType();
            cmd.setType(Type.COULEUR);
            args = args[1].split(",");
            cmd.setObj(new Color((int)Double.parseDouble(args[0]), (int)Double.parseDouble(args[1]), (int)Double.parseDouble(args[2])));
            cmdEventHandler.invoke(cmd);
            return;
        }


        ObjType cmd = Interpreter.getType(args[0]);
        cmd.setObj(new Point((int)Double.parseDouble(args[1].split(",")[0]), (int)Double.parseDouble(args[1].split(",")[1])));
        cmdEventHandler.invoke(cmd);

        if(cmd.getType() == Type.POINTAGE) {
            cmd = Interpreter.getType(args[2]);
            cmdEventHandler.invoke(cmd);

            if(args.length > 3) {
                args = args[3].split(",");
                cmd.setType(Type.COULEUR);
                cmd.setObj(new Color((int)Double.parseDouble(args[0]), (int)Double.parseDouble(args[1]), (int)Double.parseDouble(args[2])));
                cmdEventHandler.invoke(cmd);
            }
        }
    }

    private void icarListener(String msg) {
        System.out.println("icar : " + msg);
        ObjType cmd = Interpreter.getType(msg);
        cmdEventHandler.invoke(cmd);
    }
}
