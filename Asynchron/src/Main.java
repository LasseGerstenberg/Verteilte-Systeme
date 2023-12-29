import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static void main(String[] args) {
        ConcurrentLinkedQueue<String> userInputToCountdownQueue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> countdownToUserInputQueue = new ConcurrentLinkedQueue<>();

        Countdown countdown = new Countdown(countdownToUserInputQueue, userInputToCountdownQueue);
        UserInput userInput = new UserInput(countdownToUserInputQueue, userInputToCountdownQueue);

        userInput.start();
        countdown.start();
    }
}
