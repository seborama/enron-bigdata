package seborama.enron;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnronTest {
    @Test
    public void OpenAZipStream() throws Exception {
        ClassLoader loader = Test.class.getClassLoader();
        assertEquals(20, seborama.enron.Main.OpenZipStream(loader.getResource("testZip.zip").getPath()));
    }
}
