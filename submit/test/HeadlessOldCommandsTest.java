import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Test class for headless mode with old commands.
 */
public class HeadlessOldCommandsTest {

  private final InputStream originalIn = System.in;
  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream testOut;
  private Path tempFile;

  @Before
  public void setUp() throws Exception {
    testOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));
    // Create a temporary file for headless input.
    tempFile = Files.createTempFile("headlessOldCommandsTest", ".txt");
  }

  @After
  public void tearDown() throws Exception {
    System.setIn(originalIn);
    System.setOut(originalOut);
    Files.deleteIfExists(tempFile);
  }

  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommands() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work", "create event office-hours-cs5010 from 2024-03-11T10:00 to "
                                    + "2024-03-11T12:00 repeats MWR " + "until 2024-03-21T13:00",
        "create event doctor-appointment from 2024-03-14T08:00 to " + "2024-03-14T09:30",
        "create event new-event from 2024-03-11T15:00 to 2024-03-11T16:00 " + "repeats MWR "
        + "for 3 times", "print events from 2024-03-11T09:00 to 2024-03-22T13:00",
        "edit events subject office-hours-cs5010 newname",
        "print events from 2024-03-11T09:00 to 2024-03-22T13:00",
        "edit events subject doctor-appointment from 2024-03-14T07:00 with" + " annual-physical",
        "print events on 2024-03-14", "show status on 2024-03-14T15:00",
        "show status on 2024-03-14T18:00", "exit");
    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Recurring event 'office-hours-cs5010' created until 2024-03-21 on [M, W,"
                      + " R].\n" + "\n"
                      + "Single event 'doctor-appointment' created successfully from "
                      + "2024-03-14T08:00 to 2024-03-14T09:30.\n" + "\n"
                      + "Recurring event 'new-event' created for 3 times on [M, W, R].\n" + "\n"
                      + "Events from 2024-03-11T09:00 to 2024-03-22T13:00:\n"
                      + "office-hours-cs5010; From 2024-03-11T10:00 to 2024-03-11T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-13T10:00 to 2024-03-13T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-18T10:00 to 2024-03-18T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-20T10:00 to 2024-03-20T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-21T10:00 to 2024-03-21T12:00; Public\n"
                      + "new-event; From 2024-03-11T15:00 to 2024-03-11T16:00; Public\n"
                      + "new-event; From 2024-03-13T15:00 to 2024-03-13T16:00; Public\n"
                      + "new-event; From 2024-03-14T15:00 to 2024-03-14T16:00; Public\n"
                      + "doctor-appointment; From 2024-03-14T08:00 to 2024-03-14T09:30; Public\n"
                      + "\n"
                      + "All events named 'office-hours-cs5010' updated successfully: subject "
                      + "changed to newname.\n" + "\n"
                      + "Events from 2024-03-11T09:00 to 2024-03-22T13:00:\n"
                      + "new-event; From 2024-03-11T15:00 to 2024-03-11T16:00; Public\n"
                      + "new-event; From 2024-03-13T15:00 to 2024-03-13T16:00; Public\n"
                      + "new-event; From 2024-03-14T15:00 to 2024-03-14T16:00; Public\n"
                      + "doctor-appointment; From 2024-03-14T08:00 to 2024-03-14T09:30; Public\n"
                      + "newname; From 2024-03-11T10:00 to 2024-03-11T12:00; Public\n"
                      + "newname; From 2024-03-13T10:00 to 2024-03-13T12:00; Public\n"
                      + "newname; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n"
                      + "newname; From 2024-03-18T10:00 to 2024-03-18T12:00; Public\n"
                      + "newname; From 2024-03-20T10:00 to 2024-03-20T12:00; Public\n"
                      + "newname; From 2024-03-21T10:00 to 2024-03-21T12:00; Public\n" + "\n"
                      + "All events named 'doctor-appointment' after 2024-03-14T07:00 updated "
                      + "successfully: subject changed to annual-physical.\n" + "\n"
                      + "Events on 2024-03-14:\n"
                      + "annual-physical; From 2024-03-14T08:00 to 2024-03-14T09:30; Public\n"
                      + "new-event; From 2024-03-14T15:00 to 2024-03-14T16:00; Public\n"
                      + "newname; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n" + "\n"
                      + "User is busy at 2024-03-14T15:00\n" + "\n"
                      + "User is available at 2024-03-14T18:00\n" + "\n"
                      + "Headless execution complete. Exiting.\n";

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }

  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommandsTwo() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work", "create event office-hours-cs5010 from 2024-03-11T10:00 to "
                                    + "2024-03-11T12:00 repeats MWR " + "until 2024-03-21T13:00",
        "print events from 2024-03-12T09:00 to 2024-03-22T13:00", "print events on 2024-03-14",
        "create event office-hours-cs4300 from 2024-03-12T12:00 to "
        + "2024-03-12T14:00 repeats TR " + "until 2024-03-21T13:00",
        "print events from 2024-03-12T09:00 to 2024-03-22T15:00", "print events on 2024-03-14",
        "exit");
    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Recurring event 'office-hours-cs5010' created until 2024-03-21 on [M, W,"
                      + " R].\n" + "\n" + "Events from 2024-03-12T09:00 to 2024-03-22T13:00:\n"
                      + "office-hours-cs5010; From 2024-03-13T10:00 to 2024-03-13T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-18T10:00 to 2024-03-18T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-20T10:00 to 2024-03-20T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-21T10:00 to 2024-03-21T12:00; Public\n"
                      + "\n" + "Events on 2024-03-14:\n"
                      + "office-hours-cs5010; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n"
                      + "\n"
                      + "Recurring event 'office-hours-cs4300' created until 2024-03-21 on [T, R]"
                      + ".\n" + "\n" + "Events from 2024-03-12T09:00 to 2024-03-22T15:00:\n"
                      + "office-hours-cs5010; From 2024-03-13T10:00 to 2024-03-13T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-18T10:00 to 2024-03-18T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-20T10:00 to 2024-03-20T12:00; Public\n"
                      + "office-hours-cs5010; From 2024-03-21T10:00 to 2024-03-21T12:00; Public\n"
                      + "office-hours-cs4300; From 2024-03-12T12:00 to 2024-03-12T14:00; Public\n"
                      + "office-hours-cs4300; From 2024-03-14T12:00 to 2024-03-14T14:00; Public\n"
                      + "office-hours-cs4300; From 2024-03-19T12:00 to 2024-03-19T14:00; Public\n"
                      + "office-hours-cs4300; From 2024-03-21T12:00 to 2024-03-21T14:00; Public\n"
                      + "\n" + "Events on 2024-03-14:\n"
                      + "office-hours-cs5010; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n"
                      + "office-hours-cs4300; From 2024-03-14T12:00 to 2024-03-14T14:00; Public\n"
                      + "\n" + "Headless execution complete. Exiting.\n";

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }


  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommandsConflicts() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work", "create event office-hours-cs5010 from 2024-03-11T10:00 to "
                                    + "2024-03-11T12:00 repeats MWR " + "until 2024-03-21T13:00",
        "create event office-hours-cs5610 from 2024-03-11T11:30 to "
        + "2024-03-11T12:00 repeats MWR " + "until 2024-03-21T13:00", "print events on 2024-03-14",
        "exit");
    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Recurring event 'office-hours-cs5010' created until 2024-03-21 on [M, W,"
                      + " R].\n" + "\n"
                      + "Error: Error creating event: Event conflicts with existing event.";

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }

  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommandsEditConflicts() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work",
        "create event office-hours-cs5010 from 2024-03-11T10:00 to " + "2024-03-11T12:00",
        "create event office-hours-cs5610 from 2024-03-11T12:30 to "
        + "2024-03-11T13:00 repeats MWR " + "until 2024-03-21T13:00",
        "edit events end office-hours-cs5010 2024-03-11T14:00", "print events on 2024-03-14",
        "exit");
    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Single event 'office-hours-cs5010' created successfully from "
                      + "2024-03-11T10:00 to 2024-03-11T12:00.\n" + "\n"
                      + "Recurring event 'office-hours-cs5610' created until 2024-03-21 on [M, W,"
                      + " R].\n" + "\n" + "Error: Error editing event";

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }

  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommandsThree() throws Exception {

    String filePath = System.getProperty("user.dir") + "\\example.csv";
    Path path = Paths.get(filePath);

    Files.deleteIfExists(path);

    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work", "create event office-hours-cs5010 from 2024-03-11T10:00 to "
                                    + "2024-03-11T12:00 repeats MWR " + "until 2024-03-21T13:00",
        "edit events subject office-hours-cs5010 from 2024-03-13T10:00 " + "with newname",
        "print events from 2024-03-11T09:00 to 2024-03-22T13:00", "exportCalendar cal example.csv",
        "print events from 2024-03-11T09:00 to 2024-03-22T13:00", "exit");
    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Recurring event 'office-hours-cs5010' created until 2024-03-21 on [M, W,"
                      + " R].\n" + "\n"
                      + "All events named 'office-hours-cs5010' after 2024-03-13T10:00 updated "
                      + "successfully: subject changed to newname.\n" + "\n"
                      + "Events from 2024-03-11T09:00 to 2024-03-22T13:00:\n"
                      + "office-hours-cs5010; From 2024-03-11T10:00 to 2024-03-11T12:00; Public\n"
                      + "newname; From 2024-03-13T10:00 to 2024-03-13T12:00; Public\n"
                      + "newname; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n"
                      + "newname; From 2024-03-18T10:00 to 2024-03-18T12:00; Public\n"
                      + "newname; From 2024-03-20T10:00 to 2024-03-20T12:00; Public\n"
                      + "newname; From 2024-03-21T10:00 to 2024-03-21T12:00; Public\n" + "\n"
                      + "Calendar exported successfully to";
    String expected2 = "Events from 2024-03-11T09:00 to 2024-03-22T13:00:\n"
                       + "office-hours-cs5010; From 2024-03-11T10:00 to 2024-03-11T12:00; Public\n"
                       + "newname; From 2024-03-13T10:00 to 2024-03-13T12:00; Public\n"
                       + "newname; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n"
                       + "newname; From 2024-03-18T10:00 to 2024-03-18T12:00; Public\n"
                       + "newname; From 2024-03-20T10:00 to 2024-03-20T12:00; Public\n"
                       + "newname; From 2024-03-21T10:00 to 2024-03-21T12:00; Public\n" + "\n"
                       + "Headless execution complete. Exiting.";

    assertTrue("File should exist", Files.exists(path));
    try {
      List<String> lines = Files.readAllLines(path);
      assertFalse("CSV file should not be empty", lines.isEmpty());
      assertEquals("Subject,Start Date,Start Time,End Date,End Time,All Day Event,"
                   + "Description,Location,Private", lines.get(0));
      assertEquals("office-hours-cs5010,2024-03-11,10:00,2024-03-11,12:00,false,,,false",
          lines.get(1));
    } catch (IOException e) {
      fail("Failed to read file");
    }

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
    assertTrue("Test didn't pass please check implementation", output.contains(expected2));
  }


  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommandsInvalidStart() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work",
        "create event office-hours-cs5010 from 2024-03-11T10:00 to " + "2024-03-11T12:00",
        "edit events start office-hours-cs5010 2024-03-11T12:30",
        "print events from 2024-03-11T09:00 to 2024-03-22T13:00", "exit");
    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Single event 'office-hours-cs5010' created successfully from "
                      + "2024-03-11T10:00 to 2024-03-11T12:00.\n" + "\n"
                      + "Error: Error editing event: Start time must be before end time\n";

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }

  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommandsGetOnDate() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work",
        "create event office-hours-cs5010 from 2024-03-11T10:00 to " + "2024-03-11T12:00",
        "create event office-hours-cs5610 from 2024-03-12T10:00 to " + "2024-03-12T12:00",
        "edit event start office-hours-cs5010 from 2024-03-11T10:00 to " + "2024-03-11T12:00 with "
        + "2024-03-11T10:30", "print events on 2025-03-01",
        "print events from 2024-03-10T09:00 to 2024-03-22T13:00", "exit");
    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Single event 'office-hours-cs5010' created successfully from "
                      + "2024-03-11T10:00 to 2024-03-11T12:00.\n" + "\n"
                      + "Single event 'office-hours-cs5610' created successfully from "
                      + "2024-03-12T10:00 to 2024-03-12T12:00.\n" + "\n"
                      + "Event 'office-hours-cs5010' updated successfully: start changed to "
                      + "2024-03-11T10:30.\n" + "\n" + "No events on 2025-03-01\n" + "\n"
                      + "Events from 2024-03-10T09:00 to 2024-03-22T13:00:\n"
                      + "office-hours-cs5010; From 2024-03-11T10:30 to 2024-03-11T12:00; Public\n"
                      + "office-hours-cs5610; From 2024-03-12T10:00 to 2024-03-12T12:00; Public";

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }


  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommandsAllDay() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work", "create event office-hours-cs5010 on 2024-03-11T12:00",
        "print events from 2024-03-11T09:00 to 2024-03-22T13:00", "exit");
    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Single all-day event 'office-hours-cs5010' created successfully on "
                      + "2024-03-11T12:00.\n" + "\n"
                      + "Events from 2024-03-11T09:00 to 2024-03-22T13:00:\n"
                      + "office-hours-cs5010; 2024-03-11 All Day; Public\n" + "\n"
                      + "Headless execution complete. Exiting.";

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }

  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommandsFour() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work",
        "create event office-hours-cs5010 from 2024-03-11T10:00 to " + "2024-03-11T12:00",
        "create event office-hours-cs4300 from 2024-03-12T10:00 to " + "2024-03-12T12:00",
        "create event office-hours-cs5010-2 from 2024-03-13T10:00 to " + "2024-03-13T12:00",
        "create event office-hours-cs4300-2 from 2024-03-13T14:00 to " + "2024-03-13T16:00",
        "print events on 2024-03-13", "print events from 2024-03-11T09:00 to 2024-03-12T13:00",
        "exit");

    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Single event 'office-hours-cs5010' created successfully from "
                      + "2024-03-11T10:00 to 2024-03-11T12:00.\n" + "\n"
                      + "Single event 'office-hours-cs4300' created successfully from "
                      + "2024-03-12T10:00 to 2024-03-12T12:00.\n" + "\n"
                      + "Single event 'office-hours-cs5010-2' created successfully from "
                      + "2024-03-13T10:00 to 2024-03-13T12:00.\n" + "\n"
                      + "Single event 'office-hours-cs4300-2' created successfully from "
                      + "2024-03-13T14:00 to 2024-03-13T16:00.\n" + "\n" + "Events on 2024-03-13:\n"
                      + "office-hours-cs5010-2; From 2024-03-13T10:00 to 2024-03-13T12:00; Public\n"
                      + "office-hours-cs4300-2; From 2024-03-13T14:00 to 2024-03-13T16:00; Public\n"
                      + "\n" + "Events from 2024-03-11T09:00 to 2024-03-12T13:00:\n"
                      + "office-hours-cs5010; From 2024-03-11T10:00 to 2024-03-11T12:00; Public\n"
                      + "office-hours-cs4300; From 2024-03-12T10:00 to 2024-03-12T12:00; Public\n"
                      + "\n" + "Headless execution complete. Exiting.\n";

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }

  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testHeadlessOldCommandsFive() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work",
        "create event office-hours-cs5010 from 2024-03-11T10:00 to " + "2024-03-11T12:00",
        "create event office-hours-cs4300 from 2024-03-12T10:00 to " + "2024-03-12T12:00",
        "create event office-hours-cs5010-2 from 2024-03-13T10:00 to " + "2024-03-13T12:00",
        "create event office-hours-cs4300-2 from 2024-03-13T14:00 to " + "2024-03-13T16:00",
        "print events on 2024-03-13",
        "edit event subject office-hours-cs5010-2 from 2024-03-13T10:00 to"
        + " 2024-03-13T12:00 with " + "office-hours-cs5010-second", "print events on 2024-03-13",
        "exit");

    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Single event 'office-hours-cs5010' created successfully from "
                      + "2024-03-11T10:00 to 2024-03-11T12:00.\n"
                      + "\n"
                      + "Single event 'office-hours-cs4300' created successfully from "
                      + "2024-03-12T10:00 to 2024-03-12T12:00.\n"
                      + "\n"
                      + "Single event 'office-hours-cs5010-2' created successfully from "
                      + "2024-03-13T10:00 to 2024-03-13T12:00.\n"
                      + "\n"
                      + "Single event 'office-hours-cs4300-2' created successfully from "
                      + "2024-03-13T14:00 to 2024-03-13T16:00.\n"
                      + "\n" + "Events on 2024-03-13:\n"
                      + "office-hours-cs5010-2; From 2024-03-13T10:00 to 2024-03-13T12:00; Public\n"
                      + "office-hours-cs4300-2; From 2024-03-13T14:00 to 2024-03-13T16:00; Public\n"
                      + "\n"
                      + "Event 'office-hours-cs5010-2' updated successfully: subject changed to "
                      + "office-hours-cs5010-second.\n"
                      + "\n" + "Events on 2024-03-13:\n"
                      + "office-hours-cs5010-second; From 2024-03-13T10:00 to 2024-03-13T12:00; "
                      + "Public\n"
                      + "office-hours-cs4300-2; From 2024-03-13T14:00 to 2024-03-13T16:00; Public";

    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }


}
