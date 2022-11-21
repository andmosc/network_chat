import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TCPGetSettingTest {
    private TCPGetSetting tcpGetSetting;
    private final static String FILE_SETTING_PATH = "src/test/java/resources/setting.txt";

    @BeforeEach
    public void setUp() {
        tcpGetSetting = new TCPGetSetting(FILE_SETTING_PATH);
    }

    @ParameterizedTest
    @MethodSource("sourceProperties")
    public void getValueTest(String value,String key) {
        Assertions.assertEquals(tcpGetSetting.getValue(value),key);
    }

    private Stream<Arguments> sourceProperties() {
        return Stream.of(
                arguments("SERVER_HOST","127.0.0.1"),
                arguments("SERVER_PORT","8080"));
    }
}