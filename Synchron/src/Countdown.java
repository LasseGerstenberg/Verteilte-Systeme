import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class Countdown extends Thread {
    private boolean stopThread = false;
    private int timeInSeconds = 100;
    private final LinkedBlockingQueue<String> countdownToUserInputQueue;
    private final LinkedBlockingQueue<String> userInputToCountdownQueue;

    public Countdown(LinkedBlockingQueue<String> countdownToUserInputQueue, LinkedBlockingQueue<String> userInputToCountdownQueue) {
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
        try {
            countdownToUserInputQueue.put("canceled");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void pollUserInputToCountdownQueue() {
        try {
            while (!userInputToCountdownQueue.isEmpty()) {
                String userInputQueueElement = userInputToCountdownQueue.take();
                if(userInputQueueElement.startsWith("set")) {
                    String[] splitString = userInputQueueElement.split(" ");
                    timeInSeconds = Integer.parseInt(splitString[1]);
                } else if (userInputQueueElement.equals("remaining")) {
                    countdownToUserInputQueue.put(String.valueOf(timeInSeconds));
                } else if (userInputQueueElement.equals("cancel")) {
                    stopThread = true;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}