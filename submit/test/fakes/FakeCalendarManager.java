package fakes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.ICalendarManager;
import model.ReadOnlyEvent;
import model.exceptions.CalendarException;

/**
 * A fake implementation of ICalendar for testing purposes.
 */
public class FakeCalendarManager implements ICalendarManager {

  private final List<List<String>> calls;
  private boolean isBusy;
  private boolean throwExceptionNext;

  /**
   * Constructor for FakeCalendarManager.
   */
  public FakeCalendarManager() {
    calls = new ArrayList<>();
    isBusy = false;
    throwExceptionNext = false;
  }

  private void updateCalls(String method, Object... args) {
    calls.add(List.of(method + " called", Arrays.asList(args).toString()));
  }

  public void setThrowExceptionNext() {
    throwExceptionNext = true;
  }

  public List<List<String>> getAllCalls() {
    return this.calls;
  }

  public void setIsBusy(boolean isBusy) {
    this.isBusy = isBusy;
  }

  @Override
  public void createCalendar(String calendarName, ZoneId timezone) throws CalendarException {
    if (throwExceptionNext) {
      throw new CalendarException("Fake exception");
    }
    updateCalls("createCalendar", calendarName, timezone);
  }

  @Override
  public void editCalendar(String calendarName, String property, String newValue)
      throws CalendarException {
    if (throwExceptionNext) {
      throw new CalendarException("Fake exception");
    }
    updateCalls("editCalendar", calendarName, property, newValue);
  }

  @Override
  public void detectCalendar(String calendarName) throws CalendarException {
    if (throwExceptionNext) {
      throw new CalendarException("Fake exception");
    }
    updateCalls("detectCalendar", calendarName);
  }

  @Override
  public void copySingleEvent(String curCalendar, String eventName, LocalDateTime sourceDateTime,
      String targetCalendarName, LocalDateTime targetDateTime)
      throws CalendarException {
    updateCalls("copySingleEvent", curCalendar, eventName, sourceDateTime, targetCalendarName,
        targetDateTime);
  }

  @Override
  public void copyEventsOnDate(String curCalendar, LocalDate sourceDate, String targetCalendarName,
      LocalDate targetDate) throws CalendarException {
    updateCalls("copyEventsOnDate", curCalendar, sourceDate, targetCalendarName, targetDate);
  }

  @Override
  public void copyEventsBetweenDates(String curCalendar, LocalDate startDate, LocalDate endDate,
      String targetCalendarName, LocalDate targetStartDate)
      throws CalendarException {
    updateCalls("copyEventsBetweenDates", curCalendar, startDate, endDate, targetCalendarName,
        targetStartDate);

  }

  @Override
  public void createSingleEvent(String calendarName, String name, LocalDateTime start,
      LocalDateTime end) throws Exception {
    if (throwExceptionNext) {
      throw new Exception("Fake exception");
    }
    updateCalls("createSingleEvent", calendarName, name, start, end);
  }

  @Override
  public void createRecurringEvent(String calendarName, String name, LocalDateTime start,
      LocalDateTime end, List<Character> daysOfWeek, int numRepeats)
      throws Exception {
    if (throwExceptionNext) {
      throw new Exception("Fake exception");
    }
    updateCalls("createRecurringEvent", calendarName, name, start, end, daysOfWeek, numRepeats);

  }

  @Override
  public void createRecurringEvent(String calendarName, String name, LocalDateTime start,
      LocalDateTime end, List<Character> daysOfWeek,
      LocalDateTime until) throws Exception {
    if (throwExceptionNext) {
      throw new Exception("Fake exception");
    }
    updateCalls("createRecurringEvent", calendarName, name, start, end, daysOfWeek, until);
  }

  @Override
  public void editSingleEvent(String calendarName, String eventName, LocalDateTime start,
      LocalDateTime end, String propertyName, String newValue)
      throws Exception {
    if (throwExceptionNext) {
      throw new Exception("Fake exception");
    }
    updateCalls("editSingleEvent", calendarName, eventName, start, end, propertyName, newValue);

  }

  @Override
  public void editMultipleEventsFrom(String calendarName, String eventName, LocalDateTime start,
      String propertyName, String newValue) throws Exception {
    if (throwExceptionNext) {
      throw new Exception("Fake exception");
    }
    updateCalls("editMultipleEventsFrom", calendarName, eventName, start, propertyName, newValue);
  }

  @Override
  public void editMultipleEvents(String calendarName, String eventName, String propertyName,
      String newValue) throws Exception {
    if (throwExceptionNext) {
      throw new Exception("Fake exception");
    }
    updateCalls("editMultipleEvents", calendarName, eventName, propertyName, newValue);

  }

  @Override
  public Optional<List<ReadOnlyEvent>> getEventsOnDate(String calendarName, LocalDate date) {
    if (throwExceptionNext) {
      throw new RuntimeException("Fake exception");
    }
    updateCalls("getEventsOnDate", calendarName, date);
    return Optional.empty();
  }

  @Override
  public Optional<List<ReadOnlyEvent>> getEventsInRange(String calendarName, LocalDateTime start,
      LocalDateTime end) {
    updateCalls("getEventsInRange", calendarName, start, end);
    return Optional.empty();
  }

  @Override
  public Optional<List<ReadOnlyEvent>> exportCalendar(String calendarName) throws Exception {
    if (throwExceptionNext) {
      throw new Exception("Fake exception");
    }
    updateCalls("exportCalendar", calendarName);
    return Optional.empty();
  }

  @Override
  public boolean isBusy(String calendarName, LocalDateTime dateTime) {
    updateCalls("isBusy", calendarName, dateTime);
    return isBusy;
  }

  @Override
  public Map<String, Object> getCalendarMetrics(String calendarName, LocalDate startDate,
      LocalDate endDate) {
    return Map.of();
  }
}
