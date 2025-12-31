import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import model.ICalendarManager;
import model.CalendarManager;
import model.ReadOnlyEvent;
import model.exceptions.CalendarException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the CalendarManager class.
 */
public class ModelTest {

  private ICalendarManager calendarManager;

  @Before
  public void setUp() throws CalendarException {
    calendarManager = new CalendarManager();
  }

  @Test
  public void testCopySingleEvent() throws Exception {
    calendarManager.createCalendar("SourceCal", ZoneId.of("America/New_York"));
    calendarManager.createCalendar("TargetCal", ZoneId.of("America/Chicago"));
    LocalDateTime eventStart = LocalDateTime.of(2025, 4, 10, 10, 0);
    LocalDateTime eventEnd = LocalDateTime.of(2025, 4, 10, 11, 0);
    calendarManager.createSingleEvent("SourceCal", "Meeting", eventStart, eventEnd);
    calendarManager.copySingleEvent("SourceCal", "Meeting", eventStart, "TargetCal", eventStart);
    Optional<List<ReadOnlyEvent>> eventsOpt =
        calendarManager.getEventsOnDate("TargetCal", eventStart.toLocalDate());
    assertTrue(eventsOpt.isPresent());
    boolean found = eventsOpt.get().stream().anyMatch(e -> "Meeting".equals(e.getName()));
    assertTrue(found);
  }

  @Test
  public void testCopyEventsOnDate() throws Exception {
    calendarManager.createCalendar("SourceCal", ZoneId.of("America/New_York"));
    calendarManager.createCalendar("TargetCal", ZoneId.of("America/Chicago"));
    LocalDate date = LocalDate.of(2025, 4, 15);
    LocalDateTime start1 = date.atTime(9, 0);
    LocalDateTime end1 = date.atTime(10, 0);
    calendarManager.createSingleEvent("SourceCal", "Event1", start1, end1);
    // Create a second event on the same date.
    LocalDateTime start2 = date.atTime(11, 0);
    LocalDateTime end2 = date.atTime(12, 0);
    calendarManager.createSingleEvent("SourceCal", "Event2", start2, end2);
    // Copy events on the specified date.
    calendarManager.copyEventsOnDate("SourceCal", date, "TargetCal", date);
    Optional<List<ReadOnlyEvent>> eventsOpt = calendarManager.getEventsOnDate("TargetCal", date);
    assertTrue(eventsOpt.isPresent());
    // Check that both events were copied.
    List<ReadOnlyEvent> events = eventsOpt.get();
    assertTrue(events.size() >= 2);
  }

