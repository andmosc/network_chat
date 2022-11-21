import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TCPGetSetting {

    Properties properties;

    public TCPGetSetting(String pathToFile) {
        final FileReader file;
        try {
            file = new FileReader(pathToFile);
            properties = new Properties();
            properties.load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getValue(String value) {
        return properties.getProperty(value);
    }
}
