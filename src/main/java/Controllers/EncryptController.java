package Controllers;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import javax.naming.InterruptedNamingException;
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
    static Scanner hold = new Scanner(System.in);
    private static String fileName;
    private static String filePath;
    private static String fileType;
    private static byte[] output;
    private static byte key,key2;//TODO change the keys and the data to a seperate serizlizeable class - dont feel the need to do so yet thou.
    private static byte[] data;
    private static byte[] signiture = new byte[LogicController.sigLen];//first sigLen bytes of a encrypted file would be save for decryption information - such as original format.

    public int Encrypt(InputStream in) {//when we enter - the LogicController has info about the encryption file - first we need to extract it.

        fileName = LogicController.getFileName();
        filePath = LogicController.getFilePath();
        fileType = LogicController.getFileType();

        String temp = fileType+"-xxxxxxxxxxxxx";// - to break between parts of the signiture , x to make sure no data loss due to null charecters.
        signiture = temp.substring(0,LogicController.sigLen).getBytes();
        key = (byte) (rng.nextInt()%128);//working with bytes ranging from -128 to 127
        key2 = (byte) (rng.nextInt()%128);//working with bytes ranging from -128 to 127
        System.out.println("Starting Encryption Process - Encryption Keys:"+key+" ,"+key2+"!\n[Notice]In Encryptions with only one key - The first key will be used.\n\nPress Enter to continue...");
        hold.nextLine();
        LogicController.clearConsole("");

        //first write of the key
        try {
            data = LogicController.getBytesFromInputStream(in);
            File f = new File(filePath+"/key.bin");
            f.delete();//OVERWRITE
            f = new File(filePath+"/key.bin");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(key);
            fos.write(key2);
        }
        catch(IOException e)
        {
            System.out.println("[Critical Error]Failed to Load data from file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
            return 1;//exit with error code 1 - failed read (in theory we should never reach her due to previous tests
        }
        output = new byte[data.length];
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
                System.out.println("Please Select The Type Of Encryption:\nWith The Encryption Key:" + key + "\n\n1.Ceasar Encryption\n2.XOR Encryption\n3.Multiplication Encryption\n4.Double Encryption\n5.Reverse Encryption\n6.Split Encryption\n7.Change Encryption Key\n\n0.Return To Menu");
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
                    output = EncryptCaesar(data,key);
                    writeEnc(output);
                    exitFlag = true;
                    break;
                case 2:
                    output = EncryptXOR(data,key);
                    writeEnc(output);
                    exitFlag = true;
                    break;
                case 0:
                    exitFlag = true;
                    break;
                case 3:
                    try {
                        output = EncryptMult(data,key);
                        writeEnc(output);
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
                    output = EncryptDouble(data,key,key2);
                    writeEnc(output);
                    exitFlag = true;
                    break;
                case 5:
                    output = EncryptReverse(data,key);
                    writeEnc(output);
                    exitFlag = true;
                    break;
                case 6:
                    output = EncryptSplit(data,key,key2);
                    writeEnc(output);
                    exitFlag = true;
                    break;
                case 7:
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

    public static byte[] EncryptCaesar(byte[]d, byte k){//return is if the method succeeded
        byte[] temp_output = new byte[d.length];
        for(int i =0;i<d.length;i++) {
            int temp = (int) d[i] + (int) k;
            if (temp > Byte.MAX_VALUE) {//deal with overflow
                temp -= Byte.MAX_VALUE + 1;//Takin into consideration 0 , thats why +1
                temp += Byte.MIN_VALUE;
            }
            temp_output[i] = (byte) temp;
        }
        return temp_output;
    }

    public static byte[] EncryptXOR(byte[]d, byte k){
        byte[] temp_output = new byte[d.length];
        for(int i =0;i<d.length;i++) {
            temp_output[i] = (byte)(d[i] ^ k);
        }
        return temp_output;
    }

    public static  byte[] EncryptMult(byte[]d, byte k)throws IOException{
        byte[] temp_output = new byte[d.length];
        if(k%2==0 || k==0) throw new IOException("Illegal Key Value - Can not be divided by or zero!");
        for(int i =0;i<d.length;i++) {
            temp_output[i] = (byte)(d[i] * k);
        }
        return temp_output;
    }

    private static byte[] EncryptDouble(byte[] d,byte k1,byte k2) {
        byte[] temp_output;
        temp_output = EncryptXOR(d,k1);
        temp_output = EncryptCaesar(temp_output,k2);
        return temp_output;
    }

    private static byte[] EncryptReverse(byte[] d,byte k){
        byte[] temp_output = new byte[d.length];

        boolean exitFlag = false;
        boolean wflag = false;
        int choice = -1;

        while(!exitFlag) {
            if(!wflag) {
                LogicController.clearConsole("");
                System.out.println("Please Select The Type Of Encryption You want to reverse:\nWith The Encryption Key:" + k + "\n\n1.Ceasar Encryption\n2.XOR Encryption\n3.Multiplication Encryption\n4.Change Key\n\n0.Return");
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
                    temp_output = DecryptController.DecryptCaesar(d,k);
                    exitFlag = true;
                    break;
                case 2:
                    temp_output = DecryptController.DecryptXOR(d,k);
                    exitFlag = true;
                    break;
                case 0:
                    exitFlag = true;
                    break;
                case 3:
                    try {
                        temp_output = DecryptController.DecryptMult(d,k);
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
        return temp_output;
    } //TODO Save the encryption type in the encrypted signature so the decryptor wont have to know how the reverse was made - maybe?

    private static byte[] EncryptSplit(byte[] d,byte k1,byte k2) {// The problem with letting the user choose with that encryption to split encrypt is that creating the menu makes the code messy - so im avoding that for the time being - a example of it is made in encrypt reverse
        byte[] temp_output = new byte[d.length];
        byte[] encK1 = new byte[(d.length/2)+1];
        byte[] encK2 = new byte[(d.length/2)+1];
        int j = 0;
        int k = 0;

        for(int i=0;i<d.length;i++)//manually split the data byte array
        {
            if((i%2)==0){
                encK1[j] = d[i];
                j++;
            }
            else{
                encK2[k] = d[i];
                k++;
            }
        }
        encK1 = EncryptXOR(encK1,k1);
        encK2 = EncryptXOR(encK2,k2);
        j=0;
        k=0;

        for(int i=0;i<d.length;i++)
        {
            if((i%2)==0) {
                temp_output[i] = encK1[j];
                j++;
            }
            else {
                temp_output[i] = encK2[k];
                k++;
            }
        }

        return temp_output;
    }

    private static void writeEnc(byte[] out) {
        try {
            File f = new File(filePath+"/"+fileName+".encrypted");
            f.delete();//OVERWRITE
            f = new File(filePath+"/"+fileName+".encrypted");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(signiture);
            fos.write(out);
        }
        catch (IOException e) {
            System.out.println("[Critical Error]Failed to create encrypted file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
        }
    }

    private static void changeKey(){
        boolean keyflag=false;
        while(!keyflag) {
            try {
                System.out.println("\nNew Main Key Value:");
                EncryptController.key = (byte) (hold.nextInt() % 128);
                keyflag = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid Key Format!\nPlease input a numer:");
            }
        }
        keyflag = false;
        while(!keyflag) {
            try {
                System.out.println("\nNew Sub Key Value:");
                key2 = (byte) (hold.nextInt() % 128);
                keyflag = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid Key Format!\nPlease input a numer:");
            }
        }
        //now overwrite the key.bin file
        File f = new File(filePath+"/key.bin");
        f.delete();//deleting the old key (wrong to do so before we guanrntee that the new key is saved) - TODO later
        try {
            File f_new = new File(filePath + "/key.bin");
            FileOutputStream fos = new FileOutputStream(f_new);
            fos.write(key);
            fos.write(key2);
        }
        catch(IOException e){
            System.out.println("[Critical Error]Failed to Load data\nPress Enter to continue...");//should NEVER get here due to prior tests on input.
            hold.nextLine();
        }

    }

}
