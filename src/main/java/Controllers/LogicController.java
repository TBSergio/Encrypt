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
    private static String fileName = null;
    private static String filePath = null;

    public static int clearConsole(String str){
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                return 1;
            }
            else {
                Runtime.getRuntime().exec("clear");
                return 2;
            }
        } catch (IOException ex) {
            System.out.println("Sorry - Could'nt clear console!\n");
            return 0;
        }
        catch (InterruptedException e ) {
            System.out.println("Sorry - Could'nt clear console!\n");
            return 0;
        }
    }

    public static InputStream checkFilePath(String str) {
        Scanner input = new Scanner(System.in);
        Flag = false;
        InputStream temp = null;
        while(!Flag) {
            try {
                System.out.print("Please Insert Valid File Path for Encryption!(Or '*' To Close)\nFile Path:");
                String pathString = input.next();
                System.out.println("----------------------");
                if(!pathString.equals("*"))
                {
                    Path path = Paths.get(pathString);
                    temp = Files.newInputStream(path);
                    fileName = path.getFileName().toString();
                    filePath = path.getParent().toString();
                }
                Flag = true;
            } catch (IOException e) {
                System.out.println("Invalid Path to File or File Does'nt Exist!");
            }
        }
        return temp;
    }

    public static String getFileName() {return fileName;}
    public static String getFilePath() {return filePath;}

}
