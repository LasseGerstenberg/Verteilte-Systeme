import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            int port = 5001;
            Countdown serverThread = new Countdown(port);
            UserInput clientThread = new UserInput("127.0.0.1", port);

            serverThread.start();
            clientThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
