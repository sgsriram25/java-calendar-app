import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * Test class for interactive mode with old commands.
 */
public class InteractiveOldCommandsTest {

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
  public void testCopyEventEditSeries() {
    String input = "create calendar --name Work --timezone America/New_York\n"
                   + "create calendar --name Personal --timezone Europe/Paris\n"
                   + "use calendar --name Work\n"
                   + "create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00\n"
                   + "create event Meeting from 2025-03-07T10:00 to 2025-03-07T11:00\n"
                   + "create event Meeting on 2025-03-06 repeats MTWRFSU for 3 times\n"
                   + "edit events subject Meeting from 2025-03-06T10:00 with newMeeting\n"
                   + "print events on 2025-03-05\n" + "show status on 2025-03-05T00:00\n"
                   + "print events on 2025-03-07\n" + "exit" + "\n";
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
        + "\n" + "Enter command:\n" + "\n"
        + "Single event 'Meeting' created successfully from 2025-03-07T10:00 to 2025-03-07T11:00.\n"
        + "\n" + "Enter command:\n" + "\n"
        + "Error: Error creating event: Event conflicts with existing event.\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'Meeting' after 2025-03-06T10:00 updated successfully: subject "
        + "changed to newMeeting.\n" + "\n" + "Enter command:\n" + "\n" + "Events on 2025-03-05:\n"
        + "Meeting; From 2025-03-05T10:00 to 2025-03-05T11:00; Public\n" + "\n" + "Enter command:\n"
        + "\n" + "User is available at 2025-03-05T00:00\n" + "\n" + "Enter command:\n" + "\n"
        + "Events on 2025-03-07:\n"
        + "newMeeting; From 2025-03-07T10:00 to 2025-03-07T11:00; Public\n" + "\n"
        + "Enter command:\n" + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testCreateRecurringEvents() {
    String input =
        "create calendar --name Work --timezone America/New_York\n" + "use calendar --name Work\n"
        + "create event Meeting on 2025-03-06 repeats MTWRFSU for 3 times\n"
        + "edit events repeats Meeting -1\n" + "edit events repeats Meeting 0\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Note: The exact output message depends on your implementation.
    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Recurring event 'Meeting' created for 3 times on [M, T, W, R, F, S, U].\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Error: Error editing event: Number of repeats must be one or more\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Error: Error editing event: Number of repeats must be one or more\n" + "\n"
        + "Enter command:\n" + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testEditSingleOccurrence() {
    String input =
        "create calendar --name Work --timezone America/New_York\n" + "use calendar --name Work\n"
        + "create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00 repeats MTWRFSU for 3 "
        + "times\n"
        + "edit event subject Meeting from 2025-03-06T10:00 to 2025-03-06T11:00 with happy-hour\n"
        + "print events from 2025-03-02T10:00 to 2025-03-22T11:00\n"
        + "edit event subject Meeting from 2025-03-05T10:00 to 2025-03-05T11:00 with happy-hour\n"
        + "print events from 2025-03-02T10:00 to 2025-03-22T11:00\n"
        + "edit event subject happy-hour from 2025-03-06T10:00 to 2025-03-06T11:00 with "
        + "happy-hour-2\n" + "print events from 2025-03-02T10:00 to 2025-03-22T11:00\n"
        + "edit event subject Meeting from 2025-03-07T10:00 to 2025-03-07T11:00 with happy-hour\n"
        + "print events from 2025-03-02T10:00 to 2025-03-22T11:00\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Note: The exact output message depends on your implementation.
    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Recurring event 'Meeting' created for 3 times on [M, T, W, R, F, S, U].\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Event 'Meeting' updated successfully: subject changed to happy-hour.\n" + "\n"
        + "Enter command:\n" + "\n" + "Events from 2025-03-02T10:00 to 2025-03-22T11:00:\n"
        + "Meeting; From 2025-03-05T10:00 to 2025-03-05T11:00; Public\n"
        + "Meeting; From 2025-03-07T10:00 to 2025-03-07T11:00; Public\n"
        + "happy-hour; From 2025-03-06T10:00 to 2025-03-06T11:00; Public\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Event 'Meeting' updated successfully: subject changed to happy-hour.\n" + "\n"
        + "Enter command:\n" + "\n" + "Events from 2025-03-02T10:00 to 2025-03-22T11:00:\n"
        + "Meeting; From 2025-03-07T10:00 to 2025-03-07T11:00; Public\n"
        + "happy-hour; From 2025-03-06T10:00 to 2025-03-06T11:00; Public\n"
        + "happy-hour; From 2025-03-05T10:00 to 2025-03-05T11:00; Public\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Event 'happy-hour' updated successfully: subject changed to happy-hour-2.\n" + "\n"
        + "Enter command:\n" + "\n" + "Events from 2025-03-02T10:00 to 2025-03-22T11:00:\n"
        + "happy-hour-2; From 2025-03-06T10:00 to 2025-03-06T11:00; Public\n"
        + "Meeting; From 2025-03-07T10:00 to 2025-03-07T11:00; Public\n"
        + "happy-hour; From 2025-03-05T10:00 to 2025-03-05T11:00; Public\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Event 'Meeting' updated successfully: subject changed to happy-hour.\n" + "\n"
        + "Enter command:\n" + "\n" + "Events from 2025-03-02T10:00 to 2025-03-22T11:00:\n"
        + "happy-hour-2; From 2025-03-06T10:00 to 2025-03-06T11:00; Public\n"
        + "happy-hour; From 2025-03-05T10:00 to 2025-03-05T11:00; Public\n"
        + "happy-hour; From 2025-03-07T10:00 to 2025-03-07T11:00; Public\n" + "\n"
        + "Enter command:\n" + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testEditSeriesOccurrence() {
    String input =
        "create calendar --name Work --timezone America/New_York\n" + "use calendar --name Work\n"
        + "create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00 repeats MTWRFSU for 3 "
        + "times\n" + "edit events subject Meeting from 2025-03-02T10:00 with happy-hour\n"
        + "print events from 2025-03-02T10:00 to 2025-03-22T11:00\n"
        + "edit event subject happy-hour from 2025-03-06T10:00 to 2025-03-06T12:00 with "
        + "happy-hour-4\n"
        + "edit events subject happy-hour from 2025-03-06T10:00 with happy-hour-2\n"
        + "edit event subject happy-hour-2 from 2025-03-07T10:00 to 2025-03-07T11:00 with "
        + "happy-hour-3\n"
        + "edit event subject happy-hour-3 from 2025-03-07T10:00 to 2025-03-07T12:00 with "
        + "happy-hour-4\n" + "print events from 2025-03-02T10:00 to 2025-03-22T11:00\n" + "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Note: The exact output message depends on your implementation.
    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Recurring event 'Meeting' created for 3 times on [M, T, W, R, F, S, U].\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'Meeting' after 2025-03-02T10:00 updated successfully: subject "
        + "changed to happy-hour.\n" + "\n" + "Enter command:\n" + "\n"
        + "Events from 2025-03-02T10:00 to 2025-03-22T11:00:\n"
        + "happy-hour; From 2025-03-05T10:00 to 2025-03-05T11:00; Public\n"
        + "happy-hour; From 2025-03-06T10:00 to 2025-03-06T11:00; Public\n"
        + "happy-hour; From 2025-03-07T10:00 to 2025-03-07T11:00; Public\n" + "\n"
        + "Enter command:\n" + "\n" + "Error: Error editing event: No such event exists\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'happy-hour' after 2025-03-06T10:00 updated successfully: subject "
        + "changed to happy-hour-2.\n" + "\n" + "Enter command:\n" + "\n"
        + "Event 'happy-hour-2' updated successfully: subject changed to happy-hour-3.\n" + "\n"
        + "Enter command:\n" + "\n" + "Error: Error editing event: No such event exists\n" + "\n"
        + "Enter command:\n" + "\n" + "Events from 2025-03-02T10:00 to 2025-03-22T11:00:\n"
        + "happy-hour-2; From 2025-03-06T10:00 to 2025-03-06T11:00; Public\n"
        + "happy-hour-3; From 2025-03-07T10:00 to 2025-03-07T11:00; Public\n"
        + "happy-hour; From 2025-03-05T10:00 to 2025-03-05T11:00; Public\n" + "\n"
        + "Enter command:\n" + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }

  @Test
  public void testEditRecurringEvents() {
    String input = String.join("\n", "create calendar --name Work --timezone America/New_York",
        "use calendar --name Work",
        // Create a recurring event: OfficeHours, starting at 09:00–10:00
        // on 2024-04-01,
        // repeating on Monday, Tuesday, Wednesday, Thursday, Friday
        // (MTWRF) for 5 occurrences.
        "create event --autoDecline OfficeHours from 2024-04-01T09:00 to "
        + "2024-04-01T10:00 repeats" + " MTW for 5 times", "print events on 2024-04-01",
        // Edit each property one by one.
        "edit events subject OfficeHours OfficeHours_New", "print events on 2024-04-01",
        "edit events start OfficeHours_New 2024-04-01T08:30", "print events on 2024-04-01",
        "edit events end OfficeHours_New 2024-04-01T10:30", "print events on 2024-04-01",
        "edit events description OfficeHours_New " + "Updated_description_for_office_hours",
        "print events on 2024-04-01", "edit events location OfficeHours_New Conference_Room_101",
        "print events on 2024-04-01", "edit events public OfficeHours_New false",
        "print events on 2024-04-01", "edit events daysofweek OfficeHours_New MTWRFSU",
        "print events on 2024-04-05", "edit events repeats OfficeHours_New 7",
        "print events on 2024-04-07", "edit events until OfficeHours_New 2024-04-15T10:00",
        "print events on 2024-04-15", "edit events end OfficeHours_New 2024-04-02T08:30", "exit");
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    CalendarApp.main(new String[]{"--mode", "interactive"});
    String output = testOut.toString().replace("\r\n", "\n");

    // Note: The exact output message depends on your implementation.
    String expected =
        "\n" + "Interactive mode started. Type 'exit' to quit.\n" + "\n" + "Enter command:\n" + "\n"
        + "Calendar 'Work' created successfully. Timezone: America/New_York\n" + "\n"
        + "Enter command:\n" + "\n" + "Using calendar 'Work'.\n" + "\n" + "Enter command:\n" + "\n"
        + "Recurring event 'OfficeHours' created for 5 times on [M, T, W].\n" + "\n"
        + "Enter command:\n" + "\n" + "Events on 2024-04-01:\n"
        + "OfficeHours; From 2024-04-01T09:00 to 2024-04-01T10:00; Public\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'OfficeHours' updated successfully: subject changed to "
        + "OfficeHours_New.\n" + "\n" + "Enter command:\n" + "\n" + "Events on 2024-04-01:\n"
        + "OfficeHours_New; From 2024-04-01T09:00 to 2024-04-01T10:00; Public\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'OfficeHours_New' updated successfully: start changed to "
        + "2024-04-01T08:30.\n" + "\n" + "Enter command:\n" + "\n" + "Events on 2024-04-01:\n"
        + "OfficeHours_New; From 2024-04-01T08:30 to 2024-04-01T10:00; Public\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'OfficeHours_New' updated successfully: end changed to "
        + "2024-04-01T10:30.\n" + "\n" + "Enter command:\n" + "\n" + "Events on 2024-04-01:\n"
        + "OfficeHours_New; From 2024-04-01T08:30 to 2024-04-01T10:30; Public\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'OfficeHours_New' updated successfully: description changed to "
        + "Updated_description_for_office_hours.\n" + "\n" + "Enter command:\n" + "\n"
        + "Events on 2024-04-01:\n"
        + "OfficeHours_New; From 2024-04-01T08:30 to 2024-04-01T10:30; Public; Description: "
        + "Updated_description_for_office_hours\n" + "\n" + "Enter command:\n" + "\n"
        + "All events named 'OfficeHours_New' updated successfully: location changed to "
        + "Conference_Room_101.\n" + "\n" + "Enter command:\n" + "\n" + "Events on 2024-04-01:\n"
        + "OfficeHours_New; From 2024-04-01T08:30 to 2024-04-01T10:30; Public; Description: "
        + "Updated_description_for_office_hours; Location: Conference_Room_101\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'OfficeHours_New' updated successfully: public changed to false.\n"
        + "\n" + "Enter command:\n" + "\n" + "Events on 2024-04-01:\n"
        + "OfficeHours_New; From 2024-04-01T08:30 to 2024-04-01T10:30; Private; Description: "
        + "Updated_description_for_office_hours; Location: Conference_Room_101\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'OfficeHours_New' updated successfully: daysofweek changed to MTWRFSU"
        + ".\n" + "\n" + "Enter command:\n" + "\n" + "Events on 2024-04-05:\n"
        + "OfficeHours_New; From 2024-04-05T08:30 to 2024-04-05T10:30; Private; Description: "
        + "Updated_description_for_office_hours; Location: Conference_Room_101\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'OfficeHours_New' updated successfully: repeats changed to 7.\n" + "\n"
        + "Enter command:\n" + "\n" + "Events on 2024-04-07:\n"
        + "OfficeHours_New; From 2024-04-07T08:30 to 2024-04-07T10:30; Private; Description: "
        + "Updated_description_for_office_hours; Location: Conference_Room_101\n" + "\n"
        + "Enter command:\n" + "\n"
        + "All events named 'OfficeHours_New' updated successfully: until changed to "
        + "2024-04-15T10:00.\n" + "\n" + "Enter command:\n" + "\n" + "Events on 2024-04-15:\n"
        + "OfficeHours_New; From 2024-04-15T08:30 to 2024-04-15T10:30; Private; Description: "
        + "Updated_description_for_office_hours; Location: Conference_Room_101\n" + "\n"
        + "Enter command:\n" + "\n"
        + "Error: Error editing event: Start and end time must be on the same day\n" + "\n"
        + "Enter command:\n" + "\n" + "Exiting interactive mode.\n";
    assertEquals(expected, output);
  }


  @Test
  public void testCopyEventEditSeriesEvents() {
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


}
