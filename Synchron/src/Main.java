import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        LinkedBlockingQueue<String> userInputToCountdownQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> countdownToUserInputQueue = new LinkedBlockingQueue<>();

        Countdown countdown = new Countdown(countdownToUserInputQueue, userInputToCountdownQueue);
        UserInput userInput = new UserInput(countdownToUserInputQueue, userInputToCountdownQueue);

        userInput.start();
        countdown.start();
    }
}