package Controllers;

import java.io.IOException;

/**
 * Created by Sergei on 3/6/2017.
 */
public class LogicController {

    public final static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows"))
                Runtime.getRuntime().exec("cmd /c cls");
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException ex) {
            System.out.println("Sorry - Could'nt clear console!\n");
        }
    }
}
