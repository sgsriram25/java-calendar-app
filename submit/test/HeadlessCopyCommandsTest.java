import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test class for headless copy commands.
 */
public class HeadlessCopyCommandsTest {

  private final InputStream originalIn = System.in;
  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream testOut;
  private Path tempFile;

  @Before
  public void setUp() throws Exception {
    testOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));
    // Create a temporary file for headless input.
    tempFile = Files.createTempFile("headlessTest", ".txt");
  }

  @After
  public void tearDown() throws Exception {
    System.setIn(originalIn);
    System.setOut(originalOut);
    Files.deleteIfExists(tempFile);
  }

  /* ============================
   * Copy Single Event Command Tests
   * Format:
   * copy event <eventName> on <sourceDateTime> --target <targetCalendarName> to <targetDateTime>
   * ============================
   */

  @Test
  public void testCopySingleEventHeadlessCase1() throws Exception {
    String input = "create     calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00\n"
                   + "copy event Meeting on 2025-03-05T10:00 --target Personal to "
                   + "2025-03-06T09:00\n"
                   + "use calendar --name Personal\n" + "print events on 2025-03-06\n" + "exit\n";
    Files.write(tempFile, input.getBytes());
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\nRunning in headless mode. Reading commands from file: " + tempFile.toString() + "\n\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n\n"
        + "Using calendar 'Work'.\n\n"
        + "Single event 'Meeting' created successfully from 2025-03-05T10:00 to 2025-03-05T11:00"
        + ".\n\n"
        + "Event 'Meeting' copied from 2025-03-05T10:00 in calendar 'Work' to 2025-03-06T09:00 in"
        + " calendar 'Personal'.\n\n"
        + "Using calendar 'Personal'.\n\n" + "Events on 2025-03-06:\n"
        + "Meeting; From 2025-03-06T09:00 to 2025-03-06T10:00; Public\n\n"
        + "Headless execution complete. Exiting.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testCopySingleEventHeadlessCase2() throws Exception {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Seminar from 2025-04-10T14:00 to 2025-04-10T15:00\n"
                   + "copy event Seminar on 2025-04-10T14:00 --target Personal to "
                   + "2025-04-11T10:00\n"
                   + "use calendar --name Personal\n" + "print events on 2025-04-11\n" + "exit\n";
    Files.write(tempFile, input.getBytes());
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\nRunning in headless mode. Reading commands from file: " + tempFile.toString() + "\n\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n\n"
        + "Using calendar 'Work'.\n\n"
        + "Single event 'Seminar' created successfully from 2025-04-10T14:00 to 2025-04-10T15:00"
        + ".\n\n"
        + "Event 'Seminar' copied from 2025-04-10T14:00 in calendar 'Work' to 2025-04-11T10:00 in"
        + " calendar 'Personal'.\n\n"
        + "Using calendar 'Personal'.\n\n" + "Events on 2025-04-11:\n"
        + "Seminar; From 2025-04-11T10:00 to 2025-04-11T11:00; Public\n\n"
        + "Headless execution complete. Exiting.\n";
    assertEquals(expected, output);
  }

  /* ============================
   * Copy Events On Date Command Tests
   * Format:
   * copy events on <dateString> --target <targetCalendarName> to <dateString>
   * ============================
   */

  @Test
  public void testCopyEventsOnDateHeadlessCase1() throws Exception {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-05-05T10:00 to 2025-05-05T11:00\n"
                   + "create event Meeting from 2025-05-05T12:00 to 2025-05-05T13:00\n"
                   + "copy events on 2025-05-05 --target Personal to 2025-05-06\n"
                   + "use calendar --name Personal\n" + "print events on 2025-05-06\n" + "exit\n";
    Files.write(tempFile, input.getBytes());
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\nRunning in headless mode. Reading commands from file: " + tempFile.toString() + "\n\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n\n"
        + "Using calendar 'Work'.\n\n"
        + "Single event 'Meeting' created successfully from 2025-05-05T10:00 to 2025-05-05T11:00"
        + ".\n\n"
        + "Single event 'Meeting' created successfully from 2025-05-05T12:00 to 2025-05-05T13:00"
        + ".\n\n"
        + "Events on 2025-05-05 copied from calendar 'Work' to 2025-05-06 in calendar 'Personal'"
        + ".\n\n"
        + "Using calendar 'Personal'.\n\n" + "Events on 2025-05-06:\n"
        + "Meeting; From 2025-05-06T16:00 to 2025-05-06T17:00; Public\n"
        + "Meeting; From 2025-05-06T18:00 to 2025-05-06T19:00; Public\n\n"
        + "Headless execution complete. Exiting.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testCopyEventsOnDateHeadlessCase2() throws Exception {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Conference from 2025-06-10T09:00 to 2025-06-10T10:30\n"
                   + "create event Conference from 2025-06-10T11:00 to 2025-06-10T12:30\n"
                   + "copy events on 2025-06-10 --target Personal to 2025-06-11\n"
                   + "use calendar --name Personal\n" + "print events on 2025-06-11\n" + "exit\n";
    Files.write(tempFile, input.getBytes());
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\nRunning in headless mode. Reading commands from file: " + tempFile.toString() + "\n\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n\n"
        + "Using calendar 'Work'.\n\n"
        + "Single event 'Conference' created successfully from 2025-06-10T09:00 to "
        + "2025-06-10T10:30.\n\n"
        + "Single event 'Conference' created successfully from 2025-06-10T11:00 to "
        + "2025-06-10T12:30.\n\n"
        + "Events on 2025-06-10 copied from calendar 'Work' to 2025-06-11 in calendar 'Personal'"
        + ".\n\n"
        + "Using calendar 'Personal'.\n\n" + "Events on 2025-06-11:\n"
        + "Conference; From 2025-06-11T15:00 to 2025-06-11T16:30; Public\n"
        + "Conference; From 2025-06-11T17:00 to 2025-06-11T18:30; Public\n\n"
        + "Headless execution complete. Exiting.\n";
    assertEquals(expected, output);
  }

  /* ============================
   * Copy Events Between Dates Command Tests
   * Format:
   * copy events between <startDateString> and <endDateString> --target <targetCalendarName> to
   * <targetStartDateString>
   * ============================
   */

  @Test
  public void testCopyEventsBetweenDatesHeadlessCase1() throws Exception {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Lecture from 2025-07-01T09:00 to 2025-07-01T10:00\n"
                   + "create event Lecture from 2025-07-02T09:00 to 2025-07-02T10:00\n"
                   + "create event Lecture from 2025-07-03T09:00 to 2025-07-03T10:00\n"
                   + "copy events between 2025-07-01 and 2025-07-03 --target Personal to "
                   + "2025-08-01\n"
                   + "use calendar --name Personal\n"
                   + "print events from 2025-08-01T00:00 to 2025-08-01T23:59\n" + "exit\n";
    Files.write(tempFile, input.getBytes());
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\nRunning in headless mode. Reading commands from file: " + tempFile.toString() + "\n\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n\n"
        + "Using calendar 'Work'.\n\n"
        + "Single event 'Lecture' created successfully from 2025-07-01T09:00 to 2025-07-01T10:00"
        + ".\n\n"
        + "Single event 'Lecture' created successfully from 2025-07-02T09:00 to 2025-07-02T10:00"
        + ".\n\n"
        + "Single event 'Lecture' created successfully from 2025-07-03T09:00 to 2025-07-03T10:00"
        + ".\n\n"
        + "Events from 2025-07-01 to 2025-07-03 copied from calendar 'Work' to 'Personal' "
        + "starting on 2025-08-01.\n\n"
        + "Using calendar 'Personal'.\n\n" + "Events from 2025-08-01T00:00 to 2025-08-01T23:59:\n"
        + "Lecture; From 2025-08-01T15:00 to 2025-08-01T16:00; Public\n\n"
        + "Headless execution complete. Exiting.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testCopyEventsBetweenDatesHeadlessCase2() throws Exception {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Seminar from 2025-09-10T14:00 to 2025-09-10T15:30\n"
                   + "create event Seminar from 2025-09-11T14:00 to 2025-09-11T15:30\n"
                   + "copy events between 2025-09-10 and 2025-09-11 --target Personal to "
                   + "2025-10-01\n"
                   + "use calendar --name Personal\n"
                   + "print events from 2025-10-01T00:00 to 2025-10-01T23:59\n" + "exit\n";
    Files.write(tempFile, input.getBytes());
    CalendarApp.main(new String[]{"--mode", "headless", tempFile.toString()});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\nRunning in headless mode. Reading commands from file: " + tempFile.toString() + "\n\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n\n"
        + "Using calendar 'Work'.\n\n"
        + "Single event 'Seminar' created successfully from 2025-09-10T14:00 to 2025-09-10T15:30"
        + ".\n\n"
        + "Single event 'Seminar' created successfully from 2025-09-11T14:00 to 2025-09-11T15:30"
        + ".\n\n"
        + "Events from 2025-09-10 to 2025-09-11 copied from calendar 'Work' to 'Personal' "
        + "starting on 2025-10-01.\n\n"
        + "Using calendar 'Personal'.\n\n" + "Events from 2025-10-01T00:00 to 2025-10-01T23:59:\n"
        + "Seminar; From 2025-10-01T20:00 to 2025-10-01T21:30; Public\n\n"
        + "Headless execution complete. Exiting.\n";
    assertEquals(expected, output);
  }
}
