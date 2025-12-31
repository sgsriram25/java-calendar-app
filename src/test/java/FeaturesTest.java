import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import controller.Features;
import controller.GUIController;
import controller.commands.ImportCalendarCommand;
import fakes.FakeCalendarManager;
import fakes.FakeView;
import model.CalendarManager;
import model.ICalendarManager;
import model.ReadOnlyEvent;

/**
 * Test class for the Features class.
 */
public class FeaturesTest {

  private FakeCalendarManager mockManager;
  private FakeView mockView;
  private Features features;

  @Before
  public void setUp() {
    mockManager = new FakeCalendarManager();
    mockView = new FakeView();
    GUIController controller = new GUIController(mockManager, mockView);
    features = new Features(controller);
  }

  @Test
  public void testCreateCalendar() {
    String name = "MyCalendar";
    ZoneId zone = ZoneId.of("America/New_York");
    features.createCalendar(name, zone);

    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("createCalendar called", "[MyCalendar, America/New_York]");
    assertEquals(1, allCalls.size());
    assertEquals(expectedCall, allCalls.get(0));

    String expectedMessage =
        "Calendar 'MyCalendar' created successfully. Timezone: America/New_York";
    assertTrue(mockView.getMessages().contains(expectedMessage));
  }

  @Test
  public void testEditCalendar() {
    features.editCalendar("Work", "name", "WorkUpdated");
    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("editCalendar called", "[Work, name, WorkUpdated]");
    assertEquals(1, allCalls.size());
    assertEquals(expectedCall, allCalls.get(0));
    assertTrue(mockView.getMessages().contains(
        "Calendar 'Work' updated successfully: name changed to WorkUpdated."));
  }

  @Test
  public void testDetectCalendarSuccess() {
    assertTrue(features.detectCalendar("Work"));
    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("detectCalendar called", "[Work]");
    assertEquals(1, allCalls.size());
    assertEquals(expectedCall, allCalls.get(0));
    assertTrue(mockView.getErrors().isEmpty());
  }

  @Test
  public void testUseCalendar() {
    features.useCalendar("Work");
    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("detectCalendar called", "[Work]");
    assertEquals(1, allCalls.size());
    assertEquals(expectedCall, allCalls.get(0));
    assertTrue(mockView.getMessages().contains("Switched to calendar 'Work'."));
  }

  @Test
  public void testOnlyGetEventsByName() {
    LocalDate date = LocalDate.of(2024, 3, 17);
    features.onlyGetEvents("Work", date);
    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("getEventsOnDate called", "[Work, 2024-03-17]");
    assertEquals(1, allCalls.size());
    assertEquals(expectedCall, allCalls.get(0));
  }

  @Test
  public void testOnlyGetEventsByDate() {
    features.useCalendar("Work");
    LocalDate date = LocalDate.of(2024, 3, 17);
    features.onlyGetEvents(date);
    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("getEventsOnDate called", "[Work, 2024-03-17]");
    assertEquals(expectedCall, allCalls.get(1));
  }


  @Test
  public void testShowEvents() {
    features.useCalendar("Work");
    LocalDate date = LocalDate.of(2024, 3, 17);
    features.showEvents(date);
    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("getEventsOnDate called", "[Work, 2024-03-17]");
    assertEquals(expectedCall, allCalls.get(1));
    assertTrue(mockView.getMessages().stream().anyMatch(
        msg -> msg.contains("No events on " + "2024-03-17")));
  }

  @Test
  public void testAddSingleEvent() {
    features.useCalendar("TestCalendar");
    String name = "Meeting";
    LocalDateTime start = LocalDateTime.of(2024, 3, 17, 10, 0);
    LocalDateTime end = start.plusHours(1);
    features.addSingleEvent(name, start, end);
    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("createSingleEvent called",
        "[TestCalendar, Meeting, 2024-03-17T10:00, " + "2024-03-17T11:00]");
    assertEquals(2, allCalls.size());
    assertEquals(expectedCall, allCalls.get(1));
    assertTrue(mockView.getMessages().stream().anyMatch(
        msg -> msg.contains("Single event 'Meeting' created successfully")));
  }

