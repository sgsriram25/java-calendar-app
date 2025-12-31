import controller.InteractiveTextController;
import fakes.FakeCalendarManager;
import fakes.FakeView;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for InteractiveController.
 */
public class InteractiveControllerTest {

  private FakeCalendarManager fakeCalendarManager;
  private FakeView fakeView;
  private InteractiveTextController controller;

  @Before
  public void setUp() {
    fakeCalendarManager = new FakeCalendarManager();
    fakeView = new FakeView();

  }

  @Test
  public void testAllSupportedCommands() {
    // Build an input stream that issues one command for every supported command.
    // Note: The exact command syntax must match what the CommandFactory expects.
    String input = String.join("\n",
                               // Calendar commands:
                               "create calendar --name Cal1 --timezone America/New_York",
                               "edit calendar --name Cal1 --property timezone Europe/Paris",
                               "use calendar --name Cal1",
                               "create calendar --name Cal2 --timezone America/New_York",
                               "edit calendar --name Cal2 --property timezone Europe/Paris",
                               // Single event commands:
                               "create event --autoDecline Meeting from 2025-03-05T10:00 to "
                               + "2025-03-05T11:00",
                               // Recurring event commands:
                               "create event Standup from 2025-03-06T09:00 to 2025-03-06T09:30 "
                               + "repeats MTWRF for 5 times",
                               "create event --autoDecline Yoga from 2025-03-07T08:00 to "
                               + "2025-03-07T09:00 repeats MRU " + "until 2025-06-01T00:00",
                               // Edit event commands:
                               "edit event location Meeting from 2025-03-05T10:00 to "
                               + "2025-03-05T11:00 with Room101",
                               "edit events description Conference from 2025-03-05T10:00 with Math",
                               "edit events location Conference Room101",
                               // Print events commands:
                               "print events on 2025-03-08",
                               "print events from 2025-03-08T00:00 to 2025-03-10T23:59",
                               // Export calendar:
                               "exportCalendar cal calendar.csv",
                               // Show status:
                               "show status on 2025-03-09T14:00",
                               // Copy commands:
                               "copy event Meeting on 2025-03-05T10:00 --target Cal1 to "
                               + "2025-03-05T12:00",
                               "copy events on 2025-03-05 --target Cal1 to 2025-03-05",
                               "copy events between 2025-03-05 and 2025-03-06 --target Cal1 to "
                               + "2025-03-15", "show status on 2025-03-09T14:00",
                               "create event Standup on 2025-03-06 repeats MTWRF for 5 times",
                               "create event --autoDecline Meeting on 2025-03-05T10:00",
                               "create event --autoDecline Yoga on 2025-03-07 repeats MRU until"
                               + " 2025-06-01",
                               "create event --autoDecline Meeting from 2025-03-05T10:00 to "
                               + "2025-03-05T11:00 extraToken",
                               "create event Standup from 2025-03-06T09:00 to 2025-03-06T09:30 "
                               + "repeats MTWRF for hello " + "times",
                               "create event Standup from 2025-03-06T09:00 to 2025-03-06T09:30 "
                               + "repeats MTWRF for 5 times" + " extraToken",
                               "create event --autoDecline Yoga on 2025-03-07 repeats MRU until"
                               + " 2025-06-01 extraToken",
                               // Terminate session.
                               "exit");

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Build the expected list of calls.
    // Note: The expected call strings must match what the commands produce.
    List<List<String>> expectedCalls =
        List.of(List.of("createCalendar called", "[Cal1, America/New_York]"),
                List.of("editCalendar called", "[Cal1, timezone, Europe/Paris]"),
                List.of("detectCalendar called", "[Cal1]"),
                List.of("createCalendar called", "[Cal2, America/New_York]"),
                List.of("editCalendar called", "[Cal2, timezone, Europe/Paris]"),
                List.of("createSingleEvent called",
                        "[Cal1, Meeting, 2025-03-05T10:00, 2025-03-05T11:00]"),
                List.of("createRecurringEvent called",
                        "[Cal1, Standup, 2025-03-06T09:00, 2025-03-06T09:30, [M, T, W, R, F], 5]"),
                List.of("createRecurringEvent called",
                        "[Cal1, Yoga, 2025-03-07T08:00, 2025-03-07T09:00, [M, R, U], "
                        + "2025-06-01T00:00]"), List.of("editSingleEvent called",
                                                        "[Cal1, Meeting, 2025-03-05T10:00, "
                                                        + "2025-03-05T11:00, location, Room101]"),
                List.of("editMultipleEventsFrom called",
                        "[Cal1, Conference, 2025-03-05T10:00, description, Math]"),
                List.of("editMultipleEvents called", "[Cal1, Conference, location, Room101]"),
                List.of("getEventsOnDate called", "[Cal1, 2025-03-08]"),
                List.of("getEventsInRange called", "[Cal1, 2025-03-08T00:00, 2025-03-10T23:59]"),
                List.of("exportCalendar called", "[Cal1]"),
                List.of("isBusy called", "[Cal1, 2025-03-09T14:00]"),
                List.of("copySingleEvent called",
                        "[Cal1, Meeting, 2025-03-05T10:00, Cal1, 2025-03-05T12:00]"),
                List.of("copyEventsOnDate called", "[Cal1, 2025-03-05, Cal1, 2025-03-05]"),
                List.of("copyEventsBetweenDates called",
                        "[Cal1, 2025-03-05, 2025-03-06, Cal1, " + "2025-03-15]"),
                List.of("isBusy called", "[Cal1, 2025-03-09T14:00]"),
                List.of("createRecurringEvent called", "[Cal1, Standup, 2025-03-06T00:00, "
                                                       + "2025-03-06T23:59:59, [M, T, W, R, F], "
                                                       + "5]"),
                List.of("createSingleEvent called", "[Cal1, Meeting, 2025-03-05T10:00, null]"),
                List.of("createRecurringEvent called",
                        "[Cal1, Yoga, 2025-03-07T00:00, 2025-03-07T23:59:59, [M, R, U], "
                        + "2025-06-01T23:59:59]"));

    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();
    assertEquals("Expected " + expectedCalls.size() + " calls.", expectedCalls.size(),
                 allCalls.size());
    for (int i = 0; i < expectedCalls.size(); i++) {
      assertEquals("Expected call " + i, expectedCalls.get(i), allCalls.get(i));
    }

    // Optionally, verify a few view messages.

    assertTrue(fakeView.getMessages()
                   .contains("Calendar 'Cal1' created successfully. Timezone: America/New_York"));
    assertTrue(fakeView.getErrors().contains("Invalid occurrence number: expecting an integer."));
    assertTrue(
        fakeView.getErrors().contains("Failed to exportCalendar calendar. No events found."));
    assertTrue(fakeView.getMessages().contains("Switched to calendar 'Cal1'."));
    // (Additional assertions for other view messages can be added as needed.)
  }

