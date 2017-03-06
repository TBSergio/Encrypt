import Controllers.LogicController;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by Sergei on 3/6/2017.
 */
public class main{

    private static int choice=999;
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        // while(choice!=0) //loop on main menu - clearConsole will NOT clear the console inside the IDE
        //{
        LogicController.clearConsole();
        System.out.println("Please Select An Action To Perform:\n-----------------\n1.Encryption\n2.Decryption\n-----------------\n0.Exit Applicatin");
        System.out.println("\nSelected Action:");
        try {
            choice = input.nextInt();
        }
        catch(InputMismatchException e)
        {
            choice = 999;
        }
        switch(choice) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
        //  }
    }
}
