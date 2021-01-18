import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Map.entry;

/**
 * Instalike.
 * Для работы требуется Google Chrome и Selenium с драйвером.
 */
class Instalike {
    private final Map<String, String> xpaths = Map.ofEntries(
            entry("login_button", "//*[@id=\"loginForm\"]/div/div[3]/button/div"),
            entry("first_photo", "/*//*[@id=\"react-root\"]/section/main/div/div[3]/article/div/div/div[1]/div[1]"),
            entry("like_button", "/html/body/div[5]/div[2]/div/article/div[3]/section[1]/span[1]/button"),
            entry("next_button_first", "/html/body/div[5]/div[1]/div/div/a"),
            entry("next_button_default", "/html/body/div[3]/div/div[1]/div/div/a[2]"),
            entry("next_button_last", "/html/body/div[3]/div/div[1]/div/div/a")
    );
    private final String login;
    private final String password;
    private final Set<String> target;
    private ChromeDriver browser;
    private int baseDelay;
    private Logger logger;

    Instalike(String login, String password, Set<String> target) {
        this(login, password, target, 3);
    }

    Instalike(String login, String password, Set<String> target, int baseDelay) {
        this.login = login;
        this.password = password;
        this.target = target;
        this.baseDelay = baseDelay;
        logger = Logger.getLogger(Instalike.class.getName());
    }

    void start() {
        log("Instalike начал свою работу для лайкания указанных пользователей!");
        browser = new ChromeDriver();
        login();
        likeProfiles();
        end();
    }

    private void likeProfiles() {
        for (String s : target) {
            if (findFirst(s)) {
                likeCurrent();
                for (int i = 0; i < 4; i++) {
                    if (getNext()) {
                        likeCurrent();
                    }
                }
            }
        }
    }

    private void login() {
        browser.get("https://www.instagram.com/accounts/login/");
        waitASecond();
        browser.findElement(new By.ByName("username")).sendKeys(login);
        waitASecond();
        browser.findElement(new By.ByName("password")).sendKeys(password);
        browser.findElement(new By.ByXPath(xpaths.get("login_button"))).click();
        waitASecond();
        log(String.format("Залогинились, как %s", login));
    }

    /**
     * Find first photo in profile.
     *
     * @return True, if found, otherwise False.
     */
    private boolean findFirst(String targetValue) {
        browser.get(String.format("https://www.instagram.com/%s", targetValue));
        waitASecond();
        log(String.format("Ставим лайки для %s.", targetValue));
        try {
            browser.findElement(new By.ByXPath(xpaths.get("first_photo"))).click();
            waitASecond();
            return true;
        } catch (NoSuchElementException ex) {
            warn("Не могу найти первую фотку! Наверное, фоток нет вообще!");
            return false;
        }
    }

    private void likeCurrent() {
        WebElement likeButton = browser.findElement(new By.ByXPath(xpaths.get("like_button")));
        try {
            List<WebElement> elements = browser.findElements(new By.ByTagName("svg"));
            boolean isLiked = false;
            for (WebElement element : elements) {
                if (("Не нравится").equals(element.getAttribute("aria-label"))) {
                    log("Тут лайк уже стоит!");
                    isLiked = true;
                }
            }
            if (!isLiked) {
                throw new NoSuchElementException("Лайк ещё не стоит!");
            }
        } catch (NoSuchElementException ex) {
            likeButton.click();
            log("Лайк поставлен!");
            waitASecond();
        }
    }

    /**
     * Open next photo
     *
     * @return True if open, otherwise False.
     */
    private boolean getNext() {
        try {
            browser.findElement(new By.ByClassName("coreSpriteRightPaginationArrow")).click();
            waitASecond();
            return true;
        } catch (NoSuchElementException e) {
            warn("Не могу открыть следующее фото. Кажется, закончились.");
            return false;
        }
    }

    private void end() {
        browser.close();
        log("Работы выполнена!");
    }

    private void waitASecond() {
        try {
            Thread.sleep((baseDelay + ThreadLocalRandom.current().nextInt(1, 3)) * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void log(String message) {
        logger.log(Level.INFO, String.format("Instalike: %s", message));
    }

    private void warn(String message) {
        logger.log(Level.WARNING, String.format("Instalike: %s", message));
    }
}
