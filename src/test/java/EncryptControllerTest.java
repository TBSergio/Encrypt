
import Controllers.EncryptController;
import org.junit.Test;
import java.io.InputStream;
import java.io.PipedInputStream;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Sergei on 3/14/2017.
 */
public class EncryptControllerTest {

    @Test
    public void DecryptTest()
    {
        InputStream inValid = new PipedInputStream();
        EncryptController EM = mock(EncryptController.class);
        when(EM.Encrypt(null)).thenReturn(0);
        when(EM.Encrypt(inValid)).thenReturn(1);


        assertEquals(0,EM.Encrypt(null));
        assertEquals(1,EM.Encrypt(inValid));
    }

}
