package seborama.enron.stats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AverageWordsPerMail {

    private static final Pattern reWords = Pattern.compile("\\W+");
    private static final Pattern reTextEmail = Pattern.compile("^.*[A-Z]\\.txt$");

    public long calculate(String dir) throws IOException {
        Predicate<Path> pathEntryIsFile = entry -> entry.toFile().isFile();
        Predicate<Path> pathEntryIsZip = entry -> entry.toFile().toString().toLowerCase().endsWith(".zip");

        Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
        Predicate<ZipEntry> isTextEmail = ze -> ze.getName().contains("text_") && reTextEmail.matcher(ze.getName()).matches();

        return Files.list(Paths.get(dir)).parallel()
            .filter(pathEntryIsZip.and(pathEntryIsFile))
            .map(path -> {
                try {
//                    System.out.println(path.toString());
                    return new ZipFile(path.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).parallel()
            .map(
                zipFile -> zipFile.stream()
                    .filter(isFile.and(isTextEmail))
                    .map(
                        ze -> getEmailBody(zipFile, ze).parallel() // Stream<String>
                            .map(this::getWordCount).parallel() // Stream<Long>
                            .collect(Collectors.summingLong(Long::longValue)))
                    .parallel()
                    .collect(Collectors.averagingLong(Long::longValue))).parallel()
            .collect(Collectors.averagingLong(Double::longValue)).longValue();
    }

    private long getWordCountV1(String line) {
        Predicate<String> isNotEmpty = s -> !s.isEmpty();

        return Stream.of(reWords.split(line)).filter(isNotEmpty).count();
    }

    private long getWordCount(String line) {
        int pos = 0, end, count = 0;

        while ((end = line.indexOf(' ', pos)) >= 0) {
            count++;
            pos = end + 1;

            try {
                while (line.charAt(pos) == ' ') {
                    pos++;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return count;
    }

    private Stream<String> getEmailBody(ZipFile zipFile, ZipEntry zipEntry) {
        return new ZipMailReader(zipFile, zipEntry).readBody();
    }
}
