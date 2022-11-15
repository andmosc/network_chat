import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientChat implements TCPConnectionListener {

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

        inputUser = new BufferedReader(new InputStreamReader(System.in));

        inputNickname();
        String msg;
        try {
            connection = new TCPConnection(IP_ADDR, PORT, this);
            while (true) {
                try {
                    msg = inputUser.readLine();
                    if (msg.equals("exit")) {
                        connection.sendMsg("user: (" + nickname + ") disconnect");
                        connection.sendMsg(msg);
                        connection.disconnect();
                        break;
                    }
                    connection.sendMsg(nickname + ": " + msg);
                } catch (IOException e) {
                    connection.disconnect();
                }
            }
        } catch (IOException e) {
            connection.disconnect();
            //todo добавить в лог ошибку соединения
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
            //todo добавить в лог
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        connection.sendMsg("new user: " + nickname);
        //todo добавить в лог
    }

    @Override
    public void onReceiveStrings(TCPConnection tcpConnection, String value) {
        System.out.println(value);
        //todo добавить в лог переписку
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("Connection close");
        //todo добавить в лог о выходе
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        //todo добавить в лог ошибку
    }
}