  @Test
  public void testAddRecurringEventEndDate() {
    features.useCalendar("TestCalendar");
    LocalDateTime start = LocalDateTime.of(2024, 3, 17, 9, 0);
    LocalDateTime end = start.plusHours(1);
    LocalDateTime until = start.plusWeeks(4);
    List<Character> days = List.of('M', 'W');
    features.addRecurringEventEndDate("Yoga", start, end, days, until);

    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("createRecurringEvent called",
        "[TestCalendar, Yoga, 2024-03-17T09:00, 2024-03-17T10:00," + " [M, W], 2024-04-14T09:00]");
    assertEquals(2, allCalls.size());
    assertEquals(expectedCall, allCalls.get(1));
    assertTrue(mockView.getMessages().stream().anyMatch(
        msg -> msg.contains("Recurring event 'Yoga' created until")));
  }

  @Test
  public void testAddRecurringEventOccurrences() {
    features.useCalendar("TestCalendar");
    LocalDateTime start = LocalDateTime.of(2024, 3, 17, 9, 0);
    LocalDateTime end = start.plusHours(1);
    List<Character> days = List.of('T', 'R');
    features.addRecurringEventOccurrences("Class", start, end, days, 10);

    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("createRecurringEvent called",
        "[TestCalendar, Class, 2024-03-17T09:00, " + "2024-03-17T10:00, [T, R], 10]");
    assertEquals(2, allCalls.size());
    assertEquals(expectedCall, allCalls.get(1));
    assertTrue(mockView.getMessages().stream().anyMatch(
        msg -> msg.contains("Recurring event 'Class' created for 10 times")));
  }

  @Test
  public void testEditSingleEvent() {
    features.useCalendar("TestCalendar");
    LocalDateTime start = LocalDateTime.of(2024, 3, 17, 10, 0);
    LocalDateTime end = start.plusHours(1);
    features.editSingleEvent("Meeting", start, end, "location", "Room A");

    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("editSingleEvent called",
        "[TestCalendar, Meeting, 2024-03-17T10:00, " + "2024-03-17T11:00, location, Room A]");
    assertEquals(2, allCalls.size());
    assertEquals(expectedCall, allCalls.get(1));
    assertTrue(mockView.getMessages().stream().anyMatch(
        msg -> msg.contains("updated successfully: location changed to Room A")));
  }

  @Test
  public void testEditMultipleEventsFrom() {
    features.useCalendar("TestCalendar");
    LocalDateTime start = LocalDateTime.of(2024, 3, 17, 10, 0);
    features.editMultipleEventsFrom("Class", start, "location", "Room B");

    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("editMultipleEventsFrom called",
        "[TestCalendar, Class, 2024-03-17T10:00, location, Room " + "B]");
    assertEquals(2, allCalls.size());
    assertEquals(expectedCall, allCalls.get(1));
    assertTrue(mockView.getMessages().stream().anyMatch(
        msg -> msg.contains("updated successfully: location changed to Room B")));
  }

  @Test
  public void testEditAllEvents() {
    features.useCalendar("TestCalendar");
    features.editAllEvents("Class", "description", "Updated description");

    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("editMultipleEvents called",
        "[TestCalendar, Class, description, Updated description]");
    assertEquals(2, allCalls.size());
    assertEquals(expectedCall, allCalls.get(1));
    assertTrue(mockView.getMessages().stream().anyMatch(
        msg -> msg.contains("updated successfully: description changed to Updated description")));
  }

  @Test
  public void testExportCalendar() {
    features.useCalendar("Work");
    features.exportCalendar("calendar.csv");

    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall = List.of("exportCalendar called", "[Work]");
    assertEquals(2, allCalls.size());
    assertEquals(expectedCall, allCalls.get(1));
    assertTrue(mockView.getErrors().get(0).contains(
        "Error exporting calendar: Failed to exportCalendar " + "calendar."));
  }

