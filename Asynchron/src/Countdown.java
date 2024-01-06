import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class Countdown extends Thread {
    private boolean stopThread = false;
    private int remainingCountdown = 100;
    private final ConcurrentLinkedQueue<String> countdownToUserInputQueue;
    private final ConcurrentLinkedQueue<String> userInputToCountdownQueue;

    public Countdown(ConcurrentLinkedQueue<String> countdownToUserInputQueue, ConcurrentLinkedQueue<String> userInputToCountdownQueue) {
        this.countdownToUserInputQueue = countdownToUserInputQueue;
        this.userInputToCountdownQueue = userInputToCountdownQueue;
    }

    public void run() {
        // Prueft ob Eingaben durch die Nutzereingabe vorliegen und verringert den Timer
        while (remainingCountdown > 0 && !stopThread) {
            pollUserInputToCountdownQueue();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            remainingCountdown--;
            if(remainingCountdown <= 0) break;
        }
        //wird ausgefuehrt, wenn die Schleife beendet ist -> Countdown < 0 oder stopThread = true
        countdownToUserInputQueue.offer("canceled");
    }

    //Pruefung, ob in der Inputqueue der Nutzeringabe ein Element vorliegt. Wenn ja wird dieses verarbeitet.
    private void pollUserInputToCountdownQueue() {
        if (!userInputToCountdownQueue.isEmpty()) {
            String messageFromUserInput = userInputToCountdownQueue.poll();
            if(messageFromUserInput.startsWith("set")) {
                String[] splitString = messageFromUserInput.split(" ");
                int newRemainingCountdown = Integer.parseInt(splitString[1]);
                remainingCountdown = newRemainingCountdown;
                countdownToUserInputQueue.offer("Der Countdown wurde auf " + newRemainingCountdown + " Sekunden gesetzt.");
            } else if (messageFromUserInput.equals("remaining")) {
                countdownToUserInputQueue.offer("Der Countdown laeuft noch: " + String.valueOf(remainingCountdown) + " Sekunden");
            } else if (messageFromUserInput.equals("cancel")) {
                stopThread = true;
            }
        }
    }
}
