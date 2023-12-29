import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class UserInput extends Thread {
    private boolean stopThread = false;
    private final LinkedBlockingQueue<String> countdownToUserInputQueue;
    private final LinkedBlockingQueue<String> userInputToCountdownQueue;

    public UserInput(LinkedBlockingQueue<String> countdownToUserInputQueue, LinkedBlockingQueue<String> userInputToCountdownQueue) {
        this.countdownToUserInputQueue = countdownToUserInputQueue;
        this.userInputToCountdownQueue = userInputToCountdownQueue;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        do {
            pollCountdownToUserInputQueue();
            if(!stopThread) {
                System.out.println("Enter command (set X/cancel/remaining):");
                String command = scanner.nextLine();
                if (!command.isEmpty()) {
                    try {
                        if (command.startsWith("set")) {
                            userInputToCountdownQueue.put(command);
                        } else if (command.equals("cancel")) {
                            userInputToCountdownQueue.put("cancel");
                        } else if (command.equals("remaining")) {
                            userInputToCountdownQueue.put("remaining");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } while(!stopThread);
        System.out.println("Countdown ended!");
        scanner.close();
    }

    private void pollCountdownToUserInputQueue() {
        try {
            while (!countdownToUserInputQueue.isEmpty()) {
                String message = countdownToUserInputQueue.take();
                if (message.equals("canceled")) {
                    stopThread = true;
                    break;
                } else {
                    System.out.println("Current countdown: " + message);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}