import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.*;

public class UserInput extends Thread {

    private boolean stopThread = false;
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
                    processCommand(command);
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

    //Verarbeitung der Befehle, die durch die Nutzeringabe getaetigt werden
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

}
