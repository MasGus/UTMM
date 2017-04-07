package data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

/**
 * Created by Maria.Guseva on 16.03.2017.
 */
public class GisParsing {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String ADDRESSES_PATH = System.getProperty("user.dir") + "/src/test/resources/Addresses.txt";
    private final String OUTFILE_PATH = System.getProperty("user.dir") + "/src/test/resources/data.txt";
    private final String DIRECTIONS_PATH = System.getProperty("user.dir") + "/src/test/resources/Directions.txt";

    @Before
    public void start(){
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void gisParsing() throws FileNotFoundException {
        driver.get("https://2gis.ru/spb");
        driver.findElement(By.linkText("Проезд")).click();
        int lastDirection = getLastDirection();
        List<String> directions = getNextDirections(lastDirection);
        String info = "";
        char c = ' ';
        String from = "";
        String to = "";
        String direction = "";
        int j = 0;
        File file = new File(OUTFILE_PATH);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(OUTFILE_PATH, true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            for (int i = 0; i < directions.size(); i++){
                direction = directions.get(i);
                while (c != ';'){
                    c = direction.charAt(j);
                    from += c;
                    j++;
                }
                j++;
                while (j < direction.length()){
                    c = direction.charAt(j);
                    to += c;
                    j++;
                }
                info = direction + ";";
                setDirection(from, to);
                info += infoByPublic() + ";" + infoByMetro() + ";" + infoByCar() + "\n";
                bufferWriter.write(info);
                c = ' ';
            }
            bufferWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @After
    public void stop() {
        driver.quit();
        driver = null;
    }
    
    private int getLastDirection(){
        int i = 0;
        try{
            FileReader reader = new FileReader(OUTFILE_PATH);
            BufferedReader in = new BufferedReader(reader);
            String address;
            while ((address = in.readLine()) != null){
                i++;
            }
            in.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return i;
    }

    private List<String> getNextDirections(int i){
        List<String> directions = new ArrayList<String>();

        try{
            FileReader reader = new FileReader(DIRECTIONS_PATH);
            BufferedReader in = new BufferedReader(reader);
            String direction;
            int j = 0;
            while ((direction = in.readLine()) != null){
                if (j > i) {
                    directions.add(direction);
                }
                j++;
            }
            in.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return directions;
    }

    private void setDirection(String from, String to){
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        List<WebElement> textBoxes = driver.findElements(By.cssSelector(".suggest__input"));
        textBoxes.get(1).clear();
        textBoxes.get(1).sendKeys(from);
        textBoxes.get(1).sendKeys(Keys.ENTER);
        textBoxes.get(2).clear();
        textBoxes.get(2).sendKeys(to);
        textBoxes.get(2).sendKeys(Keys.ENTER);
    }

    private String infoByPublic(){
        String res = "";
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebElement elementToClick = wait.until(visibilityOfElementLocated(By.cssSelector(".searchBar__transportButton.searchBar__transportBus")));
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0," + elementToClick.getLocation().y + ")");
        try {
            elementToClick.click();
        } catch (WebDriverException e) { }
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        elementToClick.click();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        res += driver.findElement(By.cssSelector(".routeResults__time")).getText().replace(" ", "");
        res += " ";
        res += driver.findElement(By.cssSelector(".routeResults__transfers")).getText().charAt(0);

        return res;
    }

    private String infoByMetro(){
        String res = "";
        driver.findElement(By.cssSelector(".searchBar__transportButton.searchBar__transportSubway")).click();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        if (driver.findElements(By.cssSelector(".routeResults__time")).size() != 0){
            res += driver.findElement(By.cssSelector(".routeResults__time")).getText().replace(" ", "");
            res += " ";
            res += driver.findElement(By.cssSelector(".routeResults__transfers")).getText().charAt(0);
        }

        return res;
    }

    private String infoByCar(){
        String res = "";
        driver.findElement(By.cssSelector(".searchBar__transportButton.searchBar__transportCar")).click();
        res += driver.findElement(By.cssSelector(".autoResults__routeHeaderContentDuration")).getText().replace(" ", "");
        res += " ";
        res += driver.findElement(By.cssSelector(".autoResults__routeHeaderContentLength")).getText().replace(" ", "");

        return res;
    }
}
