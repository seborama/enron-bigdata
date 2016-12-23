package seborama.enron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
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
                .filter(ze -> containsText(zipFile, ze, "****"))
                .count();
    }

    private boolean containsText(ZipFile zipFile, ZipEntry zipEntry, String needle) {
        try (InputStream inputStream = zipFile.getInputStream(zipEntry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            Optional<String> found = reader.lines()
                    .filter(l -> l.contains(needle))
                    .findFirst();

            return found.isPresent();
        } catch (IOException e) {
            return false;
        }
    }
}
