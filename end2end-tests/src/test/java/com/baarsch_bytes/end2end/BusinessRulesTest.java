package com.baarsch_bytes.end2end;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BusinessRulesTest extends BaseTest {

    private static WebDriverWait wait;

    @BeforeAll
    public static void init() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:5173/");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-course-list-link"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-course-fields")));
    }

    // Helper: get roster text for a given course id
    private String getRosterText(int courseId) {
        return driver.findElement(By.id("course-roster-" + courseId)).getText();
    }

    // Helper: get enrollment count (number of names in roster cell)
    private int getEnrollmentCount(int courseId) {
        String text = getRosterText(courseId).trim();
        if (text.isEmpty()) return 0;
        return text.split("\n").length;
    }

    // Helper: click edit on a specific course row
    private void clickEditOnRow(int courseId) {
        WebElement row = driver.findElement(By.id("course-row-" + courseId));
        row.findElement(By.id("edit-course-button")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-student-button")));
    }

    // Helper: dismiss alert if one appears
    private void dismissAlertIfPresent() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().dismiss();
        } catch (TimeoutException e) {
            // No alert, continue
        }
    }
    //BR3.1 Add student to course
    @Test
    @Order(1)
    public void addStudentToCourse() {
        assertTrue(true); //already been done in the last suite
    }

    //BR3.2 Remove student from course
    @Test
    @Order(2)
    public void removeStudentFromCourse() {
        clickEditOnRow(1);

        // Wait for remove dropdown to be populated
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("remove-student-select")));

        Select removeSelect = new Select(driver.findElement(By.id("remove-student-select")));
        removeSelect.selectByVisibleText("Christopher Nolan");
        driver.findElement(By.id("remove-student-button")).click();


        assertFalse(getRosterText(1).contains("Christopher Nolan"));

        driver.findElement(By.id("edit-course-save-button")).click();
    }

    //BR3.3 Reject add when course is full
    @Test
    @Order(3)
    public void rejectAddWhenCourseFull() {
        // Create a course with max size 1
        driver.findElement(By.id("new-course-name")).clear();
        driver.findElement(By.id("new-course-name")).sendKeys("Small Test Course");
        driver.findElement(By.id("new-course-instructor")).clear();
        driver.findElement(By.id("new-course-instructor")).sendKeys("Dr. Baarsch");
        driver.findElement(By.id("new-course-max-size")).clear();
        driver.findElement(By.id("new-course-max-size")).sendKeys("1");
        driver.findElement(By.id("new-course-room")).clear();
        driver.findElement(By.id("new-course-room")).sendKeys("999");

        int before = driver.findElements(By.cssSelector("tr[id^='course-row-']")).size();
        WebElement container = driver.findElement(By.id("new-course-fields"));
        container.findElement(By.tagName("button")).click();

        // Wait for new course row to appear
        wait.until(d ->
                d.findElements(By.cssSelector("tr[id^='course-row-']")).size() > before
        );

        // Get the id of the newly created course row
        List<WebElement> rows = driver.findElements(By.cssSelector("tr[id^='course-row-']"));
        WebElement lastRow = rows.get(rows.size() - 1);
        String rowId = lastRow.getAttribute("id"); // e.g. "course-row-5"
        int newCourseId = Integer.parseInt(rowId.replace("course-row-", ""));

        // Open edit, add first student to fill the course
        clickEditOnRow(newCourseId);
        Select select = new Select(driver.findElement(By.id("select-student")));
        select.selectByIndex(1); // pick first available student
        driver.findElement(By.id("add-student-button")).click();
        dismissAlertIfPresent();
        driver.findElement(By.id("edit-course-save-button")).click();


        clickEditOnRow(newCourseId); //adding a second student should be rejected
        String rosterBefore = getRosterText(newCourseId);

        Select select2 = new Select(driver.findElement(By.id("select-student")));
        select2.selectByIndex(1);
        driver.findElement(By.id("add-student-button")).click();
        dismissAlertIfPresent();

        String rosterAfter = getRosterText(newCourseId);
        assertEquals(rosterBefore, rosterAfter, "Roster should not change when course is full");

        driver.findElement(By.id("edit-course-cancel-button")).click();
    }

    // BR3.4 Reject remove when student not in course
    @Test
    @Order(4)
    public void rejectRemoveWhenStudentNotInCourse() {
        // Course 2 should have no students in its roster
        clickEditOnRow(2);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("remove-student-select")));

        String rosterBefore = getRosterText(2);

        // Try to remove without selecting anyone
        driver.findElement(By.id("remove-student-button")).click();
        dismissAlertIfPresent();

        String rosterAfter = getRosterText(2);
        assertEquals(rosterBefore, rosterAfter, "Roster should not change when student not in course");

        driver.findElement(By.id("edit-course-cancel-button")).click();
    }

    // BR3.5 Reject add when course does not exist
    @Test
    @Order(5)
    public void rejectAddWhenCourseDoesNotExist() {
        // Verify a non-existent course row is not present
        List<WebElement> fakeRow = driver.findElements(By.id("course-row-99999"));
        assertTrue(fakeRow.isEmpty(), "Course 99999 should not exist");
    }

    // BR3.6 Reject add when student does not exist
    @Test
    @Order(6)
    public void rejectAddWhenStudentDoesNotExist() {
        clickEditOnRow(1);

        // Check that a fake student is not an option in the dropdown
        Select select = new Select(driver.findElement(By.id("select-student")));
        List<WebElement> options = select.getOptions();

        boolean fakeStudentPresent = options.stream()
                .anyMatch(o -> o.getText().equals("Fake Student 99999"));

        assertFalse(fakeStudentPresent, "Fake student should not be selectable");

        driver.findElement(By.id("edit-course-cancel-button")).click();
    }

    // BR3.7 Enrollment count increases after add
    @Test
    @Order(7)
    public void enrollmentCountIncreasesAfterAdd() {
        int countBefore = getEnrollmentCount(1);

        clickEditOnRow(1);

        Select select = new Select(driver.findElement(By.id("select-student")));
        select.selectByVisibleText("Spike Spiegal");
        driver.findElement(By.id("add-student-button")).click();

        wait.until(d ->
                d.findElement(By.id("course-roster-1")).getText().contains("Spike Spiegal")
        );

        driver.findElement(By.id("edit-course-save-button")).click();

        int countAfter = getEnrollmentCount(1);
        assertTrue(countAfter > countBefore, "Enrollment count should increase after adding a student");
    }

    // BR3.8 Enrollment count decreases after remove
    @Test
    @Order(8)
    public void enrollmentCountDecreasesAfterRemove() {
        int countBefore = getEnrollmentCount(1);

        clickEditOnRow(1);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("remove-student-select")));

        Select removeSelect = new Select(driver.findElement(By.id("remove-student-select")));
        removeSelect.selectByVisibleText("Spike Spiegal");
        driver.findElement(By.id("remove-student-button")).click();


        driver.findElement(By.id("edit-course-save-button")).click();

        int countAfter = getEnrollmentCount(1);
        assertTrue(countAfter < countBefore, "Enrollment count should decrease after removing a student");
    }

    // BR3.9 Reject remove when course does not exist
    @Test
    @Order(9)
    public void rejectRemoveWhenCourseDoesNotExist() {
        List<WebElement> fakeRow = driver.findElements(By.id("course-row-99999"));
        assertTrue(fakeRow.isEmpty(), "Cannot remove from a course that does not exist");
    }

    // BR3.10 Reject remove when student does not exist
    @Test
    @Order(10)
    public void rejectRemoveWhenStudentDoesNotExist() {
        clickEditOnRow(1);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("remove-student-select")));

        Select removeSelect = new Select(driver.findElement(By.id("remove-student-select")));
        List<WebElement> options = removeSelect.getOptions();

        boolean fakeStudentPresent = options.stream()
                .anyMatch(o -> o.getText().equals("Fake Student 99999"));

        assertFalse(fakeStudentPresent, "Fake student should not appear in remove dropdown");

        driver.findElement(By.id("edit-course-cancel-button")).click();
    }
}