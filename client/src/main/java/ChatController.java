import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatController implements Initializable {
    private static final String clientPath = "client/clientFiles";
    public ListView<String> listView;
    public TextField input;
    private DataInputStream is;
    private DataOutputStream os;
    private byte[] buffer = new byte[256];

    public void send(ActionEvent actionEvent) throws IOException {
        String fileName = listView.getSelectionModel().getSelectedItem();
        os.writeUTF(fileName);
        long len = Files.size(Paths.get(clientPath, fileName));
        os.writeLong(len);

        try (FileInputStream fis = new FileInputStream(clientPath + "/" + fileName)) {
            int read;
            while (true) {
                read = fis.read(buffer);
                if (read == -1) {
                    break;
                }
                os.write(buffer, 0, read);
            }
        }
        os.flush();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            List<String> clientFiles = Files.list(Paths.get(clientPath)).map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());

            listView.getItems().addAll(clientFiles);

        } catch (Exception e) {
            System.err.println("Socket error");
        }
    }
}