import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

public class TCPHistory {

    private static final int CAPACITY = 10;
    private static final LinkedList<String> listHistory = new LinkedList<>();

    public synchronized void addStory(String msg) {
        if (listHistory.size() > CAPACITY) {
            listHistory.removeFirst();
        }
        if (!msg.equals("exit")) {
            listHistory.add(msg);
        }
    }

    public synchronized void printHistory(BufferedWriter out) {
        if (!listHistory.isEmpty()) {
            try {
                out.write("History message: " + "\n");
                for (String msg : listHistory) {
                    out.write("Story: " + msg + "\n");
                }
                out.write("----- end history -----" + "\n");
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
