package controller;

import java.awt.Color;
import java.util.Scanner;

import controller.CommandController.ActionComplete;

public class MainController {
    /**
     * CmdCompleteEventHandler
     */
    public interface CmdCompleteEventHandler {
        public void invoke(ActionComplete act);
    }

    private IvyController ivyController;
    private CommandController commandController;

    public MainController() {
        System.out.println(Color.BLUE);
        commandController = new CommandController(act -> cmdCompleteEventHandler(act));
        ivyController = new IvyController(cmd -> commandController.cmdEventHandler(cmd));
        commandController.start(ivyController);
    }

    public void stop() {
        commandController.stop();
    }

    public void cmdCompleteEventHandler(ActionComplete act) {
        System.out.println("commande complete :\n" + act);
        ivyController.sendToPalette(act.toString());
    }

    public static void main(String[] args) {
        MainController mc = new MainController();
        Scanner sc = new Scanner(System.in);
        System.out.println("Press enter to continue");
        sc.nextLine();
        mc.stop();
        sc.close();
    }
}

