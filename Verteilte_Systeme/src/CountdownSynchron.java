import java.util.Scanner;

public class CountdownSynchron extends Thread {

    private static boolean isCountdownRunning = true;
    private static int countdownTimeInSeconds = 20;

    public static void main(String[] args) {

        // Userinput
        Thread userInputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (isCountdownRunning) {
                System.out.println("Bitte Befehl eingeben: set {Zahl}, cancel, status");
                String input = scanner.nextLine();

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
            }
        });

        // Countdown
        Thread countdownThread = new Thread(() -> {
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
        });

        userInputThread.start();
        countdownThread.start();

        try {
            userInputThread.join();
            countdownThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private synchronized static void setCountdown(int value) {
        countdownTimeInSeconds = value;
    }

    private synchronized static void cancelCountdown() {
        isCountdownRunning = false;
    }

    private synchronized static int getCountdown() {
        return countdownTimeInSeconds;
    }
}