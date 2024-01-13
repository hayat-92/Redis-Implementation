import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable {

    private Socket clientSocket;

    public Client(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            String inputLine;
            List<String> commandList = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) {
                commandList.add(inputLine);
//                System.out.println(inputLine);
                if (commandList.size()==1 && commandList.get(0).equalsIgnoreCase("ping")) {
                    out.println("+PONG");
                    out.flush();
                } else if (commandList.size() - 3 >=0 && commandList.get(commandList.size() - 3).equalsIgnoreCase("echo")) {
                    int len = Integer.parseInt(commandList.get(commandList.size() - 2).substring(1));
                    out.println("$" + len + commandList.get(commandList.size() - 1));
                    out.flush();
                }
            }
            System.out.println("Faisal");
            System.out.println("Received command: " + commandList.get(commandList.size() - 1));

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
