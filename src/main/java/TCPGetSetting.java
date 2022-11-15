import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TCPGetSetting {
    private FileReader file;
    Properties properties;
    private String pathToFile;

    public TCPGetSetting(String pathToFile) {
        try {
            file = new FileReader(pathToFile);
            properties = new Properties();
            properties.load(file);
        } catch (IOException e) {
            //todo информацию в лог об ошибки
        }
    }

    public String getValue(String value) {
        return properties.getProperty(value);
    }
}
