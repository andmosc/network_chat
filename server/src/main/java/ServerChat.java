import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerChat implements TCPConnectionListener {

    public static void main(String[] args) {
        new ServerChat();
    }

    private final static String FILE_SETTING_PATH = "setting.txt";

    private final List<TCPConnection> connectionList = new ArrayList<>();
    private final TCPHistory history;
    private int port;


    public ServerChat() {
        getSettingServer();
        history = new TCPHistory();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server running!");
            while (true) {
                try {
                    new TCPConnection(serverSocket.accept(), this);
                } catch (IOException e) {
                    System.out.println("TCP exception: " + e);
                    //todo ошибка по соединению
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getSettingServer() {
        TCPGetSetting getSetting = new TCPGetSetting(FILE_SETTING_PATH);
        port = Integer.parseInt(getSetting.getValue("SERVER_PORT"));
    }

    private void sentToAllConnections(String value) {
        StringBuilder stringBuilder = new StringBuilder();
        Date time = new Date();
        SimpleDateFormat sdTime = new SimpleDateFormat("HH:mm:ss");

        String str = String.valueOf(stringBuilder
                .append("[")
                .append(sdTime.format(time))
                .append("] ")
                .append(value));

        System.out.println(str);
        history.addStory(str);
        connectionList.forEach(TCP -> TCP.sendMsg(str));
        //todo добавить в лог всю переписку
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        connectionList.add(tcpConnection);
        System.out.println("Client connected: " + tcpConnection);
        history.printHistory(tcpConnection.getOut());
        //todo добавить в лог Client connected:
    }


    @Override
    public void onReceiveStrings(TCPConnection tcpConnection, String value) {
        if (value.equals("exit")) {
            tcpConnection.disconnect();
        } else {
            sentToAllConnections(value);
        }
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("Client disconnect: " + tcpConnection);
        connectionList.remove(tcpConnection);
        //todo добавить информацию в лог Client disconnect:
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCP exception: " + e);
        //todo добавить об ошибках в лог
    }
}
