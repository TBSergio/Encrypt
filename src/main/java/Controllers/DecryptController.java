package Controllers;

import java.awt.peer.LightweightPeer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Sergei on 3/6/2017.
 */
public class DecryptController {

    Scanner hold = new Scanner(System.in);
    private byte key;
    private String format;
    private byte[] raw;
    private byte[] data;
    private byte[] signiture = new byte[LogicController.sigLen];//first sigLen bytes of a encrypted file would be save for decryption information - such as original format.

    public int Decrypt(InputStream in) {
        LogicController.clearConsole("");

        if(!LogicController.getFileType().equals("encrypted"))//can not decrypt an file...
        {
            System.out.println("File is not in .encrypted format.\nReturning to menu - Please select Valid decryption format\nPress Enter to continue...");
            hold.nextLine();
            return 2;//wrong format return
        }

        System.out.println("Please input a decryption key!\n[Notice]Key is in byte format.\n[Notice]Overflow will apply.\n\nDecryption Key:");

        boolean keyflag = false;
        while(!keyflag) {
            try {
                key = (byte) (hold.nextInt() % 128);
                keyflag = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid Key Format!\nPlease input a numer:");
            }
        }

        try{
            raw = LogicController.getBytesFromInputStream(in);
        }
        catch(IOException e)
        {
            System.out.println("[Critical Error]Failed to Load data from file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
            return 1;//exit with error code 1 - failed read (in theory we should never reach her due to previous tests
        }
        signiture = Arrays.copyOfRange(raw,0,LogicController.sigLen-1);
        String rawSig = new String(signiture);
        format = rawSig.split(Pattern.quote("-"),2)[0];
        data = Arrays.copyOfRange(raw,LogicController.sigLen,raw.length);

        SelectDecryptionType();

        System.out.println("Decryption Successfull!\nPress Enter to continue...");
        hold.nextLine();//This wait for enter is passed by because of the previous enter - and the inability to flush the input stream.
        hold.nextLine();
        return 0;//correct exit from the decrypt
    }

    private void DecryptCaesar(){
        byte[] output = new byte[data.length];
        for(int i =0;i<data.length;i++){
            int temp = (int)data[i] - (int)key;
            if(temp < Byte.MIN_VALUE){//deal with overflow
                temp += Byte.MAX_VALUE+1;//Takin into consideration 0 , thats why +1
                temp -= Byte.MIN_VALUE;
            }
            output[i] = (byte)temp;
        }


    }

    public void SelectDecryptionType() {

        boolean exitFlag = false;
        boolean wflag = false;
        int choice = -1;

        while(!exitFlag) {
            if(!wflag) {
                LogicController.clearConsole("");
                System.out.println("Please Select The Type Of Decryption:\nWith The Decryption Key:" + key + "\n\n1.Ceasar Decryption\n2.XOR Decryption\n3.Multiplication Decryption\n4.Change Decryption Key\n\n0.Return To Menu");
                System.out.print("\nSelected Action:");
                wflag = true;
            }
            try {
                choice = hold.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input!");
                System.out.print("Selected Action:");
            }
            switch(choice){
                case 1:
                    DecryptCaesar();
                    exitFlag = true;
                    break;
                case 2:
                    DecryptXOR();
                    exitFlag = true;
                    break;
                case 0:
                    exitFlag = true;
                    break;
                case 3:
                    try{
                        DecryptMult();
                        exitFlag=true;
                    }
                    catch(IOException e){
                        LogicController.clearConsole("");
                        System.out.println(e.getMessage()+"\nPlease Change the key before attempting the decryption!\nPress Enter To Continue...");
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

    private void DecryptXOR(){
        byte output[] = new byte[data.length];
        for(int i =0;i<data.length;i++) {
            output[i] = (byte)(data[i] ^ key);
        }

        writeDec(output);
    }

    private void DecryptMult()throws IOException{
        byte dec_key = 0;
        if(key%2==0 || key==0) throw new IOException("Illegal Key Value - Can not be divided by or zero!");
        try {
            dec_key = findDecKey();
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Press Enter To Continue...");
            hold.nextLine();
        }
        byte output[] = new byte[data.length];
        for(int i =0;i<data.length;i++) {
            output[i] = (byte)(data[i] * dec_key);
        }

        writeDec(output);
    }

    private void writeDec(byte[] output) {

        try {
            File f = new File(LogicController.getFilePath()+"/"+LogicController.getFileName()+"_decrypted."+format);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(output);
        }
        catch (IOException e) {
            System.out.println("[Critical Error]Failed to create encrypted file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
        }
    }

    private byte findDecKey() throws IOException{

        for(byte i=Byte.MIN_VALUE;i<=Byte.MAX_VALUE;i++){
            if(((byte)(i*key))==1) return i;
        }
        throw new IOException("Decryption Key Not FoundS");
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
