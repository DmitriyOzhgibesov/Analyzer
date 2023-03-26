import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> aHandler = new ArrayBlockingQueue(100);
        BlockingQueue<String> bHandler = new ArrayBlockingQueue(100);
        BlockingQueue<String> cHandler = new ArrayBlockingQueue(100);

        Thread generateTexts = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    String s = generateText("abc", 100000);
                    aHandler.put(s);
                    bHandler.put(s);
                    cHandler.put(s);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        generateTexts.start();

        Thread checkA = new Thread(() -> {
            char letter = 'a';
            int maxA = getMaxCharCount(letter, aHandler);
            System.out.println("Максимальное кол-во символов '" + letter + "' во всех текстах: " + maxA);
        });
        checkA.start();

        Thread checkB = new Thread(() -> {
            char letter = 'b';
            int maxB = getMaxCharCount(letter, bHandler);
            System.out.println("Максимальное кол-во символов '" + letter + "' во всех текстах: " + maxB);
        });
        checkB.start();

        Thread checkC = new Thread(() -> {
            char letter = 'c';
            int maxC = getMaxCharCount(letter, cHandler);
            System.out.println("Максимальное кол-во символов '" + letter + "' во всех текстах: " + maxC);
        });
        checkC.start();


        checkA.join();
        checkB.join();
        checkC.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int getMaxCharCount(char symbol, BlockingQueue<String> queue) {
        int count = 0;
        int max = 0;
        String text;
        try {
            for (int i = 0; i < 10000; i++) {
                text = queue.take();
                for (char c : text.toCharArray()) {
                    if (c == symbol) count++;
                }
                if (count > max) max = count;
                count = 0;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " было прервано");
            return -1;
        }
        return max;
    }
}