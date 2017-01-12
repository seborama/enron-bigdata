package seborama.enron.stats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ZipMailReader {

    private ZipFile zipFile;
    private ZipEntry zipEntry;
    private static String endHeaderMark = "\r\nX-ZLID: ";
    private static int endHeaderMarkLength = endHeaderMark.length();
    private static String startFooterMark = "\r\n***********\r\nEDRM";

    ZipMailReader(ZipFile zipFile, ZipEntry zipEntry) {
        this.zipFile = zipFile;
        this.zipEntry = zipEntry;
    }

    Stream<String> readBodyV1() {
        List<String> emailBody = new ArrayList<>(1024);

        try (InputStream inputStream = zipFile.getInputStream(zipEntry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            skipHeader(reader);

            String line;
            while ((line = reader.readLine()) != null) {
                if (isStartOfFooter(line)) {
                    break;
                }
                emailBody.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emailBody.stream();
    }

    Stream<String> readBody() {
        final List<String> emailData = readMail();
        final String email = emailData.stream().collect(Collectors.joining());
        final int headerPos = email.indexOf(endHeaderMark);
        final int bodyStart = email.indexOf("\r\n", headerPos + endHeaderMarkLength + 1) + 2;
        final int bodyEnd = email.indexOf(startFooterMark, bodyStart) - 1;
        if (bodyEnd < bodyStart)
            return Stream.of("");

        return Stream.of(email.substring(bodyStart, bodyEnd));
    }

    private List<String> readMail() {
        List<String> emailData = new ArrayList<>();

        try (InputStream inputStream = zipFile.getInputStream(zipEntry);
             final ReadableByteChannel channel = java.nio.channels.Channels.newChannel(inputStream)) {
            final ByteBuffer byteBuffer = ByteBuffer.allocateDirect((int) zipEntry.getSize());
            int fileSize = (int) zipEntry.getSize();

            while (channel.read(byteBuffer) > 0) {
                // limit is set to current position and position is set to zero
                byteBuffer.flip();

                while (byteBuffer.hasRemaining()) {
                    byte[] bar = new byte[fileSize];
                    byteBuffer.get(bar);
                    emailData.add(new String(bar));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emailData;
    }

    private void skipHeader(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (isEndOfHeader(line)) {
                break;
            }
        }
    }

    private boolean isEndOfHeader(String line) {
        return line.startsWith("X-ZLID: ");
    }

    private boolean isStartOfFooter(String line) {
        return line.equals("***********");
    }
}
