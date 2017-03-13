import Controllers.LogicController;
import org.junit.Test;


import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by Sergei on 3/13/2017.
 */

public class LogicControllerTest {

    @Test
    public void testClearConsole()
    {
        LogicController CM = mock(LogicController.class);
        when(CM.clearConsole("Windows")).thenReturn(1);
        when(CM.clearConsole("Linux")).thenReturn(2);

        assertEquals(1,CM.clearConsole("Windows"));
        assertEquals(2,CM.clearConsole("Linux"));

        //LogicController cont = new LogicController();
        //assertEquals(1,cont.clearConsole(""));
    }
    @Test(expected = RuntimeException.class)
    public void testCheckFilePath_ExceptionThrow()
    {
       LogicController CM = mock(LogicController.class);
       doThrow(new RuntimeException("Invalid Path to File or File Does'nt Exist!")).when(CM.checkFilePath("Invalid"));

       CM.checkFilePath("Invalid");
    }
    @Test
    public void testCheckFilePath()
    {
        LogicController CM = mock(LogicController.class);
        InputStream in = null;
        when(CM.checkFilePath("Valid")).thenReturn(in);

        assertSame(in,CM.checkFilePath("Valid"));
    }




}
