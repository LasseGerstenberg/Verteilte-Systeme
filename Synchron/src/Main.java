import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
            int port = 5001;
            Countdown countdownThread = new Countdown(port);
            UserInput userInputThread = new UserInput("127.0.0.1", port);

            countdownThread.start();
            userInputThread.start();
    }
}
