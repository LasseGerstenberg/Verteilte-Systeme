import java.util.Scanner;
import java.util.concurrent.*;

public class CountdownAsynchron {

    private static boolean isCountdownRunning = true;
    private static int countdownTimeInSeconds = 20;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<Void> userInputFuture = CompletableFuture.runAsync(() -> {
            Scanner scanner = new Scanner(System.in);
            while (isCountdownRunning) {
                System.out.println("Bitte Befehl eingeben: set {Zahl}, cancel, status");
                String input = scanner.nextLine();

                if(userinputIsCorrect(input)) {
                    if (input.startsWith("set")) {
                        int value = Integer.parseInt(input.split(" ")[1]);
                        setCountdown(value);
                        System.out.println("Countdown wurde auf " + value + " Sekunden gesetzt.");
                    } else if (input.equals("cancel")) {
                        cancelCountdown();
                        System.out.println("Countdown wurde abgebrochen.");
                    } else if (input.equals("status")) {
                        System.out.println("Countdown in Sekunden: " + getCountdown());
                    }
                } else {
                    System.out.println("Falsche Nutzereingabe. Erneut veruschen..");
                }
            }
        }, executorService);

        CompletableFuture<Void> countdownFuture = CompletableFuture.runAsync(() -> {
            while (isCountdownRunning) {
                try {
                    Thread.sleep(1000); // 1000 = 1 Sekunde
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isCountdownRunning) {
                    countdownTimeInSeconds--;
                    if (countdownTimeInSeconds <= 0) {
                        isCountdownRunning = false;
                        System.out.println("Countdown abgelaufen!");
                    }
                }
            }
        }, executorService);

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(userInputFuture, countdownFuture);

        try {
            combinedFuture.get(); // Wait for both threads to finish
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }

    private static void setCountdown(int value) {
        countdownTimeInSeconds = value;
    }

    private static void cancelCountdown() {
        isCountdownRunning = false;
    }

    private static int getCountdown() {
        return countdownTimeInSeconds;
    }

    private static boolean userinputIsCorrect(String input) {
        String regex = "(cancel|status|set\\s+-?[0-8]?\\d{0,8})";
        return input.matches(regex);
    }
}
