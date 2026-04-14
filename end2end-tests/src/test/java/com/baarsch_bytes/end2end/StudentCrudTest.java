package com.baarsch_bytes.end2end;

import org.junit.jupiter.api.*; // Imports JUnit annotations like @BeforeEach
import org.openqa.selenium.*; //imports core selenium classes like WebDriver, WebElement
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*; //import waiting utilities - essential for React
import java.time.Duration; //used for wait time durations
import java.util.List; //store list of elements
import static org.junit.jupiter.api.Assertions.*; //import assertion methods like assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentCrudTest extends BaseTest{
    private static WebDriverWait wait; //allows us to wait for elements to show up which is essential with async UI.

    @BeforeAll
    public static void init(){

        //initialize with max 10 seconds
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:5173/students"); // open the frontend ui page

        //driver.findElement(By.id("nav-student-list-link")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-student-fields"))); //wait until student form is visible before clicking
    }

    //S1.1 Create Student
    @Test
    @Order(1)
    public void testCreateStudent(){
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-student-fields")));

        driver.findElement(By.id("new-student-name")).sendKeys("Jack Dingman"); //enter name
        driver.findElement(By.id("new-student-major")).sendKeys("Computer Science"); //enter major
        driver.findElement(By.id("new-student-gpa")).sendKeys("4.0"); //enter gpa
        driver.findElement(By.id("add-student-button")).click(); //click the add student button

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("student-list-table")));

        assertTrue(driver.getPageSource().contains("Jack Dingman"));
    }
    @Test
    @Order(2)
    public void testCreateStudent2() throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("new-student-name")));

        WebElement nameField = driver.findElement(By.id("new-student-name"));
        WebElement majorField = driver.findElement(By.id("new-student-major"));
        WebElement gpaField = driver.findElement(By.id("new-student-gpa"));
        // Print what's currently IN the fields before we do anything
        System.out.println("BEFORE - name: " + nameField.getAttribute("value"));
        System.out.println("BEFORE - major: " + majorField.getAttribute("value"));
        System.out.println("BEFORE - gpa: " + gpaField.getAttribute("value"));

        nameField.click();
        nameField.click();
        nameField.click();
        Thread.sleep(5000);
        nameField.sendKeys("Christopher Nolan");

        majorField.click();
        majorField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        majorField.sendKeys("Film");

        gpaField.click();
        gpaField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        gpaField.sendKeys("2.5");

        // Print what's in the fields AFTER we set them
        System.out.println("AFTER - name: " + nameField.getAttribute("value"));
        System.out.println("AFTER - major: " + majorField.getAttribute("value"));
        System.out.println("AFTER - gpa: " + gpaField.getAttribute("value"));

        int before = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();
        System.out.println("Row count before click: " + before);

        driver.findElement(By.id("add-student-button")).click();
        Thread.sleep(5000);

        int after = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();
        System.out.println("Row count after click: " + after);

        assertTrue(after > before, "Row count should have increased");
    }
    @Test
    @Order(3)
    public void testCreateStudent3(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("new-student-name")));

        driver.findElement(By.id("new-student-name")).clear();
        driver.findElement(By.id("new-student-name")).sendKeys("Spike Spiegal");

        driver.findElement(By.id("new-student-major")).clear();
        driver.findElement(By.id("new-student-major")).sendKeys("Aviation");

        driver.findElement(By.id("new-student-gpa")).clear();
        driver.findElement(By.id("new-student-gpa")).sendKeys("3.5");

        int before = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();
        driver.findElement(By.id("add-student-button")).click();

        wait.until(d ->
                d.findElements(By.cssSelector("tr[id^='student-row-']")).size() > before
        );

        assertTrue(driver.getPageSource().contains("Spike Spiegal"));
    }
    //S1.2 Reject Student > 255 chars
    @Test
    @Order(4)
    public void rejectNameTooLong(){

        // Count rows BEFORE action
        int before = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        driver.findElement(By.id("new-student-name")).sendKeys("F".repeat(256));//enter 256 characters into name
        driver.findElement(By.id("new-student-major")).sendKeys("CyberSecurity");
        driver.findElement(By.id("new-student-gpa")).sendKeys("2.5");
        driver.findElement(By.id("add-student-button")).click();

        // Wait briefly for UI update (important)
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(d -> true); // simple delay substitute

        // Count rows AFTER action
        int after = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        // Assert NO new row added
        assertEquals(before, after, "Invalid student should NOT be added");
    }
    //S1.3 Reject Missing Name
    @Test
    @Order(5)
    public void rejectMissingName(){

        // Count rows BEFORE action
        int before = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        //driver.findElement(By.id("new-student-name")).sendKeys();//enter 256 characters into name
        driver.findElement(By.id("new-student-major")).sendKeys("CS");
        driver.findElement(By.id("new-student-gpa")).sendKeys("2.5");
        driver.findElement(By.id("add-student-button")).click();

        // Wait briefly for UI update (important)
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(d -> true); // simple delay substitute

        int after = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        // Assert NO new row added
        assertEquals(before, after, "Invalid student should NOT be added");
    }
    //S1.4 Reject Missing Major
    @Test
    @Order(6)
    public void rejectMissingMajor(){

        // Count rows BEFORE action
        int before = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        driver.findElement(By.id("new-student-name")).sendKeys("Big fella");
        //driver.findElement(By.id("new-student-major")).sendKeys("CyberSecurity");
        driver.findElement(By.id("new-student-gpa")).sendKeys("2.5");
        driver.findElement(By.id("add-student-button")).click();

        // Wait briefly for UI update (important)
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(d -> true); // simple delay substitute

        int after = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        // Assert NO new row added
        assertEquals(before, after, "Invalid student should NOT be added, missing Major");
    }
    //S1.5 Reject Major > 255 chars
    @Test
    @Order(7)
    public void rejectMajorTooManyChars(){

        // Count rows BEFORE action
        int before = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        driver.findElement(By.id("new-student-name")).sendKeys("Big fella");
        driver.findElement(By.id("new-student-major")).sendKeys("f".repeat(256)); //256 chars in major slot
        driver.findElement(By.id("new-student-gpa")).sendKeys("2.5");
        driver.findElement(By.id("add-student-button")).click();

        // Wait briefly for UI update (important)
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(d -> true); // simple delay substitute

        int after = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        // Assert NO new row added
        assertEquals(before, after, "Invalid student should NOT be added, missing Major");
    }


    //S1.6 Reject GPA < 0
    @Test
    @Order(8)
    public void rejectNegativeGpa(){
        int before = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();
        driver.findElement(By.id("new-student-name")).sendKeys("Big fella");
        driver.findElement(By.id("new-student-major")).sendKeys("CyberSecurity");
        driver.findElement(By.id("new-student-gpa")).sendKeys("-4.0");
        driver.findElement(By.id("add-student-button")).click();

        // Wait briefly for UI update (important)
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(d -> true); // simple delay substitute

        int after = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        // Assert NO new row added
        assertEquals(before, after, "Invalid student should NOT be added, negative GPA");

    }
    //S1.7 Reject GPA > 4
    @Test
    @Order(9)
    public void rejectGpaGreaterThan4(){
        int before = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();
        driver.findElement(By.id("new-student-name")).sendKeys("Big fella");
        driver.findElement(By.id("new-student-major")).sendKeys("CyberSecurity");
        driver.findElement(By.id("new-student-gpa")).sendKeys("5.0");
        driver.findElement(By.id("add-student-button")).click();

        // Wait briefly for UI update (important)
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(d -> true); // simple delay substitute

        int after = driver.findElements(By.cssSelector("tr[id^='student-row-']")).size();

        // Assert NO new row added
        assertEquals(before, after, "Invalid student should NOT be added, negative GPA");

    }
    //S1.8 GET a student
    @Test
    @Order(10)
    public void testGetStudent(){
        assertTrue(driver.findElement(By.id("student-list-table")).isDisplayed());
    }
    //S1.9 Get all Students
    @Test
    @Order(11)
    public void testGetAllStudents(){
        //find all rows that match student and row patterns
        assertNotNull(driver.findElements(By.cssSelector("tr[id^='student-row']")));
    }
    //S1.10 GET a student that does not exist
    @Test
    @Order(12)
    public void testNonExistentStudent(){
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("student-list-table")));

        String fakeName = "FakeStudent";

        // Look for any table cell containing that name
        List<WebElement> matches = driver.findElements(
                By.xpath("//td[contains(text(),'" + fakeName + "')]")
        );

        // Assert that NO such element exists
        assertTrue(matches.isEmpty(), "Fake student should NOT appear in table");
    }

    //S1.11 Update Student
    @Test
    @Order(13)
    public void updateStudent(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("edit-student-button"))).click();
        driver.findElement(By.id("edit-student-name")).clear(); //clear existing student name entry
        driver.findElement(By.id("edit-student-name")).sendKeys("George Washington"); //send name into box
        driver.findElement(By.id("edit-student-save-button")).click();
        assertTrue(driver.getPageSource().contains("George Washington"));
    }


    //S1.12 Update Student, No Name Submission
    @Test
    @Order(14)
    public void rejectUpdateStudentNoName(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("edit-student-button"))).click();
        driver.findElement(By.id("edit-student-name")).clear();
        driver.findElement(By.id("edit-student-save-button")).click();
        assertTrue(driver.getPageSource().contains("George Washington")); //George washington is in row 1 from last test
    }
    //C1.13 Delete a Student
    @Test
    @Order(15)
    public void deleteStudent(){
        // Find the row containing George Washington
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[td[contains(text(),'George Washington')]]")
        ));

        // Click the delete button inside that row
        row.findElement(By.id("delete-student-button")).click();

        // Wait for George Washington to disappear
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//td[contains(text(),'George Washington')]")
        ));

        List<WebElement> matches = driver.findElements(
                By.xpath("//td[contains(text(),'George Washington')]")
        );
        assertTrue(matches.isEmpty());
    }

}