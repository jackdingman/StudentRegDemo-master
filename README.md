Download the zip project.
Run the following files in order - this is critical to anything working at all.

1. StudentCrudTest.java
2. CourseCrudTest.java
3. BusinessRulesTest.java

During the development of this Selenium suite, I made the decision to use @TestInstance and @TestMethodOrder, as well as @BeforeAll as a single initialization. 
Each test naturally built on one another. For example, a student must be created before it can be added to a course. 
By default, JUnit executes each test independently and runs @BeforeEach before every test. 
This behavior caused previously created courses and enrollments to be lost, as well as a plethora of other various errors down the line. 
Implementing @TestInstance, @BeforeAll, and @TestMethodOrder enforced a logical sequence of operations. 
The main tradeoff was that some tests depend on successful execution of earlier tests. 
So my midway-through-the-lab structure change solved some issues and created others. Some of the tests "fail" due to lack of proper backend response, rather than the test being wrong.

Look to my excel document for my test cases. Many were taken from the Postman lab and modified for testing UI.
