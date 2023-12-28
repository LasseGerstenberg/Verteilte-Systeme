import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Countdown extends Thread {
    private boolean stopThread = false;
    private int timeInSeconds = 100;
    private final BlockingQueue<String> countdownToUserInputQueue;
    private final BlockingQueue<String> userInputToCountdownQueue;

    public Countdown(BlockingQueue<String> countdownToUserInputQueue, BlockingQueue<String> userInputToCountdownQueue) {
        this.countdownToUserInputQueue = countdownToUserInputQueue;
        this.userInputToCountdownQueue = userInputToCountdownQueue;
    }

    public void run() {
        while (timeInSeconds > 0 && !stopThread) {
            pollUserInputToCountdownQueue();
            try {
                TimeUnit.SECONDS.sleep(1);
                timeInSeconds--;
                if(timeInSeconds <= 0) break;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        countdownToUserInputQueue.offer("canceled");
        System.out.println("Countdown beendet!");
    }

    private void pollUserInputToCountdownQueue() {
        String userInputQueueElement = userInputToCountdownQueue.poll();
        if(userInputQueueElement != null) {
            if(userInputQueueElement.startsWith("set")) {
                String[] splitString = userInputQueueElement.split(" ");
                timeInSeconds = Integer.parseInt(splitString[1]);
            } else if (userInputQueueElement.equals("remaining")) {
                countdownToUserInputQueue.offer(String.valueOf(timeInSeconds));
            } else if (userInputQueueElement.equals("cancel")) {
                stopThread = true;
            }
        }
    }

}
