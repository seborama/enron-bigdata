package seborama.enron;

import seborama.enron.stats.AverageWordsPerMail;
import seborama.enron.stats.TopMailRecipients;

import java.io.IOException;

public class EnronBigData {
    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            usage();

//        setParallelism();

        AverageWordsPerMail averageWordsPerMail = new AverageWordsPerMail();
        System.out.println("Average length, in words, of the emails\n(ignoring attachments):\n" + averageWordsPerMail.calculate(args[0]) + "\n\n");

        TopMailRecipients topMailRecipients = new TopMailRecipients();
        System.out.println("Top 100 recipient email addresses\n(an email sent to N recipients would could N times - count “cc” as 50%):\n" + topMailRecipients.calculate(args[0]) + "\n\n");
    }

    private static void setParallelism() {
        int numCores = Runtime.getRuntime().availableProcessors() / 2;
        System.out.printf("Setting ForkJoinPool parallelism to %d\n", numCores);
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(numCores));
    }

    private static void usage() {
        System.out.println("EnronBigData <path to enron v2 zip files dataset>");
        System.exit(10);
    }
}
