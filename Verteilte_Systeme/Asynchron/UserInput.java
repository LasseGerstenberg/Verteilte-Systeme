import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class UserInput extends Thread {

    private boolean stopThread = false;
    private final BlockingQueue<String> countdownToUserInputQueue;
    private final BlockingQueue<String> userInputToCountdownQueue;

    public UserInput(BlockingQueue<String> countdownToUserInputQueue, BlockingQueue<String> userInputToCountdownQueue) {
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
        scanner.close();
    }

    private void pollCountdownToUserInputQueue() {
        if (countdownToUserInputQueue.contains("canceled")) {
            stopThread = true;
        } else {
            List<String> list = new ArrayList<>();
            countdownToUserInputQueue.drainTo(list);

            if (!list.isEmpty()) {
                int lastElement = Integer.parseInt(list.get(list.size() - 1));
                System.out.println("Aktueller Zaehlerstand: " + lastElement);
            }
        }
    }
}
