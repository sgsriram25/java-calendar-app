import controller.HeadlessTextController;
import fakes.FakeCalendarManager;
import fakes.FakeView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Tests for the HeadlessController class.
 */
public class HeadlessControllerTest {
  private FakeCalendarManager fakeCalendarManager;
  private FakeView fakeView;
  private HeadlessTextController controller;
  private Path tempFile;

  @Before
  public void setUp() throws IOException {
    fakeCalendarManager = new FakeCalendarManager();
    fakeView = new FakeView();
    // Create a temporary file for tests that require valid file input.
    tempFile = Files.createTempFile("headlessTest", ".txt");
  }

  @After
  public void tearDown() throws IOException {
    if (tempFile != null && Files.exists(tempFile)) {
      Files.delete(tempFile);
    }
  }

  @Test
  public void testHeadlessControllerValidFile() throws IOException {
    // Write a valid command sequence to the temporary file.
    try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(tempFile))) {
      writer.println("use calendar --name MyCalendar");
      writer.println("create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00");
      writer.println("exit");
    }
    controller = new HeadlessTextController(fakeCalendarManager, fakeView, tempFile.toString());
    controller.run();

    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();
    // Expecting two calls:
    // 1. The useCalendar call.
    // 2. The createSingleEvent call with calendar context "MyCalendar".
    List<String> expectedCall1 = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCall2 = List.of("createSingleEvent called",
        "[MyCalendar, Meeting, " + "2025-03-05T10:00, " + "2025-03-05T11:00]");
    assertEquals(2, allCalls.size());
    assertEquals(expectedCall1, allCalls.get(0));
    assertEquals(expectedCall2, allCalls.get(1));

    // Verify the view's messages include the headless start and exit messages.
    assertTrue(fakeView.getMessages()
        .contains("Running in headless mode. Reading commands from file: " + tempFile.toString()));
    assertTrue(fakeView.getMessages().contains("Headless execution complete. Exiting."));
  }

  /**
   * Tests the headless controller when the specified file does not exist.
   * Expected: The view should display an error message indicating a file reading error.
   */
  @Test
  public void testHeadlessControllerFileReadError() {
    String nonExistentPath = "nonexistentfile.txt";
    controller = new HeadlessTextController(fakeCalendarManager, fakeView, nonExistentPath);
    controller.run();
    List<String> errors = fakeView.getErrors();
    assertFalse(errors.isEmpty());
    assertTrue(errors.get(0).startsWith("Error reading file:"));
  }

  /**
   * Tests the headless controller with a file containing an invalid command (with proper
   * calendar context)
   * followed by "exit". Expected: The view should display an error message indicating an error
   * processing the command.
   */
  @Test
  public void testHeadlessControllerInvalidCommand() throws IOException {
    // Write an invalid command (after setting calendar context) and an "exit" line to the
    // temporary file.
    try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(tempFile))) {
      writer.println("use calendar --name MyCalendar");
      writer.println("invalidCommand");
      writer.println("exit");
    }
    controller = new HeadlessTextController(fakeCalendarManager, fakeView, tempFile.toString());
    controller.run();
    List<String> errors = fakeView.getErrors();
    assertFalse(errors.isEmpty());
    assertTrue(errors.get(0).startsWith("Invalid command format,"));
  }

  /**
   * Tests the headless controller with a file that does not end with "exit".
   * Expected: The view should display an error message indicating that the last command must be
   * 'exit'.
   */
  @Test
  public void testHeadlessControllerNotEndedWithExit() throws IOException {
    // Write commands without the final "exit" command.
    try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(tempFile))) {
      writer.println("use calendar --name MyCalendar");
      writer.println("create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00");
    }
    controller = new HeadlessTextController(fakeCalendarManager, fakeView, tempFile.toString());
    controller.run();
    List<String> errors = fakeView.getErrors();
    assertFalse(errors.isEmpty());
    assertEquals("Last command in file must be 'exit'.", errors.get(0));
  }
}
