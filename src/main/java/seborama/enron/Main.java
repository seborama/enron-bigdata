package seborama.enron;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {
    public static long OpenZipStream(String zip) throws IOException {
        Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();

        ZipFile zipFile = new ZipFile(zip);
        return zipFile.stream()
                .filter(isFile)
                .count();
    }
}
