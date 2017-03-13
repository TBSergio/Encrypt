package Controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Created by Sergei on 3/6/2017.
 */
public class LogicController {

    private static boolean Flag = false;
    private static InputStream in = null;
    private static String fileName = null;
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
    public static InputStream checkFilePath()
    {
        Scanner input = new Scanner(System.in);
        Flag = false;
        while(!Flag) {
            try {
                System.out.print("Please Insert Valid File Path for Encryption!(Or '*' To Close)\nFile Path:");
                String pathString = input.next();
                System.out.println("----------------------");
                if(pathString.equals("*"))
                    exit(0);
                Path path = Paths.get(pathString);
                in = Files.newInputStream(path);
                fileName = path.getFileName().toString();
                Flag = true;
            } catch (IOException e) {
                System.out.println("Invalid Path to File or File Does'nt Exist!");
            }
        }
        return in;
    }


    public static String getFileName()
    {
        return fileName;
    }

}
