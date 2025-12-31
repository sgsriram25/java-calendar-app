import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Test class for interactive mode with copy commands.
 */
public class InteractiveCopyCommandsTest {

  private InputStream originalIn;
  private PrintStream originalOut;
  private ByteArrayOutputStream testOut;

  @Before
  public void setUp() {
    originalIn = System.in;
    originalOut = System.out;
    testOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));
  }

  @After
  public void tearDown() {
    System.setIn(originalIn);
    System.setOut(originalOut);
  }

  /* ============================================
   * Copy Single Event Command Tests
   * Format:
   * copy event <eventName> on <sourceDateTime> --target <targetCalendarName> to <targetDateTime>
   * ============================================
   */

  @Test
  public void testCopySingleEventCase1() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00\n"
                   + "create event Meeting2 from 2025-03-05T11:00 to 2025-03-05T12:00 repeats MWF"
                   + " for 8 " + "times\n"
                   + "edit events subject Meeting2 from 2025-04-11T00:00 with newMeeting\n"
                   + "copy event Meeting on 2025-03-05T10:00 --target Personal to "
                   + "2025-03-06T09:00\n"
                   + "copy event Meeting2 on 2025-03-05T13:00 --target Personal to "
                   + "2025-03-06T09:00\n" + "use calendar --name Personal\n"
                   + "print events on 2025-03-06\n" + "use calendar --name Work\n"
                   + "show status on 2024-03-01T00:00\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output (adjust formatting as needed)
    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Meeting' created successfully from 2025-03-05T10:00 to 2025-03-05T11:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Recurring event 'Meeting2' created for 8 times on [M, W, F].\n" + "\n"
        + "Enter command:\n" + "\n" + "Error: Error editing event: No such event exists\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Event 'Meeting' copied from 2025-03-05T10:00 in calendar 'Work' to 2025-03-06T09:00 in"
        + " calendar 'Personal'.\n" + "\n" + "Enter command:\n" + "\n" + "Error: Event not found.\n"
        + "\n" + "Enter command:\n" + "\n" + "Using calendar 'Personal'.\n" + "\n"
        + "Enter command:\n" + "\n" + "Events on 2025-03-06:\n"
        + "Meeting; From 2025-03-06T09:00 to 2025-03-06T10:00; Public\n" + "\n" + "Enter command:\n"
        + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "User is available at 2024-03-01T00:00\n" + "\n" + "Enter command:\n" + "\n"
        + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testCopySingleEventCase2() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-04-10T14:00 to 2025-04-10T15:00\n"
                   + "copy event Meeting on 2025-04-10T14:00 --target Personal to "
                   + "2025-04-11T10:00\n" + "use calendar --name Personal\n"
                   + "print events on 2025-04-11\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Meeting' created successfully from 2025-04-10T14:00 to 2025-04-10T15:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Event 'Meeting' copied from 2025-04-10T14:00 in calendar 'Work' to 2025-04-11T10:00 in"
        + " calendar 'Personal'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Using calendar 'Personal'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Events on 2025-04-11:\n" + "Meeting; From 2025-04-11T10:00 to 2025-04-11T11:00; Public\n"
        + "\n" + "Enter command:\n" + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testCopySingleEventAfterEditCase1() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-04-10T14:00 to 2025-04-10T15:00\n"
                   + "edit event description Meeting from 2025-04-10T14:00 to 2025-04-10T15:00 "
                   + "with \"Important" + " Meeting\"\n"
                   + "edit event public Meeting from 2025-04-10T14:00 to 2025-04-10T15:00 with "
                   + "false\n" + "copy event Meeting on 2025-04-10T14:00 --target Personal to "
                   + "2025-04-11T10:00\n" + "use calendar --name Personal\n"
                   + "print events on 2025-04-11\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Work' created successfully. Timezone: America/New_York\n"
                      + "\nEnter command:\n"
                      + "\nCalendar 'Personal' created successfully. Timezone: Europe/Paris\n"
                      + "\nEnter command:\n" + "\nUsing calendar 'Work'.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Meeting' created successfully from 2025-04-10T14:00 to "
                      + "2025-04-10T15:00.\n" + "\nEnter command:\n"
                      + "\nEvent 'Meeting' updated successfully: description changed to Important"
                      + " Meeting.\n" + "\nEnter command:\n"
                      + "\nEvent 'Meeting' updated successfully: public changed to false.\n"
                      + "\nEnter command:\n"
                      + "\nEvent 'Meeting' copied from 2025-04-10T14:00 in calendar 'Work' to "
                      + "2025-04-11T10:00 in calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nUsing calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nEvents on 2025-04-11:\n"
                      + "Meeting; From 2025-04-11T10:00 to 2025-04-11T11:00; Private; Description:"
                      + " Important Meeting\n" + "\nEnter command:\n"
                      + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testCopySingleEventAfterEditCase2() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Seminar from 2025-05-15T16:00 to 2025-05-15T17:30\n"
                   + "edit event description Seminar from 2025-05-15T16:00 to 2025-05-15T17:30 "
                   + "with \"Key " + "Seminar\"\n"
                   + "edit event public Seminar from 2025-05-15T16:00 to 2025-05-15T17:30 with "
                   + "true\n" + "copy event Seminar on 2025-05-15T17:00 --target Personal to "
                   + "2025-05-16T12:00\n"
                   + "copy event Seminar on 2025-05-15T16:00 --target Personal to "
                   + "2025-05-16T12:00\n"
                   + "copy event Hello on 2025-05-15T16:00 --target Personal to 2025-05-16T12:00\n"
                   + "use calendar --name Personal\n" + "print events on 2025-05-16\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Seminar' created successfully from 2025-05-15T16:00 to 2025-05-15T17:30.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Event 'Seminar' updated successfully: description changed to Key Seminar.\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Event 'Seminar' updated successfully: public changed to true.\n" + "\n"
        + "Enter command:\n" + "\n" + "Error: Event not found.\n" + "\n" + "Enter command:\n" + "\n"
        + "Event 'Seminar' copied from 2025-05-15T16:00 in calendar 'Work' to 2025-05-16T12:00 in"
        + " calendar 'Personal'.\n" + "\n" + "Enter command:\n" + "\n" + "Error: null\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Personal'.\n" + "\n" + "Enter command:\n"
        + "\n" + "Events on 2025-05-16:\n"
        + "Seminar; From 2025-05-16T12:00 to 2025-05-16T13:30; Public; Description: Key Seminar\n"
        + "\n" + "Enter command:\n" + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }

  /* ============================================
   * Copy Events On Date Command Tests
   * Format:
   * copy events on <dateString> --target <targetCalendarName> to <dateString>
   * ============================================
   */

  @Test
  public void testCopyEventsOnDateCase1() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-05-05T10:00 to 2025-05-05T11:00\n"
                   + "create event Meeting from 2025-05-05T12:00 to 2025-05-05T13:00\n"
                   + "copy events on 2025-05-05 --target Personal to 2025-05-06\n"
                   + "use calendar --name Personal\n" + "print events on 2025-05-06\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Work' created successfully. Timezone: America/New_York\n"
                      + "\nEnter command:\n"
                      + "\nCalendar 'Personal' created successfully. Timezone: Europe/Paris\n"
                      + "\nEnter command:\n" + "\nUsing calendar 'Work'.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Meeting' created successfully from 2025-05-05T10:00 to "
                      + "2025-05-05T11:00.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Meeting' created successfully from 2025-05-05T12:00 to "
                      + "2025-05-05T13:00.\n" + "\nEnter command:\n"
                      + "\nEvents on 2025-05-05 copied from calendar 'Work' to 2025-05-06 in "
                      + "calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nUsing calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nEvents on 2025-05-06:\n"
                      + "Meeting; From 2025-05-06T16:00 to 2025-05-06T17:00; Public\n"
                      + "Meeting; From 2025-05-06T18:00 to 2025-05-06T19:00; Public\n"
                      + "\nEnter command:\n" + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testCopyEventsOnDateCase2() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Conference from 2025-06-10T09:00 to 2025-06-10T10:30\n"
                   + "create event Conference from 2025-06-10T11:00 to 2025-06-10T12:30\n"
                   + "copy events on 2025-06-10 --target Personal to 2025-06-11\n"
                   + "use calendar --name Personal\n" + "print events on 2025-06-11\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Work' created successfully. Timezone: America/New_York\n"
                      + "\nEnter command:\n"
                      + "\nCalendar 'Personal' created successfully. Timezone: Europe/Paris\n"
                      + "\nEnter command:\n" + "\nUsing calendar 'Work'.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Conference' created successfully from 2025-06-10T09:00 "
                      + "to 2025-06-10T10:30.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Conference' created successfully from 2025-06-10T11:00 "
                      + "to 2025-06-10T12:30.\n" + "\nEnter command:\n"
                      + "\nEvents on 2025-06-10 copied from calendar 'Work' to 2025-06-11 in "
                      + "calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nUsing calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nEvents on 2025-06-11:\n"
                      + "Conference; From 2025-06-11T15:00 to 2025-06-11T16:30; Public\n"
                      + "Conference; From 2025-06-11T17:00 to 2025-06-11T18:30; Public\n"
                      + "\nEnter command:\n" + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testCopyEventsOnDateDifferentActualDate() {
    String input = "create calendar --name LA --timezone America/Los_Angeles\n"
                   + "create calendar --name Berlin --timezone Europe/Berlin\n"
                   + "use calendar --name LA\n"
                   + "create event LateEvent from 2025-06-10T23:00 to 2025-06-11T00:00\n" +
                   // Here we copy events on 2025-06-10 from LA to target date 2025-06-10 in Berlin.
                   // Because LA at 23:00 converts to Berlin at 2025-06-11T08:00,
                   // the copied event will actually be on 2025-06-11.
                   "copy events on 2025-06-10 --target Berlin to 2025-06-10\n"
                   + "use calendar --name Berlin\n" +
                   // Now print events on 2025-06-11 because the copied event should appear on
                   // that day.
                   "print events on 2025-06-11\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Expected output:
    // The event LateEvent, originally from 2025-06-10T23:00 to 2025-06-11T00:00 in LA,
    // when copied to Berlin with target date 2025-06-10, gets adjusted as follows:
    // Candidate new start = 2025-06-10T23:00 (LA local time) interpreted in LA zone,
    // then converted to Berlin zone: that instant becomes 2025-06-11T08:00 (Berlin local time).
    // The event's duration (1 hour) is preserved: so new event is from 2025-06-11T08:00 to
    // 2025-06-11T09:00.
    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'LA' created successfully. Timezone: America/Los_Angeles\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Berlin' created successfully. Timezone: Europe/Berlin\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'LA'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Single event 'LateEvent' created successfully from 2025-06-10T23:00 to "
        + "2025-06-11T00:00.\n" + "\n" + "Enter command:\n" + "\n"
        + "Events on 2025-06-10 copied from calendar 'LA' to 2025-06-10 in calendar 'Berlin'.\n"
        + "\n" + "Enter command:\n" + "\n" + "Using calendar 'Berlin'.\n" + "\n"
        + "Enter command:\n" + "\n" + "Events on 2025-06-11:\n"
        + "LateEvent; From 2025-06-11T08:00 to 2025-06-11T09:00; Public\n" + "\n"
        + "Enter command:\n" + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testCopyEventsOnDateAfterEditingProperties() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Conference from 2025-07-10T09:00 to 2025-07-10T10:00\n"
                   + "create event Conference from 2025-07-10T11:00 to 2025-07-10T12:00\n" +
                   // Edit first occurrence:
                   "edit event description Conference from 2025-07-10T09:00 to 2025-07-10T10:00 "
                   + "with \"Annual" + " Conference\"\n"
                   + "edit event location Conference from 2025-07-10T09:00 to 2025-07-10T10:00 "
                   + "with Room101\n"
                   + "edit event public Conference from 2025-07-10T09:00 to 2025-07-10T10:00 with"
                   + " true\n" +
                   // Edit second occurrence:
                   "edit event description Conference from 2025-07-10T11:00 to 2025-07-10T12:00 "
                   + "with \"Annual" + " Conference\"\n"
                   + "edit event location Conference from 2025-07-10T11:00 to 2025-07-10T12:00 "
                   + "with Room101\n"
                   + "edit event public Conference from 2025-07-10T11:00 to 2025-07-10T12:00 with"
                   + " true\n" + "copy events on 2025-07-10 --target Personal to 2025-07-11\n"
                   + "use calendar --name Personal\n" + "print events on 2025-07-11\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Conversion: In New York during July, EDT (UTC-4), in Paris during July, CEST (UTC+2), so a
    // 6-hour difference.
    // For the event starting at 09:00 in New York, candidate new start = 2025-07-11T09:00,
    // then converting from New York to Paris adds 6 hours, resulting in 2025-07-11T15:00.
    // Duration remains 1 hour (ends at 16:00).
    // Similarly, for the second event: 11:00 in New York becomes 17:00 in Paris, ending at 18:00.
    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Work' created successfully. Timezone: America/New_York\n"
                      + "\nEnter command:\n"
                      + "\nCalendar 'Personal' created successfully. Timezone: Europe/Paris\n"
                      + "\nEnter command:\n" + "\nUsing calendar 'Work'.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Conference' created successfully from 2025-07-10T09:00 "
                      + "to 2025-07-10T10:00.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Conference' created successfully from 2025-07-10T11:00 "
                      + "to 2025-07-10T12:00.\n" + "\nEnter command:\n"
                      + "\nEvent 'Conference' updated successfully: description changed to Annual"
                      + " Conference.\n" + "\nEnter command:\n"
                      + "\nEvent 'Conference' updated successfully: location changed to Room101.\n"
                      + "\nEnter command:\n"
                      + "\nEvent 'Conference' updated successfully: public changed to true.\n"
                      + "\nEnter command:\n"
                      + "\nEvent 'Conference' updated successfully: description changed to Annual"
                      + " Conference.\n" + "\nEnter command:\n"
                      + "\nEvent 'Conference' updated successfully: location changed to Room101.\n"
                      + "\nEnter command:\n"
                      + "\nEvent 'Conference' updated successfully: public changed to true.\n"
                      + "\nEnter command:\n"
                      + "\nEvents on 2025-07-10 copied from calendar 'Work' to 2025-07-11 in "
                      + "calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nUsing calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nEvents on 2025-07-11:\n"
                      + "Conference; From 2025-07-11T15:00 to 2025-07-11T16:00; Public; "
                      + "Description: Annual Conference; Location: Room101\n"
                      + "Conference; From 2025-07-11T17:00 to 2025-07-11T18:00; Public; "
                      + "Description: Annual Conference; Location: Room101\n" + "\nEnter command:\n"
                      + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }

  /* ============================================
   * Copy Events Between Dates Command Tests
   * Format:
   * copy events between <startDateString> and <endDateString> --target <targetCalendarName> to
   * <targetStartDateString>
   * ============================================
   */

  @Test
  public void testCopyEventsBetweenDatesCase1() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Lecture from 2025-07-01T09:00 to 2025-07-01T10:00\n"
                   + "create event Lecture from 2025-07-02T09:00 to 2025-07-02T10:00\n"
                   + "create event Lecture from 2025-07-03T09:00 to 2025-07-03T10:00\n"
                   + "copy events between 2025-07-01 and 2025-07-03 --target Personal to "
                   + "2025-08-01\n" + "use calendar --name Personal\n"
                   + "print events from 2025-08-01T00:00 to 2025-08-01T23:59\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\nInteractive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Lecture' created successfully from 2025-07-01T09:00 to 2025-07-01T10:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Lecture' created successfully from 2025-07-02T09:00 to 2025-07-02T10:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Lecture' created successfully from 2025-07-03T09:00 to 2025-07-03T10:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Events from 2025-07-01 to 2025-07-03 copied from calendar 'Work' to 'Personal' "
        + "starting on 2025-08-01.\n" + "\n" + "Enter command:\n" + "\n"
        + "Using calendar 'Personal'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Events from 2025-08-01T00:00 to 2025-08-01T23:59:\n"
        + "Lecture; From 2025-08-01T15:00 to 2025-08-01T16:00; Public\n" + "\n" + "Enter command:\n"
        + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testCopyEventsBetweenDatesCase2() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Seminar from 2025-09-10T14:00 to 2025-09-10T15:30\n"
                   + "edit events start Seminar 2025-09-10T15:30\n"
                   + "create event Seminar from 2025-09-11T14:00 to 2025-09-11T15:30\n"
                   + "copy events between 2025-09-10 and 2025-09-11 --target Personal to "
                   + "2025-10-01\n" + "use calendar --name Personal\n"
                   + "print events from 2025-10-01T00:00 to 2025-10-01T23:59\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\nInteractive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Seminar' created successfully from 2025-09-10T14:00 to 2025-09-10T15:30.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Error: Error editing event: Start time must be before end time\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Single event 'Seminar' created successfully from 2025-09-11T14:00 to 2025-09-11T15:30.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Events from 2025-09-10 to 2025-09-11 copied from calendar 'Work' to 'Personal' "
        + "starting on 2025-10-01.\n" + "\n" + "Enter command:\n" + "\n"
        + "Using calendar 'Personal'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Events from 2025-10-01T00:00 to 2025-10-01T23:59:\n"
        + "Seminar; From 2025-10-01T20:00 to 2025-10-01T21:30; Public\n" + "\n" + "Enter command:\n"
        + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testCopyEventsOnDateAtomic() {
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "create calendar --name Personal --timezone Europe/Paris", "use calendar --name Personal",
        "create event ConflictEvent from 2024-03-11T16:00 to " + "2024-03-11T17:00",
        "use calendar --name Work",
        "create event Meeting1 from 2024-03-11T10:00 to 2024-03-11T11:00",
        "create event Meeting2 from 2024-03-11T11:00 to 2024-03-11T12:00",
        "copy events on 2024-03-11 --target Personal to 2024-03-11", "use calendar --name Personal",
        "print events on 2024-03-11", "exit");
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Personal'.\n" + "\n" + "Enter command:\n"
        + "\n" + "Single event 'ConflictEvent' created successfully from 2024-03-11T16:00 to "
        + "2024-03-11T17:00.\n" + "\n" + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Meeting1' created successfully from 2024-03-11T10:00 to 2024-03-11T11:00"
        + ".\n" + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Meeting2' created successfully from 2024-03-11T11:00 to 2024-03-11T12:00"
        + ".\n" + "\n" + "Enter command:\n" + "\n"
        + "Error: Error copying event: Event conflicts with existing event.\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Personal'.\n" + "\n" + "Enter command:\n"
        + "\n" + "Events on 2024-03-11:\n"
        + "ConflictEvent; From 2024-03-11T16:00 to 2024-03-11T17:00; Public\n" + "\n"
        + "Enter command:\n" + "\n" + "Exiting interactive mode.";
    assertTrue(output.contains(expected));
  }

  @Test
  public void testCopyEventsBetweenDateAtomic() {
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "create calendar --name Personal --timezone America/New_York",
        "use calendar --name Personal",
        "create event ConflictEvent from 2024-03-17T10:00 to " + "2024-03-17T11:00",
        "use calendar --name Work",
        "create event Meeting from 2024-03-14T10:00 to 2024-03-14T11:00",
        "create event Seminar from 2024-03-15T10:00 to 2024-03-15T11:00",
        "copy events between 2024-03-14 and 2024-03-15 --target Personal " + "to 2024-03-16",
        "use calendar --name Personal", "print events on 2024-03-16", "print events on 2024-03-17",
        "exit");
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\nInteractive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Personal' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Personal'.\n" + "\n" + "Enter command:\n"
        + "\n" + "Single event 'ConflictEvent' created successfully from 2024-03-17T10:00 to "
        + "2024-03-17T11:00.\n" + "\n" + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Meeting' created successfully from 2024-03-14T10:00 to 2024-03-14T11:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Seminar' created successfully from 2024-03-15T10:00 to 2024-03-15T11:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Error: Error copying event: Event conflicts with existing event.\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Personal'.\n" + "\n" + "Enter command:\n"
        + "\n" + "No events on 2024-03-16\n" + "\n" + "Enter command:\n" + "\n"
        + "Events on 2024-03-17:\n"
        + "ConflictEvent; From 2024-03-17T10:00 to 2024-03-17T11:00; Public\n" + "\n"
        + "Enter command:\n" + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }


}
