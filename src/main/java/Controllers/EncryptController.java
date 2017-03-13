package Controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sergei on 3/6/2017.
 */
public class EncryptController {

    public int Encrypt(InputStream in) {
            System.out.println("\nEncryption simulation of file: "+LogicController.getFileName()+"\n");
            return 1;
    }

}
