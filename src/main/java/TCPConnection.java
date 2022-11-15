import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TCPConnection {
    private final Socket socket;
    private final BufferedWriter out;
    private final BufferedReader in;
    private final TCPConnectionListener eventListener;
    private Thread thread;

    public TCPConnection(String ipAddres, int port, TCPConnectionListener eventListener) throws IOException {
        this(new Socket(ipAddres, port), eventListener);
    }

    public TCPConnection(Socket socket, TCPConnectionListener eventListener) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        thread = new Thread(() -> {
            try {
                eventListener.onConnectionReady(TCPConnection.this);
                while (!thread.isInterrupted()) {
                    eventListener.onReceiveStrings(TCPConnection.this, in.readLine());
                }
            } catch (IOException e) {
                eventListener.onException(TCPConnection.this, e);
            } finally {
                eventListener.onDisconnect(TCPConnection.this);
            }
        });
        thread.start();
    }

    public synchronized void sendMsg(String value) {
        try {
            out.write(value + "\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "connection: " + socket.getInetAddress() + " : " + socket.getPort();
    }

    public BufferedWriter getOut() {
        return out;
    }
}
