import java.util.Scanner;
import java.util.concurrent.*;

public class UserInput extends Thread {

    private volatile boolean stopThread = false;
    private final ConcurrentLinkedQueue<String> countdownToUserInputQueue;
    private final ConcurrentLinkedQueue<String> userInputToCountdownQueue;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public UserInput(ConcurrentLinkedQueue<String> countdownToUserInputQueue, ConcurrentLinkedQueue<String> userInputToCountdownQueue) {
        this.countdownToUserInputQueue = countdownToUserInputQueue;
        this.userInputToCountdownQueue = userInputToCountdownQueue;
    }

    public void run() {
        Runnable userInputTask = () -> {
            Scanner scanner = new Scanner(System.in);
            while (!stopThread) {
                System.out.println("Enter command (set X/cancel/remaining):");
                String command = scanner.nextLine();
                processCommand(command);
            }
            scanner.close();
        };

        executorService.submit(userInputTask);

        while (!stopThread) {
            pollCountdownToUserInputQueue();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        executorService.shutdownNow();
        System.out.println("Der Countdown wurde beendet!");
        System.exit(0);
    }

    private void processCommand(String command) {
        if (!command.isEmpty()) {
            if (command.startsWith("set")) {
                userInputToCountdownQueue.offer(command);
            } else if (command.equals("cancel")) {
                userInputToCountdownQueue.offer("cancel");
            } else if (command.equals("remaining")) {
                userInputToCountdownQueue.offer("remaining");
            }
        }
    }

    private void pollCountdownToUserInputQueue() {
        String message;
        while ((message = countdownToUserInputQueue.poll()) != null) {
            if (message.equals("canceled")) {
                stopThread = true;
                break;
            } else {
                System.out.println("Aktueller Zaehlerstand: " + message);
            }
        }
    }
}
