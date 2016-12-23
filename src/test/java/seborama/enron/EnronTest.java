package seborama.enron;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnronTest {
    @Test
    public void OpenAZipStream() throws Exception {
        EnronZipStream zipStream = new EnronZipStream();

        assertEquals(20, zipStream.OpenZipStream(
                Test.class.getClassLoader().getResource("testZip.zip").getPath()));
    }
}
