package seborama.enron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EnronZipStream {
    public long OpenZipStream(String zip) throws IOException {
        Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
        Predicate<ZipEntry> isInTextDirectory = ze -> ze.getName().contains("text_000/");
        Predicate<ZipEntry> isText = ze -> ze.getName().matches("^.*[A-Z]\\.txt$");

        ZipFile zipFile = new ZipFile(zip);
        return zipFile.stream()
                .filter(isFile)
                .filter(isInTextDirectory)
                .filter(isText)
                .map(ze -> getEmailBody(zipFile, ze))
                .flatMap(Arrays::stream)
                .count();
    }

    private String[] getEmailBody(ZipFile zipFile, ZipEntry zipEntry) {
        List<String> writer = new ArrayList<>();

        try (InputStream inputStream = zipFile.getInputStream(zipEntry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            boolean inBody = false;
            while ((line = reader.readLine()) != null) {
                if (inBody) {
                    if (line.equals("***********")) {
                        inBody = false;
                        continue;
                    }

                    writer.add(line);
                } else if (line.startsWith("X-ZLID: ")) inBody = true;
            }
        } catch (IOException e) {
            // TODO: 03/01/2017 something useful with the error
        }

        return writer.toArray(new String[0]);
    }
}
