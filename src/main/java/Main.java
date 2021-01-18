import java.io.IOException;
import java.util.Set;

/**
 * Самый главный класс.
 */
public class Main {

    private static CredentialProperties credentials;

    public static void main(String[] args) {
        setup();
        Instalike bot = new Instalike(
                credentials.getLogin(),
                credentials.getPassword(),
                Set.of("air.shop_by", "lime_travel", "socks_air", "air.cafe.by")
        );
        bot.start();
    }

    private static void setup() {
        // Log config.
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT [%4$s]: %5$s%6$s%n"
        );
        // Read credentials from file.
        try {
            credentials = new CredentialProperties();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Path to Chromedriver.
        System.setProperty(
                "webdriver.chrome.driver",
                "D:\\Install\\chromedriver_win32\\chromedriver.exe"
        );
    }
}