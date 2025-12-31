import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import view.CalendarCLIView;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the CalendarCLIView class.
 */
public class ViewTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private CalendarCLIView view;

  @Before
  public void setUp() {
    System.setOut(new PrintStream(outContent));
    view = new CalendarCLIView();
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
    outContent.reset();
  }

  @Test
  public void testDisplayMessage() {
    view.displayMessage("Hello World");
    String expected = "Hello World";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }

  @Test
  public void testDisplayError() {
    view.displayError("Error occurred");
    String expected = "Error: Error occurred";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }

  @Test
  public void testCreateCalendar() {
    ZoneId zone = ZoneId.of("America/New_York");
    view.createCalendar("TestCal", zone);
    String expected = "Calendar 'TestCal' created successfully. Timezone: " + zone.getId();
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }

  @Test
  public void testEditCalendar() {
    view.editCalendar("TestCal", "timezone", "America/Chicago");
    String expected =
        "Calendar 'TestCal' updated successfully: timezone changed to America/Chicago.";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }

  @Test
  public void testUseCalendar() {
    view.useCalendar("TestCal");
    String expected = "Using calendar 'TestCal'.";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testCopySingleEvent() {
    LocalDateTime source = LocalDateTime.of(2025, 4, 10, 10, 0);
    LocalDateTime target = LocalDateTime.of(2025, 4, 10, 10, 0);
    view.copySingleEvent("SourceCal", "Meeting", source, "TargetCal", target);
    String expected =
        "Event 'Meeting' copied from " + source.toString() + " in calendar 'SourceCal' to "
        + target.toString() + " in calendar 'TargetCal'.";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testCopyEventsOnDate() {
    LocalDate date = LocalDate.of(2025, 4, 15);
    view.copyEventsOnDate("SourceCal", date, "TargetCal", date);
    String expected =
        "Events on " + date.toString() + " copied from calendar 'SourceCal' to " + date.toString()
        + " in calendar 'TargetCal'.";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testCopyEventsBetweenDates() {
    LocalDate sourceStart = LocalDate.of(2025, 5, 1);
    LocalDate sourceEnd = LocalDate.of(2025, 5, 3);
    LocalDate targetStart = LocalDate.of(2025, 6, 1);
    view.copyEventsBetweenDates("SourceCal", sourceStart, sourceEnd, "TargetCal", targetStart);
    String expected = "Events from " + sourceStart.toString() + " to " + sourceEnd.toString()
                      + " copied from calendar 'SourceCal' to 'TargetCal' starting on "
                      + targetStart.toString() + ".";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testCreateSingleWithEnd() {
    LocalDateTime start = LocalDateTime.of(2025, 7, 10, 14, 0);
    LocalDateTime end = LocalDateTime.of(2025, 7, 10, 15, 0);
    view.createSingle("Workshop", start, end, false);
    String expected =
        "Single event 'Workshop' created successfully from " + start.toString() + " to "
        + end.toString() + ".";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testCreateSingleAllDay() {
    LocalDateTime start = LocalDateTime.of(2025, 7, 10, 0, 0);
    view.createSingle("Holiday", start, null, false);
    String expected =
        "Single all-day event 'Holiday' created successfully on " + start.toString() + ".";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testCreateRecurringOccurrences() {
    LocalDateTime start = LocalDateTime.of(2025, 8, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 8, 1, 10, 0);
    List<Character> daysOfWeek = Arrays.asList('M', 'W', 'F');
    view.createRecurringOccurrences("Yoga", start, end, daysOfWeek, 3);
    String expected =
        "Recurring event 'Yoga' created for 3 times on " + daysOfWeek.toString() + ".";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }

  @Test
  public void testCreateRecurringUntil() {
    LocalDateTime start = LocalDateTime.of(2025, 9, 1, 8, 0);
    LocalDateTime end = LocalDateTime.of(2025, 9, 1, 9, 0);
    List<Character> daysOfWeek = Arrays.asList('T', 'R');
    LocalDateTime until = LocalDateTime.of(2025, 9, 15, 23, 59);
    view.createRecurringUntil("Standup", start, end, daysOfWeek, until);
    String expected =
        "Recurring event 'Standup' created until " + until.toLocalDate().toString() + " on "
        + daysOfWeek.toString() + ".";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testEditSingle() {
    LocalDateTime start = LocalDateTime.of(2025, 10, 5, 12, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 5, 13, 0);
    view.editSingle("Lunch", start, end, "name", "Team Lunch");
    String expected = "Event 'Lunch' updated successfully: name changed to Team Lunch.";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }

  @Test
  public void testEditMultiple() {
    LocalDateTime start = LocalDateTime.of(2025, 11, 1, 9, 0);
    view.editMultiple("Scrum", start, "name", "Daily Scrum");
    String expected = "All events named 'Scrum' after " + start.toString()
                      + " updated successfully: name changed to Daily Scrum.";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }

  @Test
  public void testEditAll() {
    view.editAll("Conference", "name", "Annual Conference");
    String expected = "All events named 'Conference' updated successfully: name changed to "
                      + "Annual Conference.";
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testPrintEventsOnDateEmpty() {
    LocalDate date = LocalDate.of(2025, 12, 25);
    view.printEventsOnDate(date, Optional.empty());
    String expected = "No events on " + date.toString();
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testPrintEventsInRangeEmpty() {
    LocalDateTime start = LocalDateTime.of(2025, 12, 31, 0, 0);
    LocalDateTime end = LocalDateTime.of(2026, 1, 1, 0, 0);
    view.printEventsInRange(start, end, Optional.empty());
    String expected = "No events from " + start.toString() + " to " + end.toString();
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }


  @Test
  public void testExport() {
    String filepath = "calendar_export.txt";
    view.exportCalendar(filepath);
    String expected = "Calendar exported successfully to " + filepath;
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }

  @Test
  public void testShowStatusBusy() {
    LocalDateTime dateTime = LocalDateTime.of(2026, 2, 14, 15, 0);
    view.showStatus(dateTime, true);
    String expected = "User is busy at " + dateTime.toString();
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }

  @Test
  public void testShowStatusAvailable() {
    LocalDateTime dateTime = LocalDateTime.of(2026, 2, 14, 15, 0);
    view.showStatus(dateTime, false);
    String expected = "User is available at " + dateTime.toString();
    assertEquals(expected, outContent.toString().trim());
    outContent.reset();
  }
}
