package seborama.enron;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class EnronTest {
    @Test
    public void OpenAZipFile() throws Exception {
        assertTrue(seborama.enron.Main.OpenZipFile());
    }
}
