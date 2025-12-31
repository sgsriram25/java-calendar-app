import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

/**
 * Test class for headless mode with new commands.
 */
public class HeadlessNewCommandsTest {


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
  public void editCalendar() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work", "create event office-hours from 2024-03-11T10:00 to "
                                    + "2024-03-11T12:00 repeats MWR until " + "2024-03-21T13:00",
        "create event meeting from 2024-03-14T08:00 to 2024-03-14T09:30",
        "print events from 2024-03-11T09:00 to 2024-03-22T13:00",
        "edit calendar --name Work --property timezone Europe/Paris",
        "print events from 2024-03-11T09:00 to 2024-03-22T13:00",
        "edit calendar --name Work --property name Office", "use calendar --name Office",
        "print events from 2024-03-11T09:00 to 2024-03-22T13:00", "exit");

    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Using calendar 'Work'.\n" + "\n"
                      + "Recurring event 'office-hours' created until 2024-03-21 on [M, W, R].\n"
                      + "\n"
                      + "Single event 'meeting' created successfully from 2024-03-14T08:00 to "
                      + "2024-03-14T09:30.\n" + "\n"
                      + "Events from 2024-03-11T09:00 to 2024-03-22T13:00:\n"
                      + "office-hours; From 2024-03-11T10:00 to 2024-03-11T12:00; Public\n"
                      + "office-hours; From 2024-03-13T10:00 to 2024-03-13T12:00; Public\n"
                      + "office-hours; From 2024-03-14T10:00 to 2024-03-14T12:00; Public\n"
                      + "office-hours; From 2024-03-18T10:00 to 2024-03-18T12:00; Public\n"
                      + "office-hours; From 2024-03-20T10:00 to 2024-03-20T12:00; Public\n"
                      + "office-hours; From 2024-03-21T10:00 to 2024-03-21T12:00; Public\n"
                      + "meeting; From 2024-03-14T08:00 to 2024-03-14T09:30; Public\n" + "\n"
                      + "Calendar 'Work' updated successfully: timezone changed to Europe/Paris.\n"
                      + "\n" + "Events from 2024-03-11T09:00 to 2024-03-22T13:00:\n"
                      + "office-hours; From 2024-03-11T15:00 to 2024-03-11T17:00; Public\n"
                      + "office-hours; From 2024-03-13T15:00 to 2024-03-13T17:00; Public\n"
                      + "office-hours; From 2024-03-14T15:00 to 2024-03-14T17:00; Public\n"
                      + "office-hours; From 2024-03-18T15:00 to 2024-03-18T17:00; Public\n"
                      + "office-hours; From 2024-03-20T15:00 to 2024-03-20T17:00; Public\n"
                      + "office-hours; From 2024-03-21T15:00 to 2024-03-21T17:00; Public\n"
                      + "meeting; From 2024-03-14T13:00 to 2024-03-14T14:30; Public\n" + "\n"
                      + "Calendar 'Work' updated successfully: name changed to Office.\n" + "\n"
                      + "Using calendar 'Office'.\n" + "\n"
                      + "Events from 2024-03-11T09:00 to 2024-03-22T13:00:\n"
                      + "office-hours; From 2024-03-11T15:00 to 2024-03-11T17:00; Public\n"
                      + "office-hours; From 2024-03-13T15:00 to 2024-03-13T17:00; Public\n"
                      + "office-hours; From 2024-03-14T15:00 to 2024-03-14T17:00; Public\n"
                      + "office-hours; From 2024-03-18T15:00 to 2024-03-18T17:00; Public\n"
                      + "office-hours; From 2024-03-20T15:00 to 2024-03-20T17:00; Public\n"
                      + "office-hours; From 2024-03-21T15:00 to 2024-03-21T17:00; Public\n"
                      + "meeting; From 2024-03-14T13:00 to 2024-03-14T14:30; Public\n" + "\n"
                      + "Headless execution complete. Exiting.";
    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }


  /**
   * Tests headless mode with the following sequence.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void copyCommands() throws Exception {
    // Build the input command sequence.
    String input = String.join("\n", "create calendar --name Work1 --timezone America/New_York",
        "create calendar --name Personal1 --timezone Europe/Paris", "use calendar --name Work1",
        "create event Meeting from 2024-03-11T10:00 to 2024-03-11T11:00",
        "copy event Meeting on 2024-03-11T10:00 --target Personal1 to " + "2024-03-11T15:00",
        "use calendar --name Personal1", "print events on 2024-03-11",
        "create calendar --name Work2 --timezone America/New_York",
        "create calendar --name Personal2 --timezone Europe/Paris", "use calendar --name Work2",
        "create event Conference from 2024-03-12T09:00 to 2024-03-12T10:00",
        "create event Seminar from 2024-03-12T11:00 to 2024-03-12T12:00",
        "copy events on 2024-03-12 --target Personal2 to 2024-03-12",
        "use calendar --name Personal2", "print events on 2024-03-12",
        "create calendar --name Work3 --timezone America/New_York",
        "create calendar --name Personal3 --timezone Europe/Paris", "use calendar --name Work3",
        "create event Workshop from 2024-03-13T08:00 to 2024-03-13T09:00",
        "create event Webinar from 2024-03-14T14:00 to 2024-03-14T15:00",
        "create event Training from 2024-03-15T10:00 to 2024-03-15T11:00",
        "copy events between 2024-03-13 and 2024-03-15 --target Personal3 " + "to 2024-03-16",
        "use calendar --name Personal3", "print events from 2024-03-16T00:00 to 2024-03-16T23:59",
        "exportCalendar cal example.csv", "exit");


    Files.write(tempFile, input.getBytes());

    // Run headless mode by passing the temp file path as an argument.
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});

    // Capture output and normalize line endings.
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output. Adjust this string as necessary to match your app's actual output.
    String expected = "Calendar 'Work1' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Calendar 'Personal1' created successfully. Timezone: Europe/Paris\n" + "\n"
                      + "Using calendar 'Work1'.\n" + "\n"
                      + "Single event 'Meeting' created successfully from 2024-03-11T10:00 to "
                      + "2024-03-11T11:00.\n"
                      + "\n"
                      + "Event 'Meeting' copied from 2024-03-11T10:00 in calendar 'Work1' to "
                      + "2024-03-11T15:00 in calendar 'Personal1'.\n"
                      + "\n" + "Using calendar 'Personal1'.\n" + "\n" + "Events on 2024-03-11:\n"
                      + "Meeting; From 2024-03-11T15:00 to 2024-03-11T16:00; Public\n" + "\n"
                      + "Calendar 'Work2' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Calendar 'Personal2' created successfully. Timezone: Europe/Paris\n" + "\n"
                      + "Using calendar 'Work2'.\n" + "\n"
                      + "Single event 'Conference' created successfully from 2024-03-12T09:00 to "
                      + "2024-03-12T10:00.\n"
                      + "\n"
                      + "Single event 'Seminar' created successfully from 2024-03-12T11:00 to "
                      + "2024-03-12T12:00.\n"
                      + "\n"
                      + "Events on 2024-03-12 copied from calendar 'Work2' to 2024-03-12 in "
                      + "calendar 'Personal2'.\n"
                      + "\n" + "Using calendar 'Personal2'.\n" + "\n" + "Events on 2024-03-12:\n"
                      + "Seminar; From 2024-03-12T16:00 to 2024-03-12T17:00; Public\n"
                      + "Conference; From 2024-03-12T14:00 to 2024-03-12T15:00; Public\n" + "\n"
                      + "Calendar 'Work3' created successfully. Timezone: America/New_York\n" + "\n"
                      + "Calendar 'Personal3' created successfully. Timezone: Europe/Paris\n" + "\n"
                      + "Using calendar 'Work3'.\n" + "\n"
                      + "Single event 'Workshop' created successfully from 2024-03-13T08:00 to "
                      + "2024-03-13T09:00.\n"
                      + "\n"
                      + "Single event 'Webinar' created successfully from 2024-03-14T14:00 to "
                      + "2024-03-14T15:00.\n"
                      + "\n"
                      + "Single event 'Training' created successfully from 2024-03-15T10:00 to "
                      + "2024-03-15T11:00.\n"
                      + "\n"
                      + "Events from 2024-03-13 to 2024-03-15 copied from calendar 'Work3' to "
                      + "'Personal3' starting on 2024-03-16.\n"
                      + "\n" + "Using calendar 'Personal3'.\n" + "\n"
                      + "Events from 2024-03-16T00:00 to 2024-03-16T23:59:\n"
                      + "Workshop; From 2024-03-16T13:00 to 2024-03-16T14:00; Public";


    assertTrue("Test didn't pass please check implementation", output.contains(expected));
  }

}