  @Test
  public void testCopyEventsBetweenDates() throws Exception {
    calendarManager.createCalendar("SourceCal", ZoneId.of("America/New_York"));
    calendarManager.createCalendar("TargetCal", ZoneId.of("America/Chicago"));
    LocalDate startDate = LocalDate.of(2025, 5, 1);
    LocalDate endDate = LocalDate.of(2025, 5, 3);
    // Create one event per day in the source calendar.
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      LocalDateTime start = date.atTime(10, 0);
      LocalDateTime end = date.atTime(11, 0);
      calendarManager.createSingleEvent("SourceCal", "DailyMeeting", start, end);
    }
    LocalDate targetStartDate = LocalDate.of(2025, 6, 1);
    calendarManager.copyEventsBetweenDates("SourceCal", startDate, endDate, "TargetCal",
                                           targetStartDate);
    for (int i = 0; i <= endDate.toEpochDay() - startDate.toEpochDay(); i++) {
      LocalDate targetDate = targetStartDate.plusDays(i);
      Optional<List<ReadOnlyEvent>> eventsOpt =
          calendarManager.getEventsOnDate("TargetCal", targetDate);
      assertTrue(eventsOpt.isPresent());
      boolean found = eventsOpt.get().stream().anyMatch(e -> "DailyMeeting".equals(e.getName()));
      assertTrue(found);
    }
  }

  @Test
  public void testCreateSingleEvent() throws Exception {
    calendarManager.createCalendar("EventCal", ZoneId.of("America/New_York"));
    LocalDateTime start = LocalDateTime.of(2025, 7, 10, 14, 0);
    LocalDateTime end = LocalDateTime.of(2025, 7, 10, 15, 0);
    calendarManager.createSingleEvent("EventCal", "Workshop", start, end);
    Optional<List<ReadOnlyEvent>> eventsOpt =
        calendarManager.getEventsOnDate("EventCal", start.toLocalDate());
    assertTrue(eventsOpt.isPresent());
    boolean found = eventsOpt.get().stream().anyMatch(e -> "Workshop".equals(e.getName()));
    assertTrue(found);
  }

  @Test
  public void testCreateRecurringEventWithRepeats() throws Exception {
    calendarManager.createCalendar("RecurringCal", ZoneId.of("America/New_York"));
    LocalDateTime start = LocalDateTime.of(2025, 8, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 8, 1, 10, 0);
    List<Character> daysOfWeek = Arrays.asList('M', 'W', 'F');
    int numRepeats = 3;
    calendarManager.createRecurringEvent("RecurringCal", "Yoga", start, end, daysOfWeek,
                                         numRepeats);
    // Use a wide range to cover expected occurrences.
    LocalDateTime rangeStart = start.minusDays(1);
    LocalDateTime rangeEnd = start.plusDays(10);
    Optional<List<ReadOnlyEvent>> eventsOpt =
        calendarManager.getEventsInRange("RecurringCal", rangeStart, rangeEnd);
    assertTrue(eventsOpt.isPresent());
    long count = eventsOpt.get().stream().filter(e -> "Yoga".equals(e.getName())).count();
    assertTrue(count == numRepeats);
  }

  @Test
  public void testCreateRecurringEventUntil() throws Exception {
    calendarManager.createCalendar("RecurringCal2", ZoneId.of("America/New_York"));
    LocalDateTime start = LocalDateTime.of(2025, 9, 1, 8, 0);
    LocalDateTime end = LocalDateTime.of(2025, 9, 1, 9, 0);
    List<Character> daysOfWeek = Arrays.asList('T', 'R');
    LocalDateTime until = LocalDateTime.of(2025, 9, 15, 23, 59);
    calendarManager.createRecurringEvent("RecurringCal2", "Standup", start, end, daysOfWeek, until);
    LocalDateTime rangeStart = start.minusDays(1);
    LocalDateTime rangeEnd = until.plusDays(1);
    Optional<List<ReadOnlyEvent>> eventsOpt =
        calendarManager.getEventsInRange("RecurringCal2", rangeStart, rangeEnd);
    assertTrue(eventsOpt.isPresent());
    for (ReadOnlyEvent event : eventsOpt.get()) {
      if ("Standup".equals(event.getName())) {
        assertFalse(event.getStart().toLocalDate().isAfter(until.toLocalDate()));
      }
    }
  }

  @Test
  public void testEditSingleEvent() throws Exception {
    calendarManager.createCalendar("EditCal", ZoneId.of("America/New_York"));
    LocalDateTime start = LocalDateTime.of(2025, 10, 5, 12, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 5, 13, 0);
    calendarManager.createSingleEvent("EditCal", "Lunch", start, end);
    calendarManager.editSingleEvent("EditCal", "Lunch", start, end, "subject", "Team Lunch");
    Optional<List<ReadOnlyEvent>> eventsOpt =
        calendarManager.getEventsOnDate("EditCal", start.toLocalDate());
    assertTrue(eventsOpt.isPresent());
    boolean found = eventsOpt.get().stream().anyMatch(e -> "Team Lunch".equals(e.getName()));
    assertTrue(found);
  }

  @Test
  public void testEditMultipleEventsFrom() throws Exception {
    calendarManager.createCalendar("MultiEditCal", ZoneId.of("America/New_York"));
    LocalDateTime start1 = LocalDateTime.of(2025, 11, 1, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 11, 1, 10, 0);
    calendarManager.createSingleEvent("MultiEditCal", "Scrum", start1, end1);
    LocalDateTime start2 = LocalDateTime.of(2025, 11, 2, 9, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 11, 2, 10, 0);
    calendarManager.createSingleEvent("MultiEditCal", "Scrum", start2, end2);
    // Edit events starting from start2 onward.
    calendarManager.editMultipleEventsFrom("MultiEditCal", "Scrum", start2, "subject",
                                           "Daily " + "Scrum");
    Optional<List<ReadOnlyEvent>> eventsOpt =
        calendarManager.getEventsInRange("MultiEditCal", start1.minusDays(1), start2.plusDays(1));
    assertTrue(eventsOpt.isPresent());
    for (ReadOnlyEvent event : eventsOpt.get()) {
      if ("Scrum".equals(event.getName()) && !event.getStart().isBefore(start2)) {
        fail("Event was not updated.");
      }
    }
  }

  @Test
  public void testEditMultipleEvents() throws Exception {
    calendarManager.createCalendar("MultiEditCal2", ZoneId.of("America/New_York"));
    LocalDateTime start1 = LocalDateTime.of(2025, 12, 1, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 12, 1, 10, 0);
    calendarManager.createSingleEvent("MultiEditCal2", "Conference", start1, end1);
    LocalDateTime start2 = LocalDateTime.of(2025, 12, 2, 9, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 12, 2, 10, 0);
    calendarManager.createSingleEvent("MultiEditCal2", "Conference", start2, end2);
    calendarManager.editMultipleEvents("MultiEditCal2", "Conference", "subject",
                                       "Annual " + "Conference");
    Optional<List<ReadOnlyEvent>> eventsOpt =
        calendarManager.getEventsInRange("MultiEditCal2", start1.minusDays(1), start2.plusDays(1));
    assertTrue(eventsOpt.isPresent());
    for (ReadOnlyEvent event : eventsOpt.get()) {
      if ("Conference".equals(event.getName())) {
        fail("Event was not updated.");
      }
    }
    boolean found = eventsOpt.get().stream().anyMatch(e -> "Annual Conference".equals(e.getName()));
    assertTrue(found);
  }

  @Test
  public void testGetEventsOnDate() throws Exception {
    calendarManager.createCalendar("QueryCal", ZoneId.of("America/New_York"));
    LocalDate date = LocalDate.of(2025, 12, 25);
    LocalDateTime start = date.atTime(10, 0);
    LocalDateTime end = date.atTime(11, 0);
    calendarManager.createSingleEvent("QueryCal", "Christmas Brunch", start, end);
    Optional<List<ReadOnlyEvent>> eventsOpt = calendarManager.getEventsOnDate("QueryCal", date);
    assertTrue(eventsOpt.isPresent());
    List<ReadOnlyEvent> events = eventsOpt.get();
    assertFalse(events.isEmpty());
    boolean found = events.stream().anyMatch(e -> "Christmas Brunch".equals(e.getName()));
    assertTrue(found);
  }

  @Test
  public void testGetEventsInRange() throws Exception {
    calendarManager.createCalendar("RangeCal", ZoneId.of("America/New_York"));
    LocalDateTime eventStart = LocalDateTime.of(2025, 12, 31, 18, 0);
    LocalDateTime eventEnd = LocalDateTime.of(2025, 12, 31, 19, 0);
    calendarManager.createSingleEvent("RangeCal", "New Year Eve", eventStart, eventEnd);
    LocalDateTime rangeStart = LocalDateTime.of(2025, 12, 31, 0, 0);
    LocalDateTime rangeEnd = LocalDateTime.of(2026, 1, 1, 0, 0);
    Optional<List<ReadOnlyEvent>> eventsOpt =
        calendarManager.getEventsInRange("RangeCal", rangeStart, rangeEnd);
    assertTrue(eventsOpt.isPresent());
    boolean found = eventsOpt.get().stream().anyMatch(e -> "New Year Eve".equals(e.getName()));
    assertTrue(found);
  }

  @Test
  public void testExportCalendar() throws Exception {
    calendarManager.createCalendar("ExportCal", ZoneId.of("America/New_York"));
    LocalDateTime start = LocalDateTime.of(2026, 1, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2026, 1, 1, 10, 0);
    calendarManager.createSingleEvent("ExportCal", "New Year Meeting", start, end);
    Optional<List<ReadOnlyEvent>> export = calendarManager.exportCalendar("ExportCal");
    assertTrue(export.isPresent());
    boolean found = export.get().stream().anyMatch(e -> "New Year Meeting".equals(e.getName()));
    assertTrue(found);
  }

  @Test
  public void testIsBusy() throws Exception {
    calendarManager.createCalendar("BusyCal", ZoneId.of("America/New_York"));
    LocalDateTime busyTime = LocalDateTime.of(2026, 2, 14, 15, 0);
    LocalDateTime start = busyTime.minusHours(1);
    LocalDateTime end = busyTime.plusHours(1);
    calendarManager.createSingleEvent("BusyCal", "Valentine's Day Special", start, end);
    assertTrue(calendarManager.isBusy("BusyCal", busyTime));
    LocalDateTime freeTime = busyTime.plusHours(2);
    assertFalse(calendarManager.isBusy("BusyCal", freeTime));
  }
}
