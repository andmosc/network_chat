import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerChat implements TCPConnectionListener {

    private final static Logger logger = LoggerFactory.getLogger(ServerChat.class);

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
            logger.debug("Server running!");
            System.out.println("Server running!");
            while(true) {
                try {
                    logger.debug("new connection: {}", new TCPConnection(serverSocket.accept(), this));
                } catch (IOException e) {
                    System.out.println("TCP exception: " + e);
                    logger.error("TCP exception: ", e);
                }
            }
        } catch (IOException e) {
            logger.error("serverSocket: ",new RuntimeException(e));
        }
    }

    private void getSettingServer() {
        TCPGetSetting getSetting = new TCPGetSetting(FILE_SETTING_PATH);
        port = Integer.parseInt(getSetting.getValue("SERVER_PORT"));
        logger.debug("received server settings, port: {}", port);
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
        logger.info(value);
        history.addStory(str);
        connectionList.forEach(TCP -> TCP.sendMsg(str));
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        connectionList.add(tcpConnection);
        System.out.println("Client connected: " + tcpConnection);
        history.printHistory(tcpConnection.getOut());
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
        logger.info("Client disconnect: {}", tcpConnection);
        connectionList.remove(tcpConnection);
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCP exception: " + e);
        logger.error("TCP exception: ", e);
    }
}
