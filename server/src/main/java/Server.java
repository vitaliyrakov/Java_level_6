import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {

    private final ConcurrentLinkedQueue<ClientHandler> clients;

    public Server(int port) {
        clients = new ConcurrentLinkedQueue<>();
        try {

            ServerSocket server = new ServerSocket(port);
            System.out.println("Server started!");

            while (true) {
                Socket socket = server.accept();
                System.out.println("Client accepted");
                ClientHandler handler = new FileHandler(socket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.err.println("Server was broken");
        }
    }

    public void broadCast(String message) throws IOException {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void kickClient(ClientHandler handler) {
        clients.remove(handler);
    }

}
