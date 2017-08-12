package Controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

import static java.lang.System.exit;

/**
 * Created by Sergei on 3/6/2017.
 */
public class LogicController {

    private static boolean Flag = false;
    private static String fileName = null;
    private static String filePath = null;
    private static Scanner hold = new Scanner(System.in);
    public static final int sigLen = 10;

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
                LogicController.clearConsole("");
                System.out.println("Invalid Path to File or File Does'nt Exist!\nPlease Press Enter to continue...");
                LogicController.hold.nextLine();
            }
        }
        return temp;
    }


    public static byte[] getBytesFromInputStream(InputStream is) throws IOException
    {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
        {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1;)
                os.write(buffer, 0, len);

            os.flush();

            return os.toByteArray();
        }
    }

    public static String getFileName() {
        String[] fileNameParts = fileName.split(Pattern.quote("."),2);
        return fileNameParts[0];
    }

    public static String getFileType() {
        String[] fileNameParts = fileName.split(Pattern.quote("."),2);
        return fileNameParts[1];
    }
    public static String getFilePath() {return filePath;}

}
