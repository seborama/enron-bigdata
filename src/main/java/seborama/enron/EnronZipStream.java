package seborama.enron;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

class KeyValue<K, V> {
    public K key;
    public V value;

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

public class EnronZipStream {
    public long OpenZipStream(String zip) throws IOException {
        Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
        Predicate<ZipEntry> isInTextDirectory = ze -> ze.getName().contains("text_000/");
        Predicate<ZipEntry> isTextEmail = ze -> ze.getName().matches("^.*[A-Z]\\.txt$");

        ZipFile zipFile = new ZipFile(zip);

//        System.out.println("DEBUG 2");
//        return zipFile.stream()
//                .filter(isFile.and(isTextEmail).and(isInTextDirectory))
//                .map(ze -> getEmailBody(zipFile, ze))
//                .flatMap(Arrays::stream)
//                .map(line -> getWords(line))
//                .flatMap(Arrays::stream)
//                .count();

        Map<String, Long> blah1b = zipFile.stream()
                .filter(isFile.and(isTextEmail).and(isInTextDirectory))
                .collect(toMap(ze -> ze.getName(), ze -> Stream.of(getEmailBody(zipFile, ze))
                        .map(line -> getWords(line))
                        .flatMap(Arrays::stream)
                        .count()
                ));

        return new Double(blah1b.entrySet().stream()
                .flatMapToLong(es -> LongStream.of(es.getValue()))
                .average()
                .getAsDouble()).longValue();
    }

    private String[] getWords(String line) {
        Predicate<String> isNotEmpty = s -> s != null && !s.isEmpty();

        return Stream.of(line.split("\\W+")).filter(isNotEmpty).toArray(String[]::new);
    }

    private String[] getEmailBody(ZipFile zipFile, ZipEntry zipEntry) {
        List<String> writer = new ArrayList<>();

        // Unfortunately you have to close functional file streams explicitly with try/with statements.
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