  @Test
  public void testSupportedCommandsWithoutUseCalendar() {
    // Build an input stream that issues one command for every supported command.
    // Note: The exact command syntax must match what the CommandFactory expects.
    String input = String.join("\n",
                               // Calendar commands:
                               "create calendar --name Cal1 --timezone America/New_York",
                               "edit calendar --name Cal1 --property timezone Europe/Paris",
                               // Single event commands:
                               "create event --autoDecline Meeting from 2025-03-05T10:00 to "
                               + "2025-03-05T11:00",
                               // Recurring event commands:
                               "create event Standup from 2025-03-06T09:00 to 2025-03-06T09:30 "
                               + "repeats MTWRF for 5 times",
                               "create event --autoDecline Yoga from 2025-03-07T08:00 to "
                               + "2025-03-07T09:00 repeats MRU " + "until 2025-06-01T00:00",
                               // Edit event commands:
                               "edit event location Meeting from 2025-03-05T10:00 to "
                               + "2025-03-05T11:00 with Room101",
                               "edit events description Conference from 2025-03-05T10:00 with Math",
                               "edit events location Conference Room101",
                               // Print events commands:
                               "print events on 2025-03-08",
                               "print events from 2025-03-08T00:00 to 2025-03-10T23:59",
                               // Export calendar:
                               "exportCalendar cal calendar.csv",
                               // Show status:
                               "show status on 2025-03-09T14:00",
                               // Copy commands:
                               "copy event Meeting on 2025-03-05T10:00 --target Cal1 to "
                               + "2025-03-05T12:00",
                               "copy events on 2025-03-05 --target Cal1 to 2025-03-05",
                               "copy events between 2025-03-05 and 2025-03-06 --target Cal1 to "
                               + "2025-03-15", "show status on 2025-03-09T14:00",
                               // Terminate session.
                               "exit");

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Build the expected list of calls.
    // Note: The expected call strings must match what the commands produce.
    List<List<String>> expectedCalls =
        List.of(List.of("createCalendar called", "[Cal1, America/New_York]"),
                List.of("editCalendar called", "[Cal1, timezone, Europe/Paris]"));

    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();
    assertEquals("Expected " + expectedCalls.size() + " calls.", expectedCalls.size(),
                 allCalls.size());
    for (int i = 0; i < expectedCalls.size(); i++) {
      assertEquals("Expected call " + i, expectedCalls.get(i), allCalls.get(i));
    }

    // Optionally, verify a few view messages.

    assertTrue(fakeView.getMessages()
                   .contains("Calendar 'Cal1' created successfully. Timezone: America/New_York"));
  }


