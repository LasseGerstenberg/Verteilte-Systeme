import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        BlockingQueue<String> userInputToCountdownQueue = new LinkedBlockingQueue<>();
        BlockingQueue<String> countdownToUserInputQueue = new LinkedBlockingQueue<>();

        Countdown countdown = new Countdown(countdownToUserInputQueue, userInputToCountdownQueue);
        UserInput userInput = new UserInput(countdownToUserInputQueue, userInputToCountdownQueue);

        userInput.start();
        countdown.start();
    }
}
