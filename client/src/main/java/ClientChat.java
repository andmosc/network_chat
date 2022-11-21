import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientChat implements TCPConnectionListener {

    private final static Logger logger = LoggerFactory.getLogger(ClientChat.class);

    public static void main(String[] args) {
        new ClientChat();
    }

    private static String IP_ADDR;
    private static int PORT;
    private final static String FILE_SETTING_PATH = "setting.txt";

    private final BufferedReader inputUser;
    private String nickname;
    private TCPConnection connection;
    private TCPGetSetting getSetting;

    public ClientChat() {
        getSetting = new TCPGetSetting(FILE_SETTING_PATH);
        IP_ADDR = getSetting.getValue("SERVER_HOST");
        PORT = Integer.parseInt(getSetting.getValue("SERVER_PORT"));
        logger.debug("getting settings from a file: {}, port: {}", IP_ADDR, PORT);

        inputUser = new BufferedReader(new InputStreamReader(System.in));

        inputNickname();
        logger.debug("user entered nickname: {}", nickname);

        String msg;
        try {
            connection = new TCPConnection(IP_ADDR, PORT, this);
            logger.debug("connecting to the server: {}", connection);
            while (true) {
                try {
                    msg = inputUser.readLine();
                    if (msg.equals("exit")) {
                        connection.sendMsg("user: (" + nickname + ") disconnect");
                        logger.info("user:({}) disconnected", nickname);
                        connection.sendMsg(msg);
                        connection.disconnect();
                        break;
                    }
                    connection.sendMsg(nickname + ": " + msg);
                } catch (IOException e) {
                    connection.disconnect();
                    logger.error("inputUser.readLine()", e);
                }
            }
        } catch (IOException e) {
            connection.disconnect();
            logger.error("new TCPConnection", e);
        }
    }

    private void inputNickname() {
        try {
            do {
                System.out.print("Enter your nickname: ");
                nickname = inputUser.readLine();
            } while (nickname.equals(""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        connection.sendMsg("new user: " + nickname);
        logger.debug("sending a message to the server -> new user: {}", nickname);
    }

    @Override
    public void onReceiveStrings(TCPConnection tcpConnection, String value) {
        System.out.println(value);
        logger.info(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("Connection close");
        logger.debug("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("onException: " + e);
        logger.error("onException: ",e);
    }
}