  @Test
  public void testCreateCalendar() {
    // Simulate user input for creating a new calendar.
    String input = "create calendar --name MyCalendar --timezone America/New_York\nexit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Retrieve all calls from the fake calendar manager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting one call for the create calendar command.
    List<String> expectedCall = List.of("createCalendar called", "[MyCalendar, America/New_York]");
    assertEquals(1, allCalls.size());
    assertEquals(expectedCall, allCalls.get(0));

    // Verify that the view displays the expected creation message.
    String expectedMessage =
        "Calendar 'MyCalendar' created successfully. Timezone: " + "America/New_York";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }


  @Test
  public void testEditCalendar() {
    // Simulate user input for editing an existing calendar.
    String input = "edit calendar --name MyCalendar --property timezone Europe/Paris\nexit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Retrieve all calls from the fake calendar manager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting one call for the edit calendar command.
    List<String> expectedCall =
        List.of("editCalendar called", "[MyCalendar, timezone, Europe/Paris]");
    assertEquals(1, allCalls.size());
    assertEquals(expectedCall, allCalls.get(0));

    // Verify that the view displays the expected edit confirmation message.
    String expectedMessage =
        "Calendar 'MyCalendar' updated successfully: timezone changed to Europe/Paris.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }

  @Test
  public void testEditCalendarName() {
    // Simulate user input for editing the calendar name.
    String input = "edit calendar --name MyCalendar --property name NewCalendar\nexit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Retrieve all calls from the fake calendar manager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting one call for the edit calendar command.
    List<String> expectedCall = List.of("editCalendar called", "[MyCalendar, name, NewCalendar]");
    assertEquals(1, allCalls.size());
    assertEquals(expectedCall, allCalls.get(0));

    // Verify that the view displays the expected edit confirmation message.
    String expectedMessage =
        "Calendar 'MyCalendar' updated successfully: name changed to NewCalendar.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }


  @Test
  public void testUseCalendar() {
    // Simulate user input for switching calendar context.
    String input = "use calendar --name MyCalendar\nexit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Retrieve all calls from the fake calendar manager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting one call for the use calendar command.
    List<String> expectedCall = List.of("detectCalendar called", "[MyCalendar]");
    assertEquals(1, allCalls.size());
    assertEquals(expectedCall, allCalls.get(0));

    // Verify that the view displays the expected context switch message.
    String expectedMessage = "Switched to calendar 'MyCalendar'.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }


  @Test
  public void testCreateSingleEventWithAutoDecline() {
    // Combine the commands: first switch calendar, then create event, then exit.
    String input = "use calendar --name MyCalendar\n"
                   + "create event --autoDecline Meeting from 2025-03-05T10:00 to "
                   + "2025-03-05T11:00\n" + "exit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Retrieve all calls from the fakeCalendarManager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls: one for the use calendar command and one for the create event command.
    List<String> expectedUseCalendarCall = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCreateEventCall = List.of("createSingleEvent called",
                                                   "[MyCalendar, Meeting, 2025-03-05T10:00, "
                                                   + "2025-03-05T11:00]");

    // Verify that both calls were made.
    assertEquals(2, allCalls.size());
    assertEquals(expectedUseCalendarCall, allCalls.get(0));
    assertEquals(expectedCreateEventCall, allCalls.get(1));

    // Verify that the view displays the expected use calendar message.
    String expectedUseCalendarMessage = "Switched to calendar 'MyCalendar'.";
    assertTrue(fakeView.getMessages().contains(expectedUseCalendarMessage));

    // Verify that the view displays the expected create event success message.
    String expectedMessage =
        "Single event 'Meeting' created successfully from 2025-03-05T10:00 to 2025-03-05T11:00.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));

    // Verify interactive mode messages.
    String expectedStartMessage = "Interactive mode started. Type 'exit' to quit.";
    String expectedEnterMessage = "Enter command:";
    assertTrue(fakeView.getMessages().contains(expectedStartMessage));
    assertTrue(fakeView.getMessages().contains(expectedEnterMessage));
  }


  @Test
  public void testCreateSingleEventWithoutAutoDecline() {
    // Combine the commands: first switch calendar, then create event, then exit.
    String input =
        "use calendar --name MyCalendar\n" + "create event Meeting from 2025-03-05T10:00 to "
        + "2025-03-05T11:00\n" + "exit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Retrieve all calls from the fakeCalendarManager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls: one for the use calendar command and one for the create event command.
    List<String> expectedUseCalendarCall = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCreateEventCall = List.of("createSingleEvent called",
                                                   "[MyCalendar, Meeting, 2025-03-05T10:00, "
                                                   + "2025-03-05T11:00]");

    // Verify that both calls were made.
    assertEquals(2, allCalls.size());
    assertEquals(expectedUseCalendarCall, allCalls.get(0));
    assertEquals(expectedCreateEventCall, allCalls.get(1));

    // Verify that the view displays the expected use calendar message.
    String expectedUseCalendarMessage = "Switched to calendar 'MyCalendar'.";
    assertTrue(fakeView.getMessages().contains(expectedUseCalendarMessage));

    // Verify that the view displays the expected create event success message.
    String expectedMessage =
        "Single event 'Meeting' created successfully from 2025-03-05T10:00 to 2025-03-05T11:00.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));

    // Verify interactive mode messages.
    String expectedStartMessage = "Interactive mode started. Type 'exit' to quit.";
    String expectedEnterMessage = "Enter command:";
    assertTrue(fakeView.getMessages().contains(expectedStartMessage));
    assertTrue(fakeView.getMessages().contains(expectedEnterMessage));
  }


  @Test
  public void testCreateAllDayEventWithoutAutoDecline() {
    // Combine the commands: first switch calendar, then create event, then exit.
    String input =
        "use calendar --name MyCalendar\n" + "create event Meeting from 2025-03-05T10:00 to "
        + "2025-03-05T11:00\n" + "exit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Retrieve all calls from the fakeCalendarManager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls: one for the use calendar command and one for the create event command.
    List<String> expectedUseCalendarCall = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCreateEventCall = List.of("createSingleEvent called",
                                                   "[MyCalendar, Meeting, 2025-03-05T10:00, "
                                                   + "2025-03-05T11:00]");

    // Verify that both calls were made.
    assertEquals(2, allCalls.size());
    assertEquals(expectedUseCalendarCall, allCalls.get(0));
    assertEquals(expectedCreateEventCall, allCalls.get(1));

    // Verify that the view displays the expected use calendar message.
    String expectedUseCalendarMessage = "Switched to calendar 'MyCalendar'.";
    assertTrue(fakeView.getMessages().contains(expectedUseCalendarMessage));

    // Verify that the view displays the expected create event success message.
    String expectedMessage =
        "Single event 'Meeting' created successfully from 2025-03-05T10:00 to 2025-03-05T11:00.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));

    // Verify interactive mode messages.
    String expectedStartMessage = "Interactive mode started. Type 'exit' to quit.";
    String expectedEnterMessage = "Enter command:";
    assertTrue(fakeView.getMessages().contains(expectedStartMessage));
    assertTrue(fakeView.getMessages().contains(expectedEnterMessage));
  }

  @Test
  public void testCreateAllDayEventWithAutoDecline() {
    // Combine the commands: first switch calendar, then create event, then exit.
    String input = "use calendar --name MyCalendar\n"
                   + "create event --autoDecline Meeting from 2025-03-05T10:00 to "
                   + "2025-03-05T11:00\n" + "exit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }

    // Retrieve all calls from the fakeCalendarManager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls: one for the use calendar command and one for the create event command.
    List<String> expectedUseCalendarCall = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCreateEventCall = List.of("createSingleEvent called",
                                                   "[MyCalendar, Meeting, 2025-03-05T10:00, "
                                                   + "2025-03-05T11:00]");

    // Verify that both calls were made.
    assertEquals(2, allCalls.size());
    assertEquals(expectedUseCalendarCall, allCalls.get(0));
    assertEquals(expectedCreateEventCall, allCalls.get(1));

    // Verify that the view displays the expected use calendar message.
    String expectedUseCalendarMessage = "Switched to calendar 'MyCalendar'.";
    assertTrue(fakeView.getMessages().contains(expectedUseCalendarMessage));

    // Verify that the view displays the expected create event success message.
    String expectedMessage =
        "Single event 'Meeting' created successfully from 2025-03-05T10:00 to 2025-03-05T11:00.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));

    // Verify interactive mode messages.
    String expectedStartMessage = "Interactive mode started. Type 'exit' to quit.";
    String expectedEnterMessage = "Enter command:";
    assertTrue(fakeView.getMessages().contains(expectedStartMessage));
    assertTrue(fakeView.getMessages().contains(expectedEnterMessage));
  }

  @Test
  public void testCreateRecurringEventWithOccurrencesAndAutoDecline() {
    // Combine commands: first switch calendar, then create recurring event, then exit.
    String input = "use calendar --name MyCalendar\n"
                   + "create event --autoDecline Standup from 2025-03-06T09:00 to "
                   + "2025-03-06T09:30 repeats MTWRF for 5 times\n" + "exit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();
    assertEquals(2, allCalls.size());

    List<String> expectedUseCalendarCall = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedRecurringCall = List.of("createRecurringEvent called",
                                                 "[MyCalendar, Standup, 2025-03-06T09:00, "
                                                 + "2025-03-06T09:30, [M, T, W, R, F], 5]");

    assertEquals(expectedUseCalendarCall, allCalls.get(0));
    assertEquals(expectedRecurringCall, allCalls.get(1));

    String expectedMessage = "Recurring event 'Standup' created for 5 times on [M, T, W, R, F].";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }

  @Test
  public void testCreateRecurringEventWithOccurrencesWithoutAutoDecline() {
    // Combine commands: first switch calendar, then create recurring event, then exit.
    String input = "use calendar --name MyCalendar\n"
                   + "create event Standup from 2025-03-06T09:00 to 2025-03-06T09:30 repeats "
                   + "MTWRF for 5 times\n" + "exit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();
    assertEquals(2, allCalls.size());

    List<String> expectedUseCalendarCall = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedRecurringCall = List.of("createRecurringEvent called",
                                                 "[MyCalendar, Standup, 2025-03-06T09:00, "
                                                 + "2025-03-06T09:30, [M, T, W, R, F], 5]");

    assertEquals(expectedUseCalendarCall, allCalls.get(0));
    assertEquals(expectedRecurringCall, allCalls.get(1));

    String expectedMessage = "Recurring event 'Standup' created for 5 times on [M, T, W, R, F].";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }

  @Test
  public void testCreateRecurringEventWithEndDateAndAutoDecline() {
    // Combine commands: first switch calendar, then create recurring event, then exit.
    String input = "use calendar --name MyCalendar\n"
                   + "create event --autoDecline Yoga from 2025-03-07T08:00 to 2025-03-07T09:00 "
                   + "repeats MRU until 2025-06-01T00:00\n" + "exit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    // Retrieve all calls from the fake calendar manager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls: one for the use calendar command and one for the recurring event
    // command.
    assertEquals(2, allCalls.size());

    List<String> expectedUseCalendarCall = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedRecurringCall = List.of("createRecurringEvent called",
                                                 "[MyCalendar, Yoga, 2025-03-07T08:00, "
                                                 + "2025-03-07T09:00, [M, R, U], "
                                                 + "2025-06-01T00:00]");

    assertEquals(expectedUseCalendarCall, allCalls.get(0));
    assertEquals(expectedRecurringCall, allCalls.get(1));

    String expectedMessage = "Recurring event 'Yoga' created until 2025-06-01 on [M, R, U].";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }

  @Test
  public void testCreateRecurringEventWithEndDateWithoutAutoDecline() {
    // Combine commands: first switch calendar, then create recurring event, then exit.
    String input = "use calendar --name MyCalendar\n"
                   + "create event Yoga from 2025-03-07T08:00 to 2025-03-07T09:00 repeats MRU "
                   + "until 2025-06-01T00:00\n" + "exit\n";

    // Create the controller with the fake calendar manager and view.
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    // Retrieve all calls from the fake calendar manager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls: one for the use calendar command and one for the recurring event
    // command.
    assertEquals(2, allCalls.size());

    List<String> expectedUseCalendarCall = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedRecurringCall = List.of("createRecurringEvent called",
                                                 "[MyCalendar, Yoga, 2025-03-07T08:00, "
                                                 + "2025-03-07T09:00, [M, R, U], "
                                                 + "2025-06-01T00:00]");

    assertEquals(expectedUseCalendarCall, allCalls.get(0));
    assertEquals(expectedRecurringCall, allCalls.get(1));

    String expectedMessage = "Recurring event 'Yoga' created until 2025-06-01 on [M, R, U].";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }


  @Test
  public void testEditSingleEventCommand() {
    // Combine commands: first set calendar context, then create event, then edit event, then exit.
    String input = "use calendar --name MyCalendar\n"
                   + "create event Meeting from 2025-03-05T10:00 to 2025-03-05T11:00\n"
                   + "edit event location Meeting from 2025-03-05T10:00 to 2025-03-05T11:00 with "
                   + "Room101\n" + "exit\n";

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected if the input stream is exhausted.
    }
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    List<String> expectedCall1 = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCall2 = List.of("createSingleEvent called",
                                         "[MyCalendar, Meeting, 2025-03-05T10:00, "
                                         + "2025-03-05T11:00]");
    List<String> expectedCall3 = List.of("editSingleEvent called",
                                         "[MyCalendar, Meeting, 2025-03-05T10:00, "
                                         + "2025-03-05T11:00, location, Room101]");

    assertEquals(3, allCalls.size());
    assertEquals(expectedCall1, allCalls.get(0));
    assertEquals(expectedCall2, allCalls.get(1));
    assertEquals(expectedCall3, allCalls.get(2));

    String expectedMessage = "Event 'Meeting' updated successfully: location changed to Room101.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }

  @Test
  public void testEditMultipleEventsFromDateCommand() {
    // Combine commands: first set calendar context, then edit events, then exit.
    String input = "use calendar --name MyCalendar\n"
                   + "edit events time Conference from 2025-03-05T10:00 with 11:00AM\n" + "exit\n";

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    // Retrieve all calls from the fakeCalendarManager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    List<String> expectedCall1 = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCall2 = List.of("editMultipleEventsFrom called",
                                         "[MyCalendar, Conference, 2025-03-05T10:00, time, "
                                         + "11:00AM]");

    assertEquals(2, allCalls.size());
    assertEquals(expectedCall1, allCalls.get(0));
    assertEquals(expectedCall2, allCalls.get(1));

    String expectedMessage =
        "All events named 'Conference' starting from 2025-03-05T10:00 updated successfully: time "
        + "changed to 11:00AM.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }


  @Test
  public void testEditMultipleEventsCommand() {
    // Combine commands: first set the calendar context, then edit events, then exit.
    String input =
        "use calendar --name MyCalendar\n" + "edit events location Conference Room101\n" + "exit\n";

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    // Retrieve all calls from the fake calendar manager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // We expect two calls:
    // 1. "useCalendar" sets the context to MyCalendar.
    // 2. "editMultipleEvents" edits events for "Conference".
    List<String> expectedCall1 = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCall2 =
        List.of("editMultipleEvents called", "[MyCalendar, Conference, location, Room101]");

    assertEquals(2, allCalls.size());
    assertEquals(expectedCall1, allCalls.get(0));
    assertEquals(expectedCall2, allCalls.get(1));

    String expectedMessage =
        "All events named 'Conference' updated successfully: location changed to Room101.";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }


  @Test
  public void testPrintEventsOnDateWithoutEvents() {
    // Combine commands: set calendar context, then print events on date, then exit.
    String input = "use calendar --name MyCalendar\n" + "print events on 2025-03-08\n" + "exit\n";

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    // Retrieve all calls from the fakeCalendarManager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls:
    // 1. "useCalendar" command sets the context to MyCalendar.
    // 2. "getEventsOnDate" command is called with the appropriate date.
    List<String> expectedCall1 = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCall2 = List.of("getEventsOnDate called", "[MyCalendar, 2025-03-08]");

    assertEquals(2, allCalls.size());
    assertEquals(expectedCall1, allCalls.get(0));
    assertEquals(expectedCall2, allCalls.get(1));

    // Verify that the view displays the expected message.
    String expectedMessage = "No events on 2025-03-08";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }


  @Test
  public void testPrintEventsInRangeWithoutEvents() {
    // Combine commands: set calendar context, then print events in range, then exit.
    String input = "use calendar --name MyCalendar\n"
                   + "print events from 2025-03-08T00:00 to 2025-03-10T23:59\n" + "exit\n";
    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    // Retrieve all calls from the fakeCalendarManager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls:
    // 1. "useCalendar" command sets the context to MyCalendar.
    // 2. "getEventsInRange" command is called with the specified start and end times.
    List<String> expectedCall1 = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCall2 =
        List.of("getEventsInRange called", "[MyCalendar, 2025-03-08T00:00, 2025-03-10T23:59]");

    assertEquals(2, allCalls.size());
    assertEquals(expectedCall1, allCalls.get(0));
    assertEquals(expectedCall2, allCalls.get(1));

    // Verify that the view displays the expected message.
    String expectedMessage = "No events from 2025-03-08T00:00 to 2025-03-10T23:59";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }

  @Test
  public void testExportCalendar() {
    // Combine commands: first set calendar context, then exportCalendar calendar, then exit.
    String input =
        "use calendar --name MyCalendar\n" + "exportCalendar cal calendar.csv\n" + "exit\n";

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    List<String> expectedCall1 = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCall2 = List.of("exportCalendar called", "[MyCalendar]");

    assertEquals(2, allCalls.size());
    assertEquals(expectedCall1, allCalls.get(0));
    assertEquals(expectedCall2, allCalls.get(1));
  }


  @Test
  public void testShowStatusBusy() {
    fakeCalendarManager.setIsBusy(true);

    // Combine commands: first set calendar context, then show status, then exit.
    String input =
        "use calendar --name MyCalendar\n" + "show status on 2025-03-09T14:00\n" + "exit\n";

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    // Retrieve all calls from the fakeCalendarManager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls:
    // 1. "useCalendar" sets the context to MyCalendar.
    // 2. "isBusy" is called with the specified time.
    List<String> expectedCall1 = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCall2 = List.of("isBusy called", "[MyCalendar, 2025-03-09T14:00]");

    assertEquals(2, allCalls.size());
    assertEquals(expectedCall1, allCalls.get(0));
    assertEquals(expectedCall2, allCalls.get(1));

    String expectedMessage = "User is busy at 2025-03-09T14:00";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }


  @Test
  public void testShowStatusNotBusy() {
    fakeCalendarManager.setIsBusy(false);

    // Combine commands: first set calendar context, then show status, then exit.
    String input =
        "use calendar --name MyCalendar\n" + "show status on 2025-03-09T14:00\n" + "exit\n";

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    try {
      controller.run();
    } catch (Exception e) {
      // Expected due to input exhaustion.
    }

    // Retrieve all calls from the fakeCalendarManager.
    List<List<String>> allCalls = fakeCalendarManager.getAllCalls();

    // Expecting two calls:
    // 1. "useCalendar" sets the context to MyCalendar.
    // 2. "isBusy" is called with the specified time.
    List<String> expectedCall1 = List.of("detectCalendar called", "[MyCalendar]");
    List<String> expectedCall2 = List.of("isBusy called", "[MyCalendar, 2025-03-09T14:00]");

    assertEquals(2, allCalls.size());
    assertEquals(expectedCall1, allCalls.get(0));
    assertEquals(expectedCall2, allCalls.get(1));

    String expectedMessage = "User is available at 2025-03-09T14:00";
    assertTrue(fakeView.getMessages().contains(expectedMessage));
  }

  @Test
  public void testUserTypesInvalidCommandThenExits() {
    String input = "invalidCommand\nexit\n";

    controller = new InteractiveTextController(fakeCalendarManager, fakeView, new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())));
    controller.run();

    // Expected error output
    String expectedErrorOutput = "Invalid command format, command is " + "shorter than 3 words.";
    assertTrue(fakeView.getErrors().contains(expectedErrorOutput));
  }
}