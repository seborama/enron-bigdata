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

        return Files.list(Paths.get(dir))
                .filter(pathEntryIsZip.and(pathEntryIsFile))
                .map(path -> {
                    try {
                        System.out.println(path.toString());
                        return new ZipFile(path.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .map(zipFile -> zipFile.stream()
                        .filter(isFile.and(isTextEmail))
                        .map(ze -> Stream.of(getEmailBody(zipFile, ze)) // Stream<String>
                                .map(this::getWordCount) // Stream<Long>
                                .collect(Collectors.summingLong(Long::longValue)))
                        .parallel()
                        .collect(Collectors.averagingLong(Long::longValue)))
                .collect(Collectors.averagingLong(Double::longValue)).longValue();
    }

    private long getWordCount(String line) {
        Predicate<String> isNotEmpty = s -> !s.isEmpty();

        return Stream.of(reWords.split(line)).filter(isNotEmpty).count();
    }

    private String[] getEmailBody(ZipFile zipFile, ZipEntry zipEntry) {
        return new ZipMailReader(zipFile, zipEntry).read();
    }
}
