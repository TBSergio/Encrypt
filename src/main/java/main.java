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

    private static int choice=-1;
    private static boolean Flag=false;
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args)
    {
        LogicController.clearConsole("");
        InputStream in;

        while(!Flag) {
            LogicController.clearConsole("");

            printRequest();

            try {
                choice = input.nextInt();
            } catch (InputMismatchException e) {
                choice = 999;
                input.nextLine();//psuedo "flush" for input stream, without it inputstream keeps reading previous charecter - needs further investigation.
            }

            switch (choice) {
                case 0:
                    Flag = true;
                    break;
                case 1:
                    LogicController.clearConsole("");
                    in = LogicController.checkFilePath("");
                    if(in != null) {
                        EncryptController en = new EncryptController();
                        en.Encrypt(in);
                    }
                    break;
                case 2:
                    LogicController.clearConsole("");
                    in = LogicController.checkFilePath("");
                    if(in != null) {
                        DecryptController de = new DecryptController();
                        de.Decrypt(in);
                    }
                    break;
            }
        }
    }
    public static void printRequest() {
        if(choice == 999)
            System.out.print("Previous Input Incorrect - Please Select a Valid Action:\n");
        else
            System.out.print("Please Select An Action To Perform:\n");

        System.out.println("\n1.Encryption\n2.Decryption\n\n0.Exit Application");
        System.out.print("\nSelected Action:");
    }


}
