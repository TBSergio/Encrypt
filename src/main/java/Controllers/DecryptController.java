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

    static Scanner hold = new Scanner(System.in);
    private static String format;
    private static String fileName;
    private static String filePath;
    private static String fileType;
    private static byte[] output;
    private static byte[] keys;//TODO change the keys and the data to a seperate serizlizeable class - dont feel the need to do so yet thou.
    private static byte[] raw;
    private static byte[] data;
    private static byte[] signiture = new byte[LogicController.sigLen];//first sigLen bytes of a encrypted file would be save for decryption information - such as original format.

    public int Decrypt(InputStream in) {

        fileName = LogicController.getFileName();
        filePath = LogicController.getFilePath();
        fileType = LogicController.getFileType();

        LogicController.clearConsole("");
        if(!fileType.equals("encrypted"))//can not decrypt an file...
        {
            System.out.println("File is not in .encrypted format.\nReturning to menu - Please select Valid decryption format\nPress Enter to continue...");
            hold.nextLine();
            return 2;//wrong format return
        }

        System.out.println("Please input a decryption key path!\n[Notice]Key is in byte format.\n[Notice]Overflow will apply.\n");
        InputStream k = LogicController.checkFilePath("");
        if(k==null){
            System.out.println("Key not found!\nPress Enter to continue...");
            hold.nextLine();
            return 3;//no key return
        }
        try{
            keys = LogicController.getBytesFromInputStream(k);
            raw = LogicController.getBytesFromInputStream(in);
        }
        catch(IOException e)
        {
            System.out.println("[Critical Error]Failed to Load data from file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
            return 1;//exit with error code 1 - failed read (in theory we should never reach her due to previous tests
        }
        if(keys.length!=2){
            System.out.println("Key Size Is Illegal!\nPress Enter to continue...");
            hold.nextLine();
            return 4;//Illegal key return
        }


        signiture = Arrays.copyOfRange(raw,0,LogicController.sigLen-1);
        String rawSig = new String(signiture);
        format = rawSig.split(Pattern.quote("-"),2)[0];
        data = Arrays.copyOfRange(raw,LogicController.sigLen,raw.length);
        output = new byte[data.length];
        SelectDecryptionType();

        System.out.println("Decryption Successfull!\nPress Enter to continue...");
        hold.nextLine();//This wait for enter is passed by because of the previous enter - and the inability to flush the input stream.
        hold.nextLine();
        return 0;//correct exit from the decrypt
    }

    public void SelectDecryptionType() {

        boolean exitFlag = false;
        boolean wflag = false;
        int choice = -1;

        while(!exitFlag) {
            if(!wflag) {
                LogicController.clearConsole("");
                System.out.println("Please Select The Type Of Decryption:\nWith The Decryption Keys:" + keys[0]+" ,"+keys[1] + "\n\n1.Ceasar Decryption\n2.XOR Decryption\n3.Multiplication Decryption\n4.Double Decryption\n5.Reverse Decryption\n6.Split Decryption\n7.Change Decryption Key\n\n0.Return To Menu");
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
                    output = DecryptCaesar(data,keys[0]);
                    writeDec(output);
                    exitFlag = true;
                    break;
                case 2:
                    output = DecryptXOR(data,keys[0]);
                    writeDec(output);
                    exitFlag = true;
                    break;
                case 0:
                    exitFlag = true;
                    break;
                case 3:
                    try{
                        output = DecryptMult(data,keys[0]);
                        writeDec(output);
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
                    output = DecryptDouble(data,keys[0],keys[1]);
                    writeDec(output);
                    exitFlag = true;
                    break;
                case 5:
                    output = DecryptReverse(data,keys[0]);
                    writeDec(output);
                    exitFlag=true;
                    break;
                case 6:
                    output = DecryptSplit(data,keys[0],keys[1]);
                    writeDec(output);
                    exitFlag=true;
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

    public static byte[] DecryptCaesar(byte[]d, byte k){
        byte[] temp_output = new byte[d.length];
        for(int i =0;i<d.length;i++){
            int temp = (int)d[i] - (int)k;
            if(temp < Byte.MIN_VALUE){//deal with overflow
                temp += Byte.MAX_VALUE+1;//Takin into consideration 0 , thats why +1
                temp -= Byte.MIN_VALUE;
            }
            temp_output[i] = (byte)temp;
        }
        return temp_output;
    }

    public static byte[] DecryptXOR(byte[]d, byte k){
        byte[] temp_output = new byte[d.length];
        for(int i =0;i<d.length;i++) {
            temp_output[i] = (byte)(d[i] ^ k);
        }
        return temp_output;
    }

    public static byte[] DecryptMult(byte[]d, byte k)throws IOException{
        byte[] temp_output = new byte[d.length];
        byte dec_key = 0;
        if(k%2==0 ||k==0) throw new IOException("Illegal Key Value - Can not be divided by or zero!");
        try {
            dec_key = findDecKey(k);
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Press Enter To Continue...");
            DecryptController.hold.nextLine();
        }
        for(int i =0;i<d.length;i++) {
            temp_output[i] = (byte)(d[i] * dec_key);
        }
        return temp_output;
    }

    private static byte[] DecryptDouble(byte[] d,byte k1,byte k2) {
        byte[] temp_output;
        temp_output = DecryptCaesar(d,k2);
        temp_output = DecryptXOR(temp_output,k1);
        return temp_output;
    }

    private static byte[] DecryptReverse(byte[] d,byte k){
        byte[] temp_output = new byte[d.length];

        boolean exitFlag = false;
        boolean wflag = false;
        int choice = -1;

        while(!exitFlag) {
            if(!wflag) {
                LogicController.clearConsole("");
                System.out.println("Please Select The Type Of Decryption You want to reverse:\nWith The Encryption Key:" + k + "\n\n1.Ceasar Encryption\n2.XOR Encryption\n3.Multiplication Encryption\n4.Change Key\n\n0.Return");
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
                    temp_output = EncryptController.EncryptCaesar(d,k);
                    exitFlag = true;
                    break;
                case 2:
                    temp_output = EncryptController.EncryptXOR(d,k);
                    exitFlag = true;
                    break;
                case 0:
                    exitFlag = true;
                    break;
                case 3:
                    try {
                        temp_output = EncryptController.EncryptMult(d,k);
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
    }//TODO Save the encryption type in the encrypted signature so the decryptor wont have to know how the reverse was made - maybe?

    private static byte[] DecryptSplit(byte[] d,byte k1,byte k2) {// The problem with letting the user choose with that encryption to split encrypt is that creating the menu makes the code messy - so im avoding that for the time being - a example of it is made in encrypt reverse
        byte[] temp_output = new byte[d.length];
        byte[] decK1 = new byte[(d.length/2)+1];
        byte[] decK2 = new byte[(d.length/2)+1];
        int j = 0;
        int k = 0;

        for(int i=0;i<d.length;i++)//manually split the data byte array
        {
            if((i%2)==0){
                decK1[j] = d[i];
                j++;
            }
            else{
                decK2[k] = d[i];
                k++;
            }
        }
        decK1 = DecryptXOR(decK1,k1);
        decK2 = DecryptXOR(decK2,k2);
        j=0;
        k=0;

        for(int i=0;i<d.length;i++)
        {
            if((i%2)==0) {
                temp_output[i] = decK1[j];
                j++;
            }
            else {
                temp_output[i] = decK2[k];
                k++;
            }
        }

        return temp_output;
    }

    private void writeDec(byte[] output) {

        try {
            File f = new File(filePath+"/"+fileName+"_decrypted."+format);
            f.delete();//OVERWRITE
            f = new File(filePath+"/"+fileName+"_decrypted."+format);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(output);
        }
        catch (IOException e) {
            System.out.println("[Critical Error]Failed to create encrypted file - Sending back to main menu\nPress Enter to continue...");
            hold.nextLine();
        }
    }

    private static byte findDecKey(byte k) throws IOException{

        for(byte i=Byte.MIN_VALUE;i<=Byte.MAX_VALUE;i++){
            if(((byte)(i*k))==1) return i;
        }
        throw new IOException("Decryption Key Not FoundS");
    }

    private static void changeKey(){
        boolean keyflag=false;
        while(!keyflag) {
            try {
                System.out.println("\nNew Main Key Value:");
                keys[0] = (byte) (hold.nextInt() % 128);
                keyflag = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid Key Format!\nPlease input a numer:");
            }
        }
        keyflag=false;
        while(!keyflag) {
            try {
                System.out.println("\nNew Sub Key Value:");
                keys[1] = (byte) (hold.nextInt() % 128);
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
            fos.write(keys[0]);
            fos.write(keys[1]);
        }
        catch(IOException e){
            System.out.println("[Critical Error]Failed to Load data\nPress Enter to continue...");//should NEVER get here due to prior tests on input.
            hold.nextLine();
        }

    }

}
