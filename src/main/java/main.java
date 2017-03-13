import Controllers.DecryptController;
import Controllers.EncryptController;
import Controllers.LogicController;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by Sergei on 3/6/2017.
 */
public class main{

    private static int choice=999;
    private static boolean Flag=false;

    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args)
    {
        // while(choice!=0) //loop on main menu - clearConsole will NOT clear the console inside the IDE
        //{
        LogicController logic = new LogicController();
        logic.clearConsole("");
        InputStream in;
        while(!Flag) {
            logic.clearConsole("");
            System.out.println("Please Select An Action To Perform:\n\n1.Encryption\n2.Decryption\n\n0.Exit Applicatin");
            System.out.print("\nSelected Action:");
            try {
                choice = input.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input!");
                choice = 999;
            }
            switch (choice) {
                case 0:
                    Flag = true;
                    break;
                case 1:
                    logic.clearConsole("");
                    in = logic.checkFilePath("");
                    if(in != null) {
                        EncryptController en = new EncryptController();
                        en.Encrypt(in);
                    }
                    choice = 999;
                    break;
                case 2:
                    logic.clearConsole("");
                    in = logic.checkFilePath("");
                    if(in != null) {
                        DecryptController de = new DecryptController();
                        de.Decrypt(in);
                    }
                    choice = 999;
                    break;
                default:
                    break;
            }
        }
        //  }
    }
}
