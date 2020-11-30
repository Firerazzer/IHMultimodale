package controller;

import controller.CommandController.CmdEventHandler;
import fr.dgac.ivy.*;
import sra.interpreter.Interpreter;
import sra.interpreter.ObjType;
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
                    sraListener(args[0]);
                }
            });

            busIvy.bindMsg("^palette Point=(.*) Confidence=.*", new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    paletteListener(args[0]);
                }
            });

            busIvy.bindMsg("^ICAR Gesture=(.*)", new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    icarListener(args[0]);
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
        if (!msg.equals("ici") && !msg.equals("ca")) {
            ObjType cmd = Interpreter.getType(msg);
            cmdEventHandler.invoke(cmd);
        }

    }

    private void paletteListener(String msg) {
        System.out.println("palette : " + msg);
        String[] args = msg.split(";");
        ObjType cmd = Interpreter.getType(args[0]);
        cmd.setObj(new Point(Integer.parseInt(args[1]), Integer.parseInt(args[2])));
        cmdEventHandler.invoke(cmd);
    }

    private void icarListener(String msg) {
        System.out.println("icar : " + msg);
        ObjType cmd = Interpreter.getType(msg);
        cmdEventHandler.invoke(cmd);
    }
}
