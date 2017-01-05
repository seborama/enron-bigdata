package seborama.enron;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EnronTest {
    @Test
    public void OpenAZipStream() throws Exception {
        EnronZipStream zipStream = new EnronZipStream();

        assertEquals(142, zipStream.OpenZipStream(
                Test.class.getClassLoader().getResource("testZip.zip").getPath()));
    }

    @Test(expected = IOException.class)
    public void OpenAZipStream_WithNonExistentFile() throws Exception {
        EnronZipStream zipStream = new EnronZipStream();

        zipStream.OpenZipStream("this_file_does_not_exist");
    }
}
