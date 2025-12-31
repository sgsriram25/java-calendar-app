import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import model.CalendarManager;
import model.ICalendarManager;
import model.exceptions.CalendarException;
import model.exceptions.InvalidEventException;
import org.junit.Before;
import org.junit.Test;


/**
 * This class will test the functionality of the dashboard feature added.
 */
public class DashboardFeatureTest {

  private final String testCalendar = "TestCalendar";
  private ICalendarManager calendarManager;

  @Before
  public void setUp() throws CalendarException {
    calendarManager = new CalendarManager();
    calendarManager.createCalendar(testCalendar, ZoneId.systemDefault());
  }


  @Test
  public void testEmptyCalendarMetrics() {
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(7);

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, startDate,
        endDate);

    assertEquals(0, metrics.get("totalEvents"));
    assertTrue(((Map<?, ?>) metrics.get("weekdayCount")).isEmpty());
    assertTrue(((Map<?, ?>) metrics.get("eventNameCount")).isEmpty());
    assertEquals(0.0, metrics.get("onlineEventsPercentage"));
    assertNull(metrics.get("busiestDay"));
    assertNull(metrics.get("leastBusyDay"));
    assertEquals(0.0, metrics.get("averageEventsPerDay"));
  }


  @Test
  public void testMultipleEventsSameDay() throws Exception {
    LocalDate today = LocalDate.now();
    calendarManager.createSingleEvent(testCalendar, "Meeting1", today.atTime(9,
            0),
        today.atTime(10, 0));
    calendarManager.createSingleEvent(testCalendar, "Meeting2", today.atTime(11,
            0),
        today.atTime(12, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(2, metrics.get("totalEvents"));
    assertEquals(2, ((Map<?, ?>) metrics.get("weekday"
        + "Count")).get(today.getDayOfWeek().toString()));
    assertEquals(2, ((Map<?, ?>) metrics.get("event"
        + "NameCount")).size());
    assertEquals(2.0, metrics.get("averageEventsPerDay"));
  }


  @Test
  public void testEventsSpanningMultipleDays() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);

    calendarManager.createSingleEvent(testCalendar, "Event1",
        today.atTime(9, 0),
        today.atTime(10, 0));
    calendarManager.createSingleEvent(testCalendar, "Event2",
        tomorrow.atTime(14, 0),
        tomorrow.atTime(15, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, tomorrow);

    assertEquals(2, metrics.get("totalEvents"));
    assertEquals(2, ((Map<?, ?>) metrics.get("weekdayCount")).size());
    assertEquals(1.0, metrics.get("averageEventsPerDay"));
  }


  @Test
  public void testOnlineEventsPercentage() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Online1",
        today.atTime(9, 0),
        today.atTime(10, 0));
    calendarManager.editSingleEvent(testCalendar, "Online1",
        today.atTime(9, 0),
        today.atTime(10, 0),
        "location", "online");

    calendarManager.createSingleEvent(testCalendar, "Online2",
        today.atTime(11, 0),
        today.atTime(12, 0));
    calendarManager.editSingleEvent(testCalendar, "Online2",
        today.atTime(11, 0),
        today.atTime(12, 0),
        "location", "online");

    calendarManager.createSingleEvent(testCalendar, "Offline",
        today.atTime(14, 0),
        today.atTime(15, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(66.67, (double) metrics.get("onlineEventsPercentage"), 0.01);
  }


  @Test
  public void testBusiestDayIdentification() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);

    calendarManager.createSingleEvent(testCalendar, "Event1", today.atTime(9, 0),
        today.atTime(10, 0));
    calendarManager.createSingleEvent(testCalendar, "Event2", today.atTime(11, 0),
        today.atTime(12, 0));

    calendarManager.createSingleEvent(testCalendar, "Event3",
        tomorrow.atTime(14, 0),
        tomorrow.atTime(15, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, tomorrow);

    List<LocalDate> busiestDays = (List<LocalDate>) metrics.get("busiestDay");
    assertEquals(1, busiestDays.size());
    assertEquals(today, busiestDays.get(0));
  }


  @Test
  public void testLeastBusyDayIdentification() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);

    calendarManager.createSingleEvent(testCalendar, "Event1",
        today.atTime(9, 0),
        today.atTime(10, 0));
    calendarManager.createSingleEvent(testCalendar, "Event2", today.atTime(11, 0),
        today.atTime(12, 0));

    calendarManager.createSingleEvent(testCalendar, "Event3",
        tomorrow.atTime(14, 0),
        tomorrow.atTime(15, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, tomorrow);

    List<LocalDate> leastBusyDays = (List<LocalDate>) metrics.get("leastBusyDay");
    assertEquals(1, leastBusyDays.size());
    assertEquals(tomorrow, leastBusyDays.get(0));
  }


  @Test
  public void testMultipleDaysWithSameEventCount() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);

    calendarManager.createSingleEvent(testCalendar, "Event1", today.atTime(9, 0),
        today.atTime(10, 0));
    calendarManager.createSingleEvent(testCalendar, "Event2", today.atTime(11, 0),
        today.atTime(12, 0));

    calendarManager.createSingleEvent(testCalendar, "Event3",
        tomorrow.atTime(14, 0),
        tomorrow.atTime(15, 0));
    calendarManager.createSingleEvent(testCalendar, "Event4",
        tomorrow.atTime(16, 0),
        tomorrow.atTime(17, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, tomorrow);

    List<LocalDate> busiestDays = (List<LocalDate>) metrics.get("busiestDay");
    assertEquals(2, busiestDays.size());
    assertTrue(busiestDays.contains(today));
    assertTrue(busiestDays.contains(tomorrow));
  }


  @Test
  public void testAverageEventsPerDay() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    LocalDate dayAfter = today.plusDays(2);

    calendarManager.createSingleEvent(testCalendar, "Event1", today.atTime(9, 0),
        today.atTime(10, 0));
    calendarManager.createSingleEvent(testCalendar, "Event2",
        tomorrow.atTime(11, 0),
        tomorrow.atTime(12, 0));
    calendarManager.createSingleEvent(testCalendar, "Event3",
        dayAfter.atTime(14, 0),
        dayAfter.atTime(15, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, dayAfter);

    assertEquals(1.0, metrics.get("averageEventsPerDay"));
  }


  @Test
  public void testRecurringEventsInMetrics() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusDays(7);

    List<Character> daysOfWeek = List.of('M', 'W');
    calendarManager.createRecurringEvent(testCalendar, "Recurring",
        today.atTime(9, 0), today.atTime(10, 0),
        daysOfWeek, endDate.atTime(23, 59, 59));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, endDate);

    assertTrue((int) metrics.get("totalEvents") >= 2);
  }


  @Test
  public void testAllDayEventsInMetrics() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "AllDay",
        today.atStartOfDay(),
        today.atTime(23, 59, 59));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testOvernightEventsInMetrics() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);

    calendarManager.createSingleEvent(testCalendar, "Overnight",
        today.atTime(23, 0),
        tomorrow.atTime(1, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, tomorrow);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testNonExistentCalendarMetrics() {
    LocalDate today = LocalDate.now();

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(
        "NonExistentCalendar", today,
        today);

    assertEquals(0, metrics.get("totalEvents"));
    assertTrue(((Map<?, ?>) metrics.get("weekdayCount")).isEmpty());
  }


  @Test
  public void testInvalidDateRange() {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today,
        yesterday);

    assertEquals(0, metrics.get("totalEvents"));
  }


  @Test
  public void testEventNameCounts() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Standup",
        today.atTime(9, 0),
        today.atTime(9, 30));
    calendarManager.createSingleEvent(testCalendar, "Standup",
        today.atTime(14, 0),
        today.atTime(14, 30));
    calendarManager.createSingleEvent(testCalendar, "Review",
        today.atTime(16, 0),
        today.atTime(17, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    Map<String, Integer> nameCounts = (Map<String, Integer>) metrics.get("eventNameCount");
    assertEquals(2, (int) nameCounts.get("Standup"));
    assertEquals(1, (int) nameCounts.get("Review"));
  }


  @Test
  public void testEmptyEventName() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "",
        today.atTime(9, 0), today.atTime(10, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, metrics.get("totalEvents"));
    assertEquals(1, ((Map<?, ?>) metrics.get("eventNameCount")).get(""));
  }


  @Test
  public void testLongEventName() throws Exception {
    LocalDate today = LocalDate.now();
    String longName = "Very long event name that exceeds typical length limits for event names";

    calendarManager.createSingleEvent(testCalendar, longName, today.atTime(9, 0),
        today.atTime(10, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, ((Map<?, ?>) metrics.get("eventNameCount")).get(longName));
  }


  @Test
  public void testSpecialCharactersInEventName() throws Exception {
    LocalDate today = LocalDate.now();
    String specialName = "Event @#$%^&*()";

    calendarManager.createSingleEvent(testCalendar, specialName, today.atTime(9, 0),
        today.atTime(10, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, ((Map<?, ?>) metrics.get("eventNameCount")).get(specialName));
  }


  @Test
  public void testMultipleLocations() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Meeting1", today.atTime(9,
            0),
        today.atTime(10, 0));
    calendarManager.editSingleEvent(testCalendar, "Meeting1", today.atTime(9,
            0),
        today.atTime(10, 0),
        "location", "Office");

    calendarManager.createSingleEvent(testCalendar, "Meeting2", today.atTime(11,
            0),
        today.atTime(12, 0));
    calendarManager.editSingleEvent(testCalendar, "Meeting2", today.atTime(11,
            0),
        today.atTime(12, 0),
        "location", "Online");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(50.0, metrics.get("onlineEventsPercentage"));
  }


  @Test
  public void testTimezoneChanges() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Meeting", today.atTime(9,
            0),
        today.atTime(10, 0));

    calendarManager.editCalendar(testCalendar, "timezone", "America/New_York");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testMidnightEvents() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Midnight", today.atTime(0,
            0),
        today.atTime(0, 30));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testEndOfDayEvents() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Late", today.atTime(23, 30),
        today.atTime(23, 59));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testVeryShortEvents() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Short", today.atTime(12, 0),
        today.atTime(12, 1));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testVeryLongEvents() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);

    calendarManager.createSingleEvent(testCalendar, "Long", today.atTime(9, 0),
        tomorrow.atTime(17, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, tomorrow);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test(expected = InvalidEventException.class)
  public void testEventsWithSameStartEndTime() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Invalid", today.atTime(9, 0),
        today.atTime(9, 0));
  }


  @Test(expected = InvalidEventException.class)
  public void testEventsWithEndBeforeStart() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Invalid", today.atTime(10,
            0),
        today.atTime(9, 0));
  }


  @Test
  public void testSingleDayRange() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Event", today.atTime(10,
            0),
        today.atTime(11, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, metrics.get("totalEvents"));
    assertEquals(1.0, metrics.get("averageEventsPerDay"));
  }


  @Test
  public void testMultiWeekRange() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate twoWeeksLater = today.plusWeeks(2);

    calendarManager.createSingleEvent(testCalendar, "Week1", today.atTime(10, 0),
        today.atTime(11, 0));
    calendarManager.createSingleEvent(testCalendar, "Week2",
        today.plusWeeks(1).atTime(10, 0),
        today.plusWeeks(1).atTime(11, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today,
        twoWeeksLater);

    assertEquals(2, metrics.get("totalEvents"));
    assertEquals(2.0 / 15, (double) metrics.get("averageEventsPerDay"),
        0.01);
  }


  @Test
  public void testYearBoundary() throws Exception {
    LocalDate dec31 = LocalDate.of(2023, 12, 31);
    LocalDate jan1 = LocalDate.of(2024, 1, 1);

    calendarManager.createSingleEvent(testCalendar, "NYE", dec31.atTime(20, 0),
        dec31.atTime(23, 59));
    calendarManager.createSingleEvent(testCalendar, "NewYear", jan1.atTime(0, 0),
        jan1.atTime(1, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, dec31, jan1);

    assertEquals(2, metrics.get("totalEvents"));
  }


  @Test
  public void testLeapDay() throws Exception {
    LocalDate feb28 = LocalDate.of(2023, 2, 28);
    LocalDate mar1 = LocalDate.of(2023, 3, 1);

    calendarManager.createSingleEvent(testCalendar, "FebEnd", feb28.atTime(10, 0),
        feb28.atTime(11, 0));
    calendarManager.createSingleEvent(testCalendar, "MarStart", mar1.atTime(10,
            0),
        mar1.atTime(11, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, feb28, mar1);

    assertEquals(2, metrics.get("totalEvents"));
  }


  @Test
  public void testFromDateEqualToToDate() throws Exception {
    LocalDate today = LocalDate.now();
    calendarManager.createSingleEvent(testCalendar, "Meeting",
        today.atTime(10, 0),
        today.atTime(11, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testCreateSingleEventSameDay() throws Exception {
    LocalDate today = LocalDate.now();
    calendarManager.createSingleEvent(testCalendar, "Meeting",
        today.atTime(10, 0),
        today.atTime(11, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testCreateSingleEventDifferentDays() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    calendarManager.createSingleEvent(testCalendar, "Conference",
        today.atTime(23, 0),
        tomorrow.atTime(1, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, tomorrow);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testEditLocationOfflineToOnline() throws Exception {
    LocalDate today = LocalDate.now();
    calendarManager.createSingleEvent(testCalendar, "Meeting",
        today.atTime(10, 0),
        today.atTime(11, 0));
    calendarManager.editSingleEvent(testCalendar, "Meeting",
        today.atTime(10, 0),
        today.atTime(11, 0),
        "location", "online");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(100.0, metrics.get("onlineEventsPercentage"));
  }


  @Test
  public void testEditLocationOnlineToOffline() throws Exception {
    LocalDate today = LocalDate.now();
    calendarManager.createSingleEvent(testCalendar, "Meeting",
        today.atTime(10, 0),
        today.atTime(11, 0));
    calendarManager.editSingleEvent(testCalendar, "Meeting",
        today.atTime(10, 0),
        today.atTime(11, 0),
        "location", "online");
    calendarManager.editSingleEvent(testCalendar, "Meeting",
        today.atTime(10, 0),
        today.atTime(11, 0),
        "location", "office");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(0.0, metrics.get("onlineEventsPercentage"));
  }


  @Test
  public void testCreateManySingleEvents() throws Exception {
    LocalDate today = LocalDate.now();

    for (int i = 0; i < 10; i++) {
      calendarManager.createSingleEvent(testCalendar, "Event" + i,
          today.atTime(9 + i, 0),
          today.atTime(9 + i, 30));
    }

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(10, metrics.get("totalEvents"));
  }


  @Test
  public void testCreateRecurringEventWithOccurrences() throws Exception {
    LocalDate today = LocalDate.now();
    List<Character> daysOfWeek = List.of('M', 'W', 'F');

    calendarManager.createRecurringEvent(testCalendar, "Standup",
        today.atTime(9, 0), today.atTime(9, 30),
        daysOfWeek, 4);

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today,
        today.plusWeeks(2));

    assertTrue((int) metrics.get("totalEvents") >= 4);
  }


  @Test
  public void testCreateRecurringEventWithUntilDate() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusWeeks(2);
    List<Character> daysOfWeek = List.of('T', 'R');

    calendarManager.createRecurringEvent(testCalendar, "Workshop",
        today.atTime(14, 0), today.atTime(16, 0),
        daysOfWeek, endDate.atTime(23, 59, 59));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, endDate);

    assertTrue((int) metrics.get("totalEvents") >= 4);
  }


  @Test
  public void testRecurringEventSpanningWeeks() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusMonths(1);
    List<Character> daysOfWeek = List.of('M');

    calendarManager.createRecurringEvent(testCalendar, "Weekly",
        today.atTime(10, 0), today.atTime(11, 0),
        daysOfWeek, endDate.atTime(23, 59, 59));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, endDate);

    assertTrue((int) metrics.get("totalEvents") >= 4);
  }


  @Test
  public void testRecurringEventSingleDay() throws Exception {
    LocalDate today = LocalDate.now();
    List<Character> daysOfWeek = List.of('W');

    calendarManager.createRecurringEvent(testCalendar, "SingleDay",
        today.atTime(15, 0), today.atTime(16, 0),
        daysOfWeek, 3);

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today,
        today.plusWeeks(3));

    assertEquals(3, metrics.get("totalEvents"));
  }


  @Test
  public void testRecurringEventAllWeekdays() throws Exception {
    LocalDate today = LocalDate.now();
    List<Character> daysOfWeek = List.of('M', 'T', 'W', 'R', 'F');

    calendarManager.createRecurringEvent(testCalendar, "Daily",
        today.atTime(9, 0), today.atTime(10, 0),
        daysOfWeek, 5);

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today,
        today.plusWeeks(1));

    assertEquals(5, metrics.get("totalEvents"));
  }


  @Test
  public void testRecurringEventWeekendDays() throws Exception {
    LocalDate today = LocalDate.now();
    List<Character> daysOfWeek = List.of('S', 'U');

    calendarManager.createRecurringEvent(testCalendar, "Weekend",
        today.atTime(11, 0), today.atTime(12, 0),
        daysOfWeek, 4);

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today,
        today.plusWeeks(2));

    assertTrue((int) metrics.get("totalEvents") >= 2);
  }


  @Test
  public void testEditRecurringEventProperty() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusWeeks(1);
    List<Character> daysOfWeek = List.of('M', 'W', 'F');

    calendarManager.createRecurringEvent(testCalendar, "Class",
        today.atTime(13, 0), today.atTime(14, 0),
        daysOfWeek, endDate.atTime(23, 59, 59));

    calendarManager.editMultipleEvents(testCalendar, "Class", "location",
        "online");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, endDate);

    assertEquals(100.0, metrics.get("onlineEventsPercentage"));
  }


  @Test
  public void testEditSingleOccurrenceOfRecurringEvent() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate nextOccurrence = today.with(java.time.DayOfWeek.WEDNESDAY);
    if (nextOccurrence.isBefore(today)) {
      nextOccurrence = nextOccurrence.plusWeeks(1);
    }
    List<Character> daysOfWeek = List.of('W');

    calendarManager.createRecurringEvent(testCalendar, "Standup",
        today.atTime(10, 0), today.atTime(10, 30),
        daysOfWeek, 3);

    calendarManager.editSingleEvent(testCalendar, "Standup",
        nextOccurrence.atTime(10, 0), nextOccurrence.atTime(10, 30),
        "location", "online");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today,
        today.plusWeeks(3));

    assertTrue((double) metrics.get("onlineEventsPercentage") > 0);
  }


  @Test(expected = InvalidEventException.class)
  public void testCreateEventSameStartEndTime() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Invalid", today.atTime(10,
            0),
        today.atTime(10, 0));
  }


  @Test(expected = InvalidEventException.class)
  public void testCreateEventEndBeforeStart() throws Exception {
    LocalDate today = LocalDate.now();

    calendarManager.createSingleEvent(testCalendar, "Invalid", today.atTime(11,
            0),
        today.atTime(10, 0));
  }


  @Test(expected = InvalidEventException.class)
  public void testCreateRecurringEventInvalidDays() throws Exception {
    LocalDate today = LocalDate.now();
    List<Character> invalidDays = List.of('X');

    calendarManager.createRecurringEvent(testCalendar, "Invalid",
        today.atTime(9, 0), today.atTime(10, 0),
        invalidDays, 3);
  }


  @Test(expected = InvalidEventException.class)
  public void testCreateRecurringEventZeroOccurrences() throws Exception {
    LocalDate today = LocalDate.now();
    List<Character> daysOfWeek = List.of('M');

    calendarManager.createRecurringEvent(testCalendar, "Invalid",
        today.atTime(9, 0), today.atTime(10, 0),
        daysOfWeek, 0);
  }


  @Test
  public void testManySingleEventsSameName() throws Exception {
    LocalDate today = LocalDate.now();

    for (int i = 0; i < 5; i++) {
      calendarManager.createSingleEvent(testCalendar, "Meeting",
          today.atTime(9 + i, 0),
          today.atTime(9 + i, 30));
    }

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(5, ((Map<?, ?>) metrics.get("eventNameCount")).get("Meeting"));
  }


  @Test
  public void testManySingleEventsDifferentNames() throws Exception {
    LocalDate today = LocalDate.now();

    for (int i = 0; i < 5; i++) {
      calendarManager.createSingleEvent(testCalendar, "Meeting" + i,
          today.atTime(9 + i, 0),
          today.atTime(9 + i, 30));
    }

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(5, ((Map<?, ?>) metrics.get("eventNameCount")).size());
  }


  @Test
  public void testSingleEventSpanningMultipleDays() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);

    calendarManager.createSingleEvent(testCalendar, "Conference",
        today.atTime(18, 0),
        tomorrow.atTime(14, 0));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, tomorrow);

    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testEditSingleEventDescription() throws Exception {
    LocalDate today = LocalDate.now();
    calendarManager.createSingleEvent(testCalendar, "Meeting", today.atTime(10,
            0),
        today.atTime(11, 0));
    calendarManager.editSingleEvent(testCalendar, "Meeting", today.atTime(10,
            0),
        today.atTime(11, 0),
        "description", "Important meeting");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);
    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testEditSingleEventVisibility() throws Exception {
    LocalDate today = LocalDate.now();
    calendarManager.createSingleEvent(testCalendar, "Meeting", today.atTime(10,
            0),
        today.atTime(11, 0));
    calendarManager.editSingleEvent(testCalendar, "Meeting", today.atTime(10,
            0),
        today.atTime(11, 0),
        "public", "false");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);
    assertEquals(1, metrics.get("totalEvents"));
  }


  @Test
  public void testDailyRecurringEvent() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusDays(4);
    List<Character> daysOfWeek = List.of('M', 'T', 'W', 'R', 'F', 'S', 'U');

    calendarManager.createRecurringEvent(testCalendar, "Daily",
        today.atTime(9, 0), today.atTime(10, 0),
        daysOfWeek, endDate.atTime(23, 59, 59));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, endDate);

    assertEquals(5, metrics.get("totalEvents"));
  }


  @Test
  public void testBiWeeklyRecurringEvent() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusWeeks(4);
    List<Character> daysOfWeek = List.of('M');

    calendarManager.createRecurringEvent(testCalendar, "BiWeekly",
        today.atTime(14, 0), today.atTime(15, 0),
        daysOfWeek, 3);

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, endDate);

    assertEquals(3, metrics.get("totalEvents"));
  }


  @Test
  public void testComplexRecurringEvent() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusWeeks(2);
    List<Character> daysOfWeek = List.of('M', 'W', 'F');

    calendarManager.createRecurringEvent(testCalendar, "Complex",
        today.atTime(16, 0), today.atTime(17, 0),
        daysOfWeek, endDate.atTime(23, 59, 59));

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, endDate);

    assertTrue((int) metrics.get("totalEvents") >= 6);
  }


  @Test
  public void testEditMultiplePropertiesSingleEvent() throws Exception {
    LocalDate today = LocalDate.now();
    calendarManager.createSingleEvent(testCalendar, "Meeting", today.atTime(10,
            0),
        today.atTime(11, 0));
    calendarManager.editSingleEvent(testCalendar, "Meeting", today.atTime(10,
            0),
        today.atTime(11, 0),
        "location", "online");
    calendarManager.editSingleEvent(testCalendar, "Meeting", today.atTime(10,
            0),
        today.atTime(11, 0),
        "description", "Team sync");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, today);

    assertEquals(100.0, metrics.get("onlineEventsPercentage"));
  }


  @Test
  public void testEditMultiplePropertiesRecurringSeries() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusWeeks(1);
    List<Character> daysOfWeek = List.of('T', 'R');

    calendarManager.createRecurringEvent(testCalendar, "Workshop",
        today.atTime(14, 0), today.atTime(16, 0),
        daysOfWeek, endDate.atTime(23, 59, 59));

    calendarManager.editMultipleEvents(testCalendar, "Workshop",
        "location", "online");
    calendarManager.editMultipleEvents(testCalendar, "Workshop",
        "description", "Training session");

    Map<String, Object> metrics = calendarManager.getCalendarMetrics(testCalendar, today, endDate);

    assertEquals(100.0, metrics.get("onlineEventsPercentage"));
  }
}