import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UserInput extends Thread {

    private boolean stopThread = false;
    private final ConcurrentLinkedQueue<String> countdownToUserInputQueue;
    private final ConcurrentLinkedQueue<String> userInputToCountdownQueue;

    public UserInput(ConcurrentLinkedQueue<String> countdownToUserInputQueue, ConcurrentLinkedQueue<String> userInputToCountdownQueue) {
        this.countdownToUserInputQueue = countdownToUserInputQueue;
        this.userInputToCountdownQueue = userInputToCountdownQueue;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (!stopThread) {
            pollCountdownToUserInputQueue();
            System.out.println("Enter command (set X/cancel/remaining):");
            String command = scanner.nextLine();
            if(!command.isEmpty()) {
                if(command.startsWith("set")) {
                    userInputToCountdownQueue.offer(command);
                } else if(command.equals("cancel")) {
                    userInputToCountdownQueue.offer("cancel");
                } else if(command.equals("remaining")) {
                    userInputToCountdownQueue.offer("remaining");
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            pollCountdownToUserInputQueue();
        }
        System.out.println("Der Countdown wurde beendet!");
        scanner.close();
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
