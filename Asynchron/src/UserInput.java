import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.*;

public class UserInput extends Thread {

    private boolean stopThread = false;

    // ConcurrentQueues ermoeglichen asynchrone Kommunikation zwischen den Klassen. offer() und poll () sind nicht blockend
    private final ConcurrentLinkedQueue<String> countdownToUserInputQueue;
    private final ConcurrentLinkedQueue<String> userInputToCountdownQueue;

    public UserInput(ConcurrentLinkedQueue<String> countdownToUserInputQueue, ConcurrentLinkedQueue<String> userInputToCountdownQueue) {
        this.countdownToUserInputQueue = countdownToUserInputQueue;
        this.userInputToCountdownQueue = userInputToCountdownQueue;
    }

    public void run() {

        // Thread für Userinput
        Thread inputThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (!stopThread) {
                    System.out.println("Enter command (set X/cancel/remaining):");
                    String command = scanner.nextLine();
                    if(!command.isEmpty()) {
                        userInputToCountdownQueue.offer(command);
                    }
                }
                scanner.close();
        });
        inputThread.start();

        // Haupthread der horcht, ob es Nachrichten vom Countdown gibt. Verarbeitet diese ggf.
        while (!stopThread) {
            if(!countdownToUserInputQueue.isEmpty()) {
                String messageFromCountdown = countdownToUserInputQueue.poll();
                if(messageFromCountdown.equals("canceled")) {
                    System.out.println("Der Countdown wurde beendet");
                    System.exit(0);
                } else {
                    System.out.println(messageFromCountdown);
                }
            }

        }
    }
}
