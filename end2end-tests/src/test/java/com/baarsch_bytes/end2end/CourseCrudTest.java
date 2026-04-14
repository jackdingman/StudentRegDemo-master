package com.baarsch_bytes.end2end;

import org.junit.jupiter.api.*; // Imports JUnit annotations like @BeforeEach
import org.openqa.selenium.*; //imports core selenium classes like WebDriver, WebElement
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*; //import waiting utilities - essential for React
import java.time.Duration; //used for wait time durations
import java.util.List; //store list of elements
import static org.junit.jupiter.api.Assertions.*; //import assertion methods like assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // goes in order, does create dependencies which isn't ideal for big real world projects

public class CourseCrudTest extends BaseTest {
    private static WebDriverWait wait; //allows us to wait for elements to show up which is essential with async UI.

    @BeforeAll
    public static void init() {

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:5173/"); // open the frontend ui page
        // Navigate to course list
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-course-list-link"))).click();
        // Wait for course form to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-course-fields")));
    }
    //C2.1 Create a Course
    @Test
    @Order(1)
    public void createCourse(){

        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        driver.findElement(By.id("new-course-name")).sendKeys("Software Testing");
        driver.findElement(By.id("new-course-instructor")).sendKeys("Dr. Smith");
        driver.findElement(By.id("new-course-max-size")).sendKeys("20");
        driver.findElement(By.id("new-course-room")).sendKeys("103");

        //JS Click
        WebElement button = driver.findElement(By.xpath("//button[text()='Add Course']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);

        // Wait for actual row creation
        wait.until(driver ->
                driver.findElements(By.cssSelector("tr[id^='course-row-']")).size() > before
        );

        int after = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        assertNotEquals(before, after);
    }
    @Test
    @Order(2)
    public void createCourse2(){

        driver.findElement(By.id("new-course-name")).sendKeys("Software Security");
        driver.findElement(By.id("new-course-instructor")).sendKeys("19");
        driver.findElement(By.id("new-course-max-size")).sendKeys("15");
        driver.findElement(By.id("new-course-room")).sendKeys("101");

        WebElement container = driver.findElement(By.id("new-course-fields"));
        WebElement button = container.findElement(By.tagName("button"));
        button.click();

    }
    @Test
    @Order(3)
    public void createCourse3(){

        driver.findElement(By.id("new-course-name")).sendKeys("Multimedia");
        driver.findElement(By.id("new-course-instructor")).sendKeys("20");
        driver.findElement(By.id("new-course-max-size")).sendKeys("10");
        driver.findElement(By.id("new-course-room")).sendKeys("1000");

        WebElement container = driver.findElement(By.id("new-course-fields"));
        WebElement button = container.findElement(By.tagName("button"));
        button.click();

    }
    //C2.2 Course Name < 1 Char
    @Order(4)
    @Test
    public void rejectEmptyCourseName(){
        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        driver.findElement(By.id("new-course-instructor")).sendKeys("2");
        driver.findElement(By.id("new-course-max-size")).sendKeys("10");
        driver.findElement(By.id("new-course-room")).sendKeys("100");

        WebElement container = driver.findElement(By.id("new-course-fields"));
        WebElement button = container.findElement(By.tagName("button"));
        button.click();

        int after = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        assertEquals(before, after);
    }

    //C2.3 Course Name too Long (>255chars)
    @Test
    @Order(5)
    public void rejectCourseNameTooLong() {
        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        driver.findElement(By.id("new-course-instructor")).sendKeys("3");
        driver.findElement(By.id("new-course-name")).sendKeys("f".repeat(256));
        driver.findElement(By.id("new-course-max-size")).sendKeys("10");
        driver.findElement(By.id("new-course-room")).sendKeys("100");

        WebElement container = driver.findElement(By.id("new-course-fields"));
        WebElement button = container.findElement(By.tagName("button"));
        button.click();

        int after = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        assertEquals(before, after); //ensure row count is the same as before
    }

    //C2.4 Empty Instructor
    @Test
    @Order(6)
    public void rejectEmptyInstructor() {
        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        //driver.findElement(By.id("new-course-instructor")).sendKeys("Dr. Pepper");
        driver.findElement(By.id("new-course-name")).sendKeys("Media");
        driver.findElement(By.id("new-course-max-size")).sendKeys("10");
        driver.findElement(By.id("new-course-room")).sendKeys("100");

        WebElement container = driver.findElement(By.id("new-course-fields"));
        WebElement button = container.findElement(By.tagName("button"));
        button.click();

        int after = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        assertEquals(before, after); //ensure row count is the same as before
    }


    //C2.5 reject max size of class < 1
    @Test
    @Order(7)
    public void rejectNegativeMaxSize() {
        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        driver.findElement(By.id("new-course-name")).clear();
        driver.findElement(By.id("new-course-name")).sendKeys("Media");
        driver.findElement(By.id("new-course-instructor")).clear();
        driver.findElement(By.id("new-course-instructor")).sendKeys("5");
        driver.findElement(By.id("new-course-max-size")).clear();
        driver.findElement(By.id("new-course-max-size")).sendKeys("-1");
        driver.findElement(By.id("new-course-room")).clear();
        driver.findElement(By.id("new-course-room")).sendKeys("100");

        WebElement container = driver.findElement(By.id("new-course-fields"));
        container.findElement(By.tagName("button")).click();

        int after = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();
        assertEquals(before, after);
    }

    //C2.6 reject empty max size
    @Test
    @Order(8)
    public void rejectEmptyMaxSize() {
        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        driver.findElement(By.id("new-course-instructor")).sendKeys("6");
        driver.findElement(By.id("new-course-name")).sendKeys("Art");
        //driver.findElement(By.id("new-course-max-size")).sendKeys("");
        driver.findElement(By.id("new-course-room")).sendKeys("100");

        WebElement container = driver.findElement(By.id("new-course-fields"));
        WebElement button = container.findElement(By.tagName("button"));
        button.click();

        int after = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();
        assertEquals(before, after); //ensure row count is the same as before
    }

    //C2.7 Create Room < 1
    @Test
    @Order(9)
    public void rejectRoomLessThan1() {
        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        driver.findElement(By.id("new-course-instructor")).sendKeys("7");
        driver.findElement(By.id("new-course-name")).sendKeys("Hacking");
        driver.findElement(By.id("new-course-max-size")).sendKeys("10");
        driver.findElement(By.id("new-course-room")).sendKeys("-1");

        WebElement container = driver.findElement(By.id("new-course-fields"));
        WebElement button = container.findElement(By.tagName("button"));
        button.click();

        int after = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();
        assertEquals(before, after); //ensure row count is the same as before
    }
    //C2.8 Create Empty Room
    @Test
    @Order(10)
    public void rejectEmptyRoom() {
        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        driver.findElement(By.id("new-course-instructor")).sendKeys("8");
        driver.findElement(By.id("new-course-name")).sendKeys("Intro to Business");
        driver.findElement(By.id("new-course-max-size")).sendKeys("10");
        //driver.findElement(By.id("new-course-room")).sendKeys();

        WebElement container = driver.findElement(By.id("new-course-fields"));
        WebElement button = container.findElement(By.tagName("button"));
        button.click();

        int after = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();
        assertEquals(before, after); //ensure row count is the same as before
    }
    //C2.9 Create Room with more than 255 chars
    @Test
    @Order(11)
    public void rejectRoomMoreThan255() {
        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();

        driver.findElement(By.id("new-course-instructor")).sendKeys("9");
        driver.findElement(By.id("new-course-name")).sendKeys("Hacking");
        driver.findElement(By.id("new-course-max-size")).sendKeys("10");
        driver.findElement(By.id("new-course-room")).sendKeys("1".repeat(256));

        WebElement container = driver.findElement(By.id("new-course-fields"));
        WebElement button = container.findElement(By.tagName("button"));
        button.click();

        int after = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();
        assertEquals(before, after); //ensure row count is the same as before
    }
    //C2.10 Create Course with roster of Valid Student Ids
    @Test
    @Order(12)
    public void createCourseWithRoster(){
        // Select student
        Select select = new Select(driver.findElement(By.id("select-student")));
        select.selectByVisibleText("Christopher Nolan");

        // Click add button
        driver.findElement(By.id("add-student-button")).click();
        select.selectByVisibleText("Christopher Nolan");
        driver.findElement(By.id("add-student-button")).click(); //add Christopher Nolan to row 1 class too
        WebElement rosterCell = driver.findElement(By.id("course-roster-1"));
        String rosterText = rosterCell.getText();
        assertTrue(rosterText.contains("Christopher Nolan"));
    }
    //C2.11 Create course with roster of invalid student ID
    @Test
    @Order(13)
    public void rejectInvalidStudentRoster(){
        WebElement row = driver.findElement(By.id("course-row-2"));
        WebElement rosterCell = row.findElement(By.id("course-roster-2"));
        String beforeRoster = rosterCell.getText();

        WebElement editButton = row.findElement(By.id("edit-course-button"));
        editButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-student-button")));

        // Click add without selecting a student
        driver.findElement(By.id("add-student-button")).click();

        // Dismiss the alert
        driver.switchTo().alert().dismiss();

        String afterRoster = driver.findElement(By.id("course-roster-2")).getText();
        assertEquals(beforeRoster, afterRoster);
    }
    //C2.12 Update Course
    @Test
    @Order(14)
    public void updateCourse(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("edit-course-button"))).click();

        WebElement field = driver.findElement(By.id("edit-course-name"));
        field.clear();
        field.sendKeys("Updated Course");

        driver.findElement(By.id("edit-course-save-button")).click();

        assertTrue(driver.getPageSource().contains("Updated Course"));
    }
    //C2.13 GET all courses
    @Test
    @Order(15)
    public void getAllCourses() {
        assertTrue(driver.findElement(By.id("course-list-table")).isDisplayed());
    }
    //C2.14 GET all courses returns correct response shape
    @Test
    @Order(16)
    public void verifyCourseTableStructure(){

        List<WebElement> rows = driver.findElements(By.cssSelector("tr[id^='course-row-']"));

        for(WebElement row : rows){ //loop through each row, finding the <td> in each row.
            assertNotNull(row.findElement(By.xpath(".//td"))); //assert that each row must contain at least one table cell
        }
    }
    //C2.15 GET Enrollment count
    @Test
    @Order(17)
    public void verifyEnrollmentCount(){
        List<WebElement> counts = driver.findElements(By.cssSelector("td[id^='course-roster-']"));
        assertNotNull(counts);
    }
    //C2.16 Verify that a Non Existing Course cannot be displayed
    @Test
    @Order(18)
    public void verifyNonExistingCourseNotDisplayed(){
        List<WebElement> matches = driver.findElements(
                By.xpath("//td[text()='-1']")  // exact match instead of contains
        );
        assertTrue(matches.isEmpty());
    }

}

