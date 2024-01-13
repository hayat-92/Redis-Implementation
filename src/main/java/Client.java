import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements Runnable {

    private Socket clientSocket;

    public Client(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        Map<String, String> map = new HashMap<String, String>();
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        ) {
            List<String> elements = new ArrayList<String>();
            int elementCount = 0;
            String line;
            while ((line = in.readLine()) != null) {
                elements.add(line);
                if (elements.size() == 1) {
                    elementCount = 1 + 2 * Integer.parseInt(line.substring(1));
                }
                if (elements.size() == elementCount) {
                    String command = elements.get(2);
                    if (command.equals("ping")) {
                        out.print("+PONG\r\n");
                        out.flush();
                    } else if (command.equals("echo")) {
                        String message = elements.get(4);
                        // $3\r\nhey\r\n
                        out.printf("$%d\r\n%s\r\n", message.length(), message);
                        out.flush();
                    }else if(command.equals("set")){
                        String key = elements.get(4);
                        String value = elements.get(6);
                        map.put(key, value);
                        out.print("+OK\r\n");
                        out.flush();
                    }else if(command.equals("get")){
                        String key = elements.get(4);
                        if(map.containsKey(key)) {
                            String value = map.get(key);
                            out.printf("$%d\r\n%s\r\n", value.length(), value);
                            out.flush();
                        }else{
                            out.print("$-1\r\n");
                            out.flush();
                        }
                    }
                    elements.clear();
                    elementCount = 0;
                }
            }

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
