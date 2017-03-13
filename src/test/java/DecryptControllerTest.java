import Controllers.DecryptController;
import org.junit.Test;

import java.io.InputStream;
import java.io.PipedInputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Sergei on 3/14/2017.
 */
public class DecryptControllerTest {

    @Test
    public void DecryptTest()
    {
        InputStream inValid = new PipedInputStream();
        DecryptController DM = mock(DecryptController.class);
        when(DM.Decrypt(null)).thenReturn(0);
        when(DM.Decrypt(inValid)).thenReturn(1);


        assertEquals(0,DM.Decrypt(null));
        assertEquals(1,DM.Decrypt(inValid));
    }

}