  @Test
  public void testImportCalendarWithTempFile() throws Exception {
    // Set the current calendar to import into
    features.useCalendar("Work");

    // Create a temporary CSV file
    Path tempFile = Files.createTempFile("calendar", ".csv");
    Files.write(tempFile, List.of(
        "Subject,Start Date,Start Time,End Date,End Time,All Day Event,Description,Location,"
        + "Private", "Event1,2020-01-01,12:00,2020-01-01,13:00,false,,,false"));

    // Import the calendar from the temp file
    features.importCalendar(tempFile.toString());

    List<List<String>> allCalls = mockManager.getAllCalls();
    List<String> expectedCall =
        List.of("createSingleEvent called", "[Work, Event1, 2020-01-01T12:00, 2020-01-01T13:00]");
    List<String> expectedCallSetPublic = List.of("editSingleEvent called",
        "[Work, Event1, 2020-01-01T12:00, 2020-01-01T13:00, public, true]");
    assertEquals(5, allCalls.size());
    assertEquals(expectedCall, allCalls.get(1));
    assertEquals(expectedCallSetPublic, allCalls.get(4));

    // Verify success message from the view
    assertTrue(mockView.getMessages().get(1).contains("Calendar imported successfully "));

    // Delete the file after the test
    Files.deleteIfExists(tempFile);

    features.showEvents(LocalDate.of(2020, 1, 1));
    List<List<String>> calls = mockManager.getAllCalls();
    List<String> expected = List.of("getEventsOnDate called", "[Work, 2020-01-01]");
    assertEquals(expected, calls.get(5));
  }

  @Test
  public void testCreateCalendarThrowsException() {
    mockManager.setThrowExceptionNext();
    features.createCalendar("ErrCalendar", ZoneId.systemDefault());
    assertTrue(mockView.getErrors().contains("Error creating calendar: Fake exception"));
  }

  @Test
  public void testEditCalendarThrowsException() {
    mockManager.setThrowExceptionNext();
    features.editCalendar("ErrCalendar", "name", "NewName");
    assertTrue(mockView.getErrors().contains("Error editing calendar: Fake exception"));
  }

  @Test
  public void testDetectCalendarThrowsException() {
    mockManager.setThrowExceptionNext();
    assertFalse(features.detectCalendar("Nonexistent"));
    assertTrue(mockView.getErrors().contains("Error finding calendar: Fake exception"));
  }

  @Test
  public void testUseCalendarThrowsException() {
    mockManager.setThrowExceptionNext();
    features.useCalendar("ErrCalendar");
    assertTrue(mockView.getErrors().contains("Error using calendar: Fake exception"));
  }

