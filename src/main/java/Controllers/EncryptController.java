package Controllers;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.awt.peer.LightweightPeer;
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
    private byte[] signiture = new byte[LogicController.sigLen];//first sigLen bytes of a encrypted file would be save for decryption information - such as original format.

    public int Encrypt(InputStream in) {

        String temp = LogicController.getFileType()+"-xxxxxxxxxxxxx";// - to break between parts of the signiture , x to make sure no data loss due to null charecters.
        signiture = temp.substring(0,LogicController.sigLen).getBytes();
        key = (byte) (rng.nextInt()%128);//working with bytes ranging from -128 to 127
        System.out.println("Starting Encryption Process - Encryption Key:"+key+"!\nPress Enter to continue...");
        hold.nextLine();
        LogicController.clearConsole("");

        try {
            data = LogicController.getBytesFromInputStream(in);
        }
        catch(IOException e)
        {
            System.out.println("[Critical Error]Failed to Load data from file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
            return 1;//exit with error code 1 - failed read (in theory we should never reach her due to previous tests
        }

        SelectEncryptionType();

        System.out.println("Encryption Successfull!\nPress Enter to continue...");
        hold.nextLine();//This wait for enter is passed by because of the previous enter - and the inability to flush the input stream.
        hold.nextLine();
        return 0;//correct exit from the encrypt
    }

    public void SelectEncryptionType() {

        boolean exitFlag = false;
        boolean wflag = false;
        int choice = -1;

        while(!exitFlag) {
            if(!wflag) {
                LogicController.clearConsole("");
                System.out.println("Please Select The Type Of Encryption:\nWith The Encryption Key:" + key + "\n\n1.Ceasar Encryption\n2.XOR Encryption\n3.Multiplication Encryption\n4.Change Encryption Key\n\n0.Return To Menu");
                System.out.print("\nSelected Action:");
                wflag=true;
            }
            try {
                choice = hold.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input!");
                System.out.print("Selected Action:");
            }
            switch(choice){
                case 1:
                    EncryptCaesar();
                    exitFlag = true;
                    break;
                case 2:
                    EncryptXOR();
                    exitFlag = true;
                    break;
                case 0:
                    exitFlag = true;
                    break;
                case 3:
                    try {
                        EncryptMult();
                        exitFlag = true;
                    }
                    catch(IOException e) {
                        LogicController.clearConsole("");
                        System.out.println(e.getMessage()+"\nPlease Change the key before attempting the Encryption!\nPress Enter To Continue...");
                        hold.nextLine();
                        hold.nextLine();
                        wflag=false;
                    }
                    break;
                case 4:
                    changeKey();
                    wflag=false;
                    break;
                default:
                    System.out.println("Invalid Input!");
                    System.out.print("Selected Action:");
                    break;
            }
        }

    }

    private void EncryptCaesar(){//return is if the method succeeded
        byte[] output = new byte[data.length];
        for(int i =0;i<data.length;i++) {
            int temp = (int) data[i] + (int) key;
            if (temp > Byte.MAX_VALUE) {//deal with overflow
                temp -= Byte.MAX_VALUE + 1;//Takin into consideration 0 , thats why +1
                temp += Byte.MIN_VALUE;
            }
            output[i] = (byte) temp;
        }

        writeEnc(output);
    }

    private void EncryptXOR(){
        byte output[] = new byte[data.length];
        for(int i =0;i<data.length;i++) {
            output[i] = (byte)(data[i] ^ key);
        }

        writeEnc(output);
    }

    private void EncryptMult()throws IOException{

        if(key%2==0 || key==0) throw new IOException("Illegal Key Value - Can not be divided by or zero!");
        byte output[] = new byte[data.length];
        for(int i =0;i<data.length;i++) {
            output[i] = (byte)(data[i] * key);
        }

        writeEnc(output);
    }

    private void writeEnc(byte[] out) {
        try {
            File f = new File(LogicController.getFilePath()+"/"+LogicController.getFileName()+".encrypted");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(signiture);
            fos.write(out);
        }
        catch (IOException e) {
            System.out.println("[Critical Error]Failed to create encrypted file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
        }
    }

    private void changeKey(){
        boolean keyflag=false;
        while(!keyflag) {
            try {
                System.out.println("\nNew Key Value:");
                key = (byte) (hold.nextInt() % 128);
                keyflag = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid Key Format!\nPlease input a numer:");
            }
        }
    }

}
