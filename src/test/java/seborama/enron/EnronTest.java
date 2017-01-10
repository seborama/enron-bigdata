package seborama.enron;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EnronTest {
    @Test
    public void OpenAZipStream() throws Exception {
        EnronZipStream zipStream = new EnronZipStream();

//        String resourcesDir = new File(
//                Test.class.getClassLoader().getResource("testZip.zip").getPath()
//        ).getParent();
        String resourcesDir = "/Volumes/Seb JS Mac Bak/Downloads/enron";

        assertEquals(204, zipStream.OpenZipStream(resourcesDir));
    }

    @Test(expected = IOException.class)
    public void OpenAZipStream_WithNonExistentDirectory() throws Exception {
        EnronZipStream zipStream = new EnronZipStream();

        zipStream.OpenZipStream("this_dir_does_not_exist");
    }
}
