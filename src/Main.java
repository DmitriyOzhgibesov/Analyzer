import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> aHandler = new ArrayBlockingQueue(10);
        BlockingQueue<String> bHandler = new ArrayBlockingQueue(10);
        BlockingQueue<String> cHandler = new ArrayBlockingQueue(10);

        AtomicReference<String> textWithMaxA = new AtomicReference<>("");
        AtomicReference<String> textWithMaxB = new AtomicReference<>("");
        AtomicReference<String> textWithMaxC = new AtomicReference<>("");

        List<Thread> threads = new ArrayList<>();

        Runnable fillQuequeues = () -> {
            for (int i = 0; i < 20; i++) {
                try {
                    String s = generateText("abc", 10);
                    aHandler.put(s);
                    bHandler.put(s);
                    cHandler.put(s);
                } catch (InterruptedException e) {
                    return;
                }
            }
        };
        Thread generateTexts = new Thread(fillQuequeues);
        Runnable chechMaxA = () -> textWithMaxA.set(handleQueue('a', aHandler));
        Thread checkA = new Thread(chechMaxA);
        Runnable chechMaxB = () -> textWithMaxB.set(handleQueue('b', bHandler));
        Thread checkB = new Thread(chechMaxB);
        Runnable chechMaxC = () -> textWithMaxC.set(handleQueue('c', cHandler));
        Thread checkC = new Thread(chechMaxC);

        threads.add(generateTexts);
        threads.add(checkA);
        threads.add(checkB);
        threads.add(checkC);

        generateTexts.start();
        checkA.start();
        checkB.start();
        checkC.start();

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Текст с максимальным кол-вом символов 'a': " + textWithMaxA.get());
        System.out.println("Текст с максимальным кол-вом символов 'b': " + textWithMaxB.get());
        System.out.println("Текст с максимальным кол-вом символов 'c': " + textWithMaxC.get());
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int getMaxCharCount(char symbol, String text) {
        int maxCharCount = 0;

        for (char c : text.toCharArray()) {
            if (c == symbol) {
                maxCharCount++;
            }
        }

        return maxCharCount;
    }

    public static String handleQueue(char symbol, BlockingQueue<String> queue) {
        int oldMaxCountA = 0;
        String result = "";
        for (int i = 0; i < 10; i++) {
            int newMaxACount;
            try {
                String s = queue.take();
                newMaxACount = getMaxCharCount(symbol, s);

                if (newMaxACount > oldMaxCountA) {
                    oldMaxCountA = newMaxACount;
                    result = s;
                }
            } catch (InterruptedException e) {
                return result;
            }
        }

        return result;
    }
}