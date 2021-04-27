import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler extends ClientHandler {
    private String path = "server/serverFiles";
    private static int count;
    private byte[] buffer;

    public FileHandler(Socket socket, Server server) throws IOException {
        super(socket, server);
        buffer = new byte[256];
        count++;
        path = path + "/user" + count;
        if (!Files.exists(Paths.get(path))) {
            Files.createDirectory(Paths.get(path));
        }
    }

    private void init() throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            init();
            while (true) {
                String fileName = is.readUTF();
                System.out.println("fileName: " + fileName);
                long len = is.readLong();
                System.out.println("file length: " + len);
                String filePath = path + "/" + fileName;

                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    for (int i = 0; i < (len + 255) / 256; i++) {
                        int read = is.read(buffer);
                        fos.write(buffer, 0, read);
                    }
                }

                os.writeUTF("OK");
                os.flush();
                System.out.println("file was successfully upload");
            }
        } catch (Exception e) {
            System.err.println("Connection was broken!");
            try {
                close();
            } catch (IOException ioException) {
                System.err.println("Exception while socket close");
            }
        }
    }
}
