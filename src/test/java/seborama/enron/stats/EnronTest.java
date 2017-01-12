package seborama.enron.stats;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EnronTest {
    @Test
    public void TestAverageWordsPerMail() throws Exception {
        AverageWordsPerMail averageWordsPerMail = new AverageWordsPerMail();

        String resourcesDir = new File(Test.class.getClassLoader().getResource("testZip.zip").toURI())
                .getParent();

        assertEquals(126, averageWordsPerMail.calculate(resourcesDir));
    }

    @Test(expected = IOException.class)
    public void TestAverageWordsPerMail_WithNonExistentDirectory() throws Exception {
        AverageWordsPerMail averageWordsPerMail = new AverageWordsPerMail();

        averageWordsPerMail.calculate("this_dir_does_not_exist");
    }
}
