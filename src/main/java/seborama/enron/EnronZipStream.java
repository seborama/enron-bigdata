package seborama.enron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EnronZipStream {
    private static final Pattern reWords = Pattern.compile("\\W+");
    private static final Pattern reTextEmail = Pattern.compile("text_.*[A-Z]\\.txt$");

    public long OpenZipStream(String dir) throws IOException {
        Predicate<Path> pathEntryIsFile = entry -> entry.toFile().isFile();
        Predicate<Path> pathEntryIsZip = entry -> entry.toFile().toString().toLowerCase().endsWith(".zip");

        Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
        Predicate<ZipEntry> isTextEmail = ze -> reTextEmail.matcher(ze.getName()).matches();

        int numCores = Runtime.getRuntime().availableProcessors() / 2;
        numCores = 1;
        System.out.printf("Setting ForkJoinPool parallelism to %d\n", numCores);
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(numCores));

        long avgWordsPerEmail = Files.list(Paths.get(dir))
                .filter(pathEntryIsFile.and(pathEntryIsZip))
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
                                .map(line -> getWordCount(line)) // Stream<Long>
                                .collect(Collectors.summingLong(Long::longValue)))
                        .parallel()
                        .collect(Collectors.averagingLong(Long::longValue)))
                .parallel()
                .collect(Collectors.averagingLong(Double::longValue)).longValue();

        return avgWordsPerEmail;
    }

    private long getWordCount(String line) {
        Predicate<String> isNotEmpty = s -> !s.isEmpty();

        return Stream.of(reWords.split(line)).filter(isNotEmpty).count();
    }

    private String[] getEmailBody(ZipFile zipFile, ZipEntry zipEntry) {
        List<String> writer = new ArrayList<>(1024);

        // Unfortunately you have to close functional file streams explicitly with try/with statements.
        try (InputStream inputStream = zipFile.getInputStream(zipEntry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("X-ZLID: "))
                    break;
            }

            while ((line = reader.readLine()) != null) {
                if (line.equals("***********"))
                    break;
                writer.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toArray(new String[0]);
    }
}
