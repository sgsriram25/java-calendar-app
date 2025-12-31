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
 * Test class for interactive mode with new commands (commands about calendar).
 */
public class InteractiveNewCommandsTest {

  private InputStream originalIn;
  private PrintStream originalOut;
  private ByteArrayOutputStream testOut;

  @Before
  public void setUp() {
    // Save original streams
    originalIn = System.in;
    originalOut = System.out;
    // Redirect output to capture it
    testOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));
  }

  @After
  public void tearDown() {
    // Restore original streams
    System.setIn(originalIn);
    System.setOut(originalOut);
  }

  @Test
  public void testCreateCalendarValid() {
    String input = "create calendar --name Work --timezone America/New_York\nexit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Work' created successfully. Timezone: America/New_York\n"
                      + "\nEnter command:\n" + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testCreateCalendarDuplicate() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Work --timezone Europe/Paris\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Work' created successfully. Timezone: America/New_York\n"
                      + "\nEnter command:\n" + "\nError: Calendar name Work already exists.\n"
                      + "\nEnter command:\n" + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testEditCalendarValid() {
    String input =
        "create calendar --name Work --timezone America/New_York\n" + "use calendar --name Work\n"
        + "create event example from 2025-03-05T22:00 to 2025-03-05T23:00 repeats MW " + "for 3 "
        + "times\n" + "print events from 2025-03-01T10:00 to 2025-03-25T11:00\n"
        + "edit calendar --name Work --property name NewWork\n"
        + "print events from 2025-03-01T10:00 to 2025-03-25T11:00\n"
        + "print events from 2023-03-01T10:00 to 2023-03-23T11:00\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Recurring event 'example' created for 3 times on [M, W].\n" + "\n" + "Enter command:\n"
        + "\n" + "Events from 2025-03-01T10:00 to 2025-03-25T11:00:\n"
        + "example; From 2025-03-05T22:00 to 2025-03-05T23:00; Public\n"
        + "example; From 2025-03-10T22:00 to 2025-03-10T23:00; Public\n"
        + "example; From 2025-03-12T22:00 to 2025-03-12T23:00; Public\n" + "\n" + "Enter command:\n"
        + "\n" + "Calendar 'Work' updated successfully: name changed to NewWork.\n" + "\n"
        + "Enter command:\n" + "\n" + "Events from 2025-03-01T10:00 to 2025-03-25T11:00:\n"
        + "example; From 2025-03-05T22:00 to 2025-03-05T23:00; Public\n"
        + "example; From 2025-03-10T22:00 to 2025-03-10T23:00; Public\n"
        + "example; From 2025-03-12T22:00 to 2025-03-12T23:00; Public\n" + "\n" + "Enter command:\n"
        + "\n" + "No events from 2023-03-01T10:00 to 2023-03-23T11:00\n" + "\n" + "Enter command:\n"
        + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testRecurringEventAppearsOnSameDayAfterTimezoneChange() {
    String input =
        String.join("\n", "create calendar --name JetLag --timezone UTC", // create in UTC
            "use calendar --name JetLag",
            // create 2 events at 23:30-23:50 UTC on Mon/Wed (not overnight in UTC)
            "create event JetSet from 2025-04-07T20:30 to 2025-04-07T23:50 repeats MW for 2 times",
            // change timezone so events now span past midnight (e.g., UTC+2)
            "edit calendar --name JetLag --property timezone Europe/Athens",
            // query both April 7 and April 8 (event should show up on both due to timezone shift)
            "print events on 2025-04-07", "print events on 2025-04-08", "exit");

    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Assertion to make sure event appears on BOTH days after timezone change
    assertTrue(output.contains(
        "Events on 2025-04-07:\n" + "JetSet; From 2025-04-07T23:30 to 2025-04-08T02:50; Public\n"
        + "\n" + "Enter command:\n" + "\n" + "Events on 2025-04-08:\n"
        + "JetSet; From 2025-04-07T23:30 to 2025-04-08T02:50; Public\n" + "\n" + "Enter command:"));
  }

  @Test
  public void testRecurringEventAppearsOnNextDayAfterTimezoneChange() {
    String input =
        String.join("\n", "create calendar --name JetLag --timezone UTC", // create in UTC
            "use calendar --name JetLag",
            // create 2 events at 23:30-23:50 UTC on Mon/Wed (not overnight in UTC)
            "create event JetSet from 2025-04-07T23:30 to 2025-04-07T23:50 repeats MW for 10 times",
            // change timezone so events now span past midnight (e.g., UTC+2)
            "edit calendar --name JetLag --property timezone Europe/Athens",
            // query both April 7 and April 8 (event should show up on both due to timezone shift)
            "print events from 2025-04-07T00:00 to 2026-04-05T00:00", "exit");

    System.setIn(new ByteArrayInputStream(input.getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Assertion to make sure event appears on BOTH days after timezone change
    assertTrue(output.contains("JetSet; From 2025-04-08T02:30 to 2025-04-08T02:50; Public\n"
                               + "JetSet; From 2025-04-10T02:30 to 2025-04-10T02:50; Public\n"
                               + "JetSet; From 2025-04-15T02:30 to 2025-04-15T02:50; Public\n"
                               + "JetSet; From 2025-04-17T02:30 to 2025-04-17T02:50; Public\n"
                               + "JetSet; From 2025-04-22T02:30 to 2025-04-22T02:50; Public\n"
                               + "JetSet; From 2025-04-24T02:30 to 2025-04-24T02:50; Public\n"
                               + "JetSet; From 2025-04-29T02:30 to 2025-04-29T02:50; Public\n"
                               + "JetSet; From 2025-05-01T02:30 to 2025-05-01T02:50; Public\n"
                               + "JetSet; From 2025-05-06T02:30 to 2025-05-06T02:50; Public\n"
                               + "JetSet; From 2025-05-08T02:30 to 2025-05-08T02:50; Public\n"));
  }


  @Test
  public void testEditCalendarTimezone() {
    String input = "create calendar --name Personal --timezone Asia/Kolkata\n"
                   + "edit calendar --name Personal --property timezone Europe/London\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Personal' created successfully. Timezone: Asia/Kolkata\n"
                      + "\nEnter command:\n"
                      + "\nCalendar 'Personal' updated successfully: timezone changed to "
                      + "Europe/London.\n" + "\nEnter command:\n" + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testEditCalendarInvalid() {
    String input = "edit calendar --name Fake --property timezone Europe/Paris\nexit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nError: Calendar not found.\n" + "\nEnter command:\n"
                      + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testUseCalendar() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/London\n"
                   + "use calendar --name Personal\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Work' created successfully. Timezone: America/New_York\n"
                      + "\nEnter command:\n"
                      + "\nCalendar 'Personal' created successfully. Timezone: Europe/London\n"
                      + "\nEnter command:\n" + "\nUsing calendar 'Personal'.\n"
                      + "\nEnter command:\n" + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testCopyEvent() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00\n"
                   + "copy event Meeting on 2025-03-05T10:00 --target Personal to "
                   + "2025-03-06T09:00\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Note: The exact output message depends on your implementation.
    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Work' created successfully. Timezone: America/New_York\n"
                      + "\nEnter command:\n"
                      + "\nCalendar 'Personal' created successfully. Timezone: Europe/Paris\n"
                      + "\nEnter command:\n" + "\nUsing calendar 'Work'.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Meeting' created successfully from 2025-03-05T10:00 to "
                      + "2025-03-05T11:00.\n" + "\nEnter command:\n"
                      + "\nEvent 'Meeting' copied from 2025-03-05T10:00 in calendar 'Work' to "
                      + "2025-03-06T09:00 in calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testCopyEventNotFound() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00\n"
                   + "copy event Meeting on 2025-03-05T12:00 --target Personal to "
                   + "2025-03-06T09:00\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Note: The exact output message depends on your implementation.
    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Meeting' created successfully from 2025-03-05T10:00 to 2025-03-05T11:00.\n"
        + "\n" + "Enter command:\n" + "\n" + "Error: Event not found.\n" + "\n" + "Enter command:\n"
        + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testCopyEventsOnDate() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00\n"
                   + "create event Meeting from 2025-03-05T12:00 to 2025-03-05T13:00\n"
                   + "copy events on 2025-03-05 --target Personal to 2025-03-06\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected = "\nInteractive mode started. Type 'exit' to quit.\n" + "\nEnter command:\n"
                      + "\nCalendar 'Work' created successfully. Timezone: America/New_York\n"
                      + "\nEnter command:\n"
                      + "\nCalendar 'Personal' created successfully. Timezone: Europe/Paris\n"
                      + "\nEnter command:\n" + "\nUsing calendar 'Work'.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Meeting' created successfully from 2025-03-05T10:00 to "
                      + "2025-03-05T11:00.\n" + "\nEnter command:\n"
                      + "\nSingle event 'Meeting' created successfully from 2025-03-05T12:00 to "
                      + "2025-03-05T13:00.\n" + "\nEnter command:\n"
                      + "\nEvents on 2025-03-05 copied from calendar 'Work' to 2025-03-06 in "
                      + "calendar 'Personal'.\n" + "\nEnter command:\n"
                      + "\nExiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testCopyEventsBetweenDates() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Lecture from 2025-03-03T09:00 to 2025-03-03T10:00\n"
                   + "create event Lecture from 2025-03-04T09:00 to 2025-03-04T10:00\n"
                   + "create event Lecture from 2025-03-05T09:00 to 2025-03-05T10:00\n"
                   + "copy events between 2025-03-03 and 2025-03-05 --target Personal to "
                   + "2025-04-01\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Calendar 'Personal' created successfully. Timezone: Europe/Paris\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Lecture' created successfully from 2025-03-03T09:00 to 2025-03-03T10:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Lecture' created successfully from 2025-03-04T09:00 to 2025-03-04T10:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Lecture' created successfully from 2025-03-05T09:00 to 2025-03-05T10:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Events from 2025-03-03 to 2025-03-05 copied from calendar 'Work' to 'Personal' "
        + "starting on 2025-04-01.\n" + "\n" + "Enter command:\n" + "\n"
        + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }
}
