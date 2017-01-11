package seborama.enron.stats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ZipMailReader {
    private ZipFile zipFile;
    private ZipEntry zipEntry;

    ZipMailReader(ZipFile zipFile, ZipEntry zipEntry) {
        this.zipFile = zipFile;
        this.zipEntry = zipEntry;
    }

    String[] read() {
        List<String> writer = new ArrayList<>(1024);

        try (InputStream inputStream = zipFile.getInputStream(zipEntry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            skipHeader(reader);

            String line;
            while ((line = reader.readLine()) != null) {
                if (isStartOfFooter(line))
                    break;
                writer.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toArray(new String[0]);
    }

    private void skipHeader(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (isEndOfHeader(line))
                break;
        }
    }

    private boolean isEndOfHeader(String line) {
        return line.startsWith("X-ZLID: ");
    }

    private boolean isStartOfFooter(String line) {
        return line.equals("***********");
    }
}
