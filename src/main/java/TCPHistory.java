import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

public class TCPHistory {

    private static final int SIZE_LIST = 10;
    private static final LinkedList<String> listHistory = new LinkedList<>();

    public synchronized String addStory(String msg) {
        if (listHistory.size() > SIZE_LIST) {
            listHistory.removeFirst();
        }
        if (!msg.equals("exit")) {
            listHistory.add(msg);
        }
        return msg;
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
            }
        }
    }

}
