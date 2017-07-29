package Controllers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Sergei on 3/6/2017.
 */
public class EncryptController {

    private Random rng = new Random();
    Scanner hold = new Scanner(System.in);
    private byte key;
    private byte[] data;

    public int Encrypt(InputStream in) {

        key = (byte) (rng.nextInt()%128);//working with bytes ranging from -128 to 127
        System.out.println("Starting Encryption Process - Encryption Key:"+key+"!\nPress Enter to continue...");
        hold.nextLine();
        LogicController.clearConsole("");
        try {//apache IOUtils - reads and buffer an input stream - can throw io exception that is taken care of here
            data = IOUtils.toByteArray(in);
        }
        catch(IOException e)
        {
            System.out.println("[Critical Error]Failed to Load data from file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
            return 1;//exit with error code 1 - failed read (in theory we should never reach her due to previous tests
        }

        SelectEncryptionType();


        System.out.println("Encryption Successfull!\nPress Enter to continue...");
        hold.nextLine();
        return 0;//correct exit from the encrypt
    }

    public void SelectEncryptionType() {

        boolean exitFlag = false;
        int choice = -1;

        while(!exitFlag) {
            LogicController.clearConsole("");
            System.out.println("Please Select The Type Of Encryption:\nWith The Encryption Key:"+key+"\n\n1.Ceasar Encryption\n2.XOR Encryption\n3.Change Encryption Key\n\n0.Return To Menu");
            System.out.print("\nSelected Action:");
            try {
                choice = hold.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input!");
            }
            switch(choice){
                case 1:
                    EncryptCaesar();
                    exitFlag = true;
                    break;
                case 2:
                    exitFlag = true;
                    break;
                case 0:
                    exitFlag = true;
                    break;
            }
        }

    }

    private void EncryptCaesar(){//return is if the method succeeded
        byte[] output = new byte[data.length];
        for(int i =0;i<data.length;i++){
            int temp = (int)data[i] + (int)key;
            if(temp > Byte.MAX_VALUE){//deal with overflow
                temp -= Byte.MAX_VALUE+1;//Takin into consideration 0 , thats why +1
                temp += Byte.MIN_VALUE;
            }
        output[i] = (byte)temp;
        }
        try {
            FileUtils.writeByteArrayToFile(new File(LogicController.getFilePath()+"/"+getFileNameNoType()+".encrypted"), output);
        }
        catch (IOException e) {
            System.out.println("[Critical Error]Failed to create encrypted file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
        }

    }

    private String getFileNameNoType() {
        String[] fileNameParts = LogicController.getFileName().split(Pattern.quote("."),2);
        return fileNameParts[0];
    }

}