  @Test
  public void testAddSingleEventThrowsException() {
    mockManager.setThrowExceptionNext();
    features.addSingleEvent("Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
    assertTrue(mockView.getErrors().contains("Error adding single event: Fake exception"));
  }

  @Test
  public void testAddRecurringEventUntilThrowsException() {
    mockManager.setThrowExceptionNext();
    features.addRecurringEventEndDate("Event", LocalDateTime.now(),
        LocalDateTime.now().plusHours(1), List.of('M'), LocalDateTime.now().plusWeeks(1));
    assertTrue(mockView.getErrors().contains("Error adding recurring eventFake exception"));
  }

  @Test
  public void testAddRecurringEventOccurrencesThrowsException() {
    mockManager.setThrowExceptionNext();
    features.addRecurringEventOccurrences("Event", LocalDateTime.now(),
        LocalDateTime.now().plusHours(1), List.of('M'), 5);
    assertTrue(mockView.getErrors().contains("Error adding recurring event: Fake exception"));
  }

  @Test
  public void testEditSingleEventThrowsException() {
    mockManager.setThrowExceptionNext();
    features.editSingleEvent("Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
        "location", "Room A");
    assertTrue(mockView.getErrors().contains("Error editing single event: Fake exception"));
  }

  @Test
  public void testEditMultipleEventsFromThrowsException() {
    mockManager.setThrowExceptionNext();
    features.editMultipleEventsFrom("Event", LocalDateTime.now(), "location", "Room B");
    assertTrue(mockView.getErrors().contains("Error editing multiple events: Fake exception"));
  }

  @Test
  public void testEditAllEventsThrowsException() {
    mockManager.setThrowExceptionNext();
    features.editAllEvents("Event", "description", "desc");
    assertTrue(mockView.getErrors().contains("Error editing all events: Fake exception"));
  }

  @Test
  public void testExportCalendarThrowsException() {
    mockManager.setThrowExceptionNext();
    features.exportCalendar("calendar.csv");
    assertTrue(mockView.getErrors().contains("Error exporting calendar: Fake exception"));
  }

  @Test
  public void testImportCalendarThrowsException() throws IOException {
    mockManager.setThrowExceptionNext();
    Path tempFile = Files.createTempFile("calendar", ".csv");
    Files.write(tempFile, List.of(
        "Subject,Start Date,Start Time,End Date,End Time,All Day Event,Description,Location,"
        + "Private", "Event1,2020-01-01,12:00,2020-01-01,13:00,false,,,false"));

    // Import the calendar from the temp file
    features.importCalendar(tempFile.toString());
    // Delete the file after the test
    Files.deleteIfExists(tempFile);
    assertTrue(mockView.getErrors().contains("Error importing calendar: Fake exception"));
  }

  @Test
  public void testOnlyGetEventsByNameThrowsException() {
    mockManager.setThrowExceptionNext();
    features.onlyGetEvents("Work", LocalDate.of(2024, 3, 17));
    assertTrue(mockView.getErrors().contains("Error getting events: Fake exception"));
  }

  @Test
  public void testOnlyGetEventsByDateThrowsException() {
    mockManager.setThrowExceptionNext();
    features.onlyGetEvents(LocalDate.of(2024, 3, 17));
    assertTrue(mockView.getErrors().contains("Error getting events: Fake exception"));
  }

  @Test
  public void testShowEventsThrowsException() {
    mockManager.setThrowExceptionNext();
    features.showEvents(LocalDate.of(2024, 3, 17));
    assertTrue(mockView.getErrors().contains("Error showing events: Fake exception"));
  }

  @Test
  public void testOnlyGetEventsByNameWithActualEvent() throws Exception {
    // Use real manager for real events
    ICalendarManager realManager = new CalendarManager();
    GUIController controller = new GUIController(realManager, mockView);
    Features features = new Features(controller);

    // Create calendar and add event
    features.createCalendar("TestCal", ZoneId.of("UTC"));
    features.useCalendar("TestCal");
    LocalDateTime start = LocalDateTime.of(2024, 4, 1, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 4, 1, 11, 0);
    features.addSingleEvent("TestEvent", start, end);

    // Test onlyGetEvents with actual event
    Optional<List<ReadOnlyEvent>> result =
        features.onlyGetEvents("TestCal", LocalDate.of(2024, 4, 1));
    assertTrue(result.isPresent());
    assertEquals(1, result.get().size());
    assertEquals("TestEvent", result.get().get(0).getName());
  }

  @Test
  public void testOnlyGetEventsByDateWithActualEvent() throws Exception {
    ICalendarManager realManager = new CalendarManager();
    GUIController controller = new GUIController(realManager, mockView);
    Features features = new Features(controller);

    // Create calendar and add event
    features.createCalendar("Default", ZoneId.of("UTC"));
    features.useCalendar("Default");
    LocalDateTime start = LocalDateTime.of(2024, 4, 2, 9, 0);
    LocalDateTime end = start.plusHours(1);
    features.addSingleEvent("Morning Meeting", start, end);

    // Test current calendar event retrieval
    Optional<List<ReadOnlyEvent>> result = features.onlyGetEvents(LocalDate.of(2024, 4, 2));
    assertTrue(result.isPresent());
    assertEquals(1, result.get().size());
    assertEquals("Morning Meeting", result.get().get(0).getName());
  }

  @Test
  public void testImportCalendarCommandReturnsCurrentCalendar() throws Exception {
    // Arrange
    String curCalendar = "Work";
    Path tempFile = Files.createTempFile("calendar", ".csv");
    Files.write(tempFile, List.of(
        "Subject,Start Date,Start Time,End Date,End Time,All Day Event,Description,Location,"
        + "Private",
        "Lunch,2024-04-05,12:00,2024-04-05,13:00,false,Lunch with team,Cafe,false"));

    ImportCalendarCommand cmd = new ImportCalendarCommand(tempFile.toString());
    FakeCalendarManager manager = new FakeCalendarManager();
    FakeView view = new FakeView();

    String result = cmd.execute(manager, view, curCalendar);
    assertEquals("Work", result);
    assertTrue(view.getMessages().stream().anyMatch(m -> m.contains("imported")));
    Files.deleteIfExists(tempFile);
  }

  @Test
  public void testImportCalendarRequiresValidCalendar() {
    ImportCalendarCommand cmd = new ImportCalendarCommand("dummy.csv");
    assertTrue(cmd.requiresValidCurCalendar());
  }


}
