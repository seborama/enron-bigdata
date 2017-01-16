package seborama.enron;

import seborama.enron.stats.AverageWordsPerMail;
import seborama.enron.stats.TopMailRecipients;

import java.io.IOException;
import java.util.concurrent.*;

public class EnronBigData {
    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            usage();

        AverageWordsPerMail averageWordsPerMail = new AverageWordsPerMail();
        System.out.println("Average length, in words, of the emails\n(ignoring attachments):\n" + wrapForkJoinPool(() -> averageWordsPerMail.calculate(args[0])) + "\n\n");

        TopMailRecipients topMailRecipients = new TopMailRecipients();
        System.out.println("Top 100 recipient email addresses\n(an email sent to N recipients would could N times - count “cc” as 50%):\n" + topMailRecipients.calculate(args[0]) + "\n\n");
    }

    private static long wrapForkJoinPool(Callable<Long> task) {
        final ForkJoinPool forkJoinPool = new ForkJoinPool(30);
        ForkJoinTask<Long> fjt = forkJoinPool.submit(task);
        try {
            forkJoinPool.shutdown();
            forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        long result = -1;
        try {
            result = fjt.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("DEBUG - returning result: " + result);
        return result;
    }

    private static void usage() {
        System.out.println("EnronBigData <path to enron v2 zip files dataset>");
        System.exit(10);
    }
}
