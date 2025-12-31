package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;
import model.exceptions.CalendarException;
import model.exceptions.InvalidEditException;
import model.exceptions.InvalidEventException;

/**
 * The CalendarManager is responsible for creating, editing, and storing calendars.
 * It delegates all event-related operations—such as creation, editing, copying,
 * querying by date or date range, and exporting—to the appropriate calendar instance
 * identified by its name.
 */
public class CalendarManager implements ICalendarManager {
  private final Map<String, ICalendar> calendars;

  /**
   * Constructs a new CalendarManager.
   */
  public CalendarManager() {
    calendars = new HashMap<>();
  }

  /**
   * Creates a new calendar with the given name and timezone.
   *
   * @param calendarName the unique name of the calendar
   * @param timezone     the IANA timezone string (e.g., "America/New_York")
   * @throws CalendarException if the calendar name already exists
   */
  @Override
  public void createCalendar(String calendarName, ZoneId timezone) throws CalendarException {
    if (calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar name " + calendarName + " already exists.");
    }
    ICalendar newCalendar = new Calendar(timezone);
    calendars.put(calendarName, newCalendar);
  }

  /**
   * Edits a calendar property.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     the property to edit (e.g., "name" or "timezone")
   * @param newValue     the new value for the property
   * @throws CalendarException if the calendar is not found or the property is invalid
   */
  @Override
  public void editCalendar(String calendarName, String property, String newValue)
      throws CalendarException {
    if (!calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar not found.");
    }
    ICalendar calendar = calendars.get(calendarName);
    switch (property.toLowerCase()) {
      case "name":
        editCalendarName(calendar, calendarName, newValue);
        break;
      case "timezone":
        editCalendarTimezone(calendar, newValue);
        break;
      default:
        throw new CalendarException("Invalid property.");
    }
  }

  /**
   * Edits the name of a calendar.
   *
   * @param calendar the calendar to edit
   * @param oldName  the old name of the calendar
   * @param newValue the new name of the calendar
   * @throws CalendarException if the new name already exists
   */
  private void editCalendarName(ICalendar calendar, String oldName, String newValue)
      throws CalendarException {
    if (calendars.containsKey(newValue)) {
      throw new CalendarException("Calendar name already exists.");
    }
    calendars.remove(oldName);
    calendars.put(newValue, calendar);
  }

  /**
   * Edits the timezone of a calendar. The corresponding calendar is updated with the new timezone.
   *
   * @param calendar the calendar to edit
   * @param newValue the new timezone
   * @throws CalendarException if the timezone is invalid
   */
  private void editCalendarTimezone(ICalendar calendar, String newValue) throws CalendarException {
    ZoneId timezone;
    try {
      timezone = ZoneId.of(newValue);
    } catch (Exception e) {
      throw new CalendarException("Invalid timezone.");
    }
    calendar.setTimezone(timezone);
  }

  /**
   * Uses a calendar as the active calendar and returns the timezone of the active calendar.
   *
   * @param calendarName the name of the calendar to set as active
   * @throws CalendarException if the calendar is not found
   */
  @Override
  public void detectCalendar(String calendarName) throws CalendarException {
    if (!calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar not found.");
    }
  }

  /**
   * Copies a single event from one calendar to another.
   *
   * @param curCalendar        the name of the current calendar
   * @param eventName          the name of the event to copy
   * @param sourceDateTime     the start time of the event to copy
   * @param targetCalendarName the name of the target calendar
   * @param targetDateTime     the start time of the new event
   */
  @Override
  public void copySingleEvent(String curCalendar, String eventName, LocalDateTime sourceDateTime,
                              String targetCalendarName, LocalDateTime targetDateTime)
      throws CalendarException {
    if (!calendars.containsKey(targetCalendarName)) {
      throw new CalendarException("Target calendar not found.");
    }
    Optional<InternalEvent> event =
        calendars.get(curCalendar).getEventOnTime(eventName, sourceDateTime);
    if (event.isEmpty()) {
      throw new CalendarException("Event not found.");
    }
    try {
      calendars.get(targetCalendarName).createSingleEventCopy(event.get(), targetDateTime);
    } catch (Exception e) {
      throw new CalendarException("Error copying event." + e.getMessage());
    }
  }

  /**
   * Copies all events on a given date from one calendar to another. This will be an atomic
   * operation: if any event fails to copy due to conflict, no events will be copied.
   *
   * @param curCalendar        the name of the current calendar
   * @param sourceDate         the date to copy events from
   * @param targetCalendarName the name of the target calendar
   * @param targetDate         the date to copy events to
   */
  @Override
  public void copyEventsOnDate(String curCalendar, LocalDate sourceDate, String targetCalendarName,
                               LocalDate targetDate) throws CalendarException {
    if (!calendars.containsKey(targetCalendarName)) {
      throw new CalendarException("Target calendar not found.");
    }

    Optional<List<InternalEvent>> sourceEvents =
        calendars.get(curCalendar).getEventsOnDate(sourceDate);
    if (sourceEvents.isEmpty()) {
      throw new CalendarException("No events found on source date.");
    }

    ZoneId sourceZone = calendars.get(curCalendar).getTimezone();
    ZoneId targetZone = calendars.get(targetCalendarName).getTimezone();
    ICalendar targetCalendar = calendars.get(targetCalendarName);

    
    List<InternalEvent> addedEvents = new ArrayList<>();

    try {
      for (InternalEvent event : sourceEvents.get()) {
        
        LocalDateTime candidate = LocalDateTime.of(targetDate, event.getStart().toLocalTime());
        ZonedDateTime sourceZdt = candidate.atZone(sourceZone);
        ZonedDateTime targetZdt = sourceZdt.withZoneSameInstant(targetZone);
        LocalDateTime newStart = targetZdt.toLocalDateTime();

        
        InternalEvent newEvent = targetCalendar.createSingleEventCopy(event, newStart);
        addedEvents.add(newEvent);
      }
    } catch (Exception e) {
      
      for (InternalEvent ev : addedEvents) {
        targetCalendar.removeEvent(ev);
      }
      throw new CalendarException("Error copying event: " + e.getMessage());
    }
  }


  /**
   * Copies all events between two dates from one calendar to another. This will be an atomic
   * operation: if any event fails to copy due to conflict, no events will be copied.
   *
   * @param curCalendar        the name of the current calendar
   * @param startDate          the start date of the interval to copy events from
   * @param endDate            the end date of the interval to copy events from
   * @param targetCalendarName the name of the target calendar
   * @param targetStartDate    the start date of the interval to copy events to
   */
  @Override
  public void copyEventsBetweenDates(String curCalendar, LocalDate startDate, LocalDate endDate,
                                     String targetCalendarName, LocalDate targetStartDate)
      throws CalendarException {
    if (!calendars.containsKey(targetCalendarName)) {
      throw new CalendarException("Target calendar not found.");
    }

    LocalDateTime sourceIntervalStart = startDate.atStartOfDay();
    LocalDateTime sourceIntervalEnd = endDate.atTime(23, 59, 59);

    Optional<List<InternalEvent>> sourceEventsOpt =
        calendars.get(curCalendar).getEventsInRange(sourceIntervalStart, sourceIntervalEnd);

    if (sourceEventsOpt.isEmpty()) {
      throw new CalendarException("No events found in source interval.");
    }

    ZoneId sourceZone = calendars.get(curCalendar).getTimezone();
    ZoneId targetZone = calendars.get(targetCalendarName).getTimezone();

    LocalDateTime targetIntervalStart = targetStartDate.atStartOfDay();

    ICalendar targetCalendar = calendars.get(targetCalendarName);
    
    List<InternalEvent> addedEvents = new ArrayList<>();

    try {
      for (InternalEvent event : sourceEventsOpt.get()) {
        
        Duration offset = Duration.between(sourceIntervalStart, event.getStart());
        ZonedDateTime newStartLocal = targetIntervalStart.plus(offset).atZone(sourceZone);
        LocalDateTime newStart = newStartLocal.withZoneSameInstant(targetZone).toLocalDateTime();

        
        InternalEvent newEvent = targetCalendar.createSingleEventCopy(event, newStart);
        addedEvents.add(newEvent);
      }
    } catch (Exception e) {
      
      for (InternalEvent ev : addedEvents) {
        targetCalendar.removeEvent(ev);
      }
      throw new CalendarException("Error copying event: " + e.getMessage());
    }
  }

  /**
   * create a single event in the given calendar.
   *
   * @param calendarName The name of the calendar
   * @param name         The name of the event
   * @param start        The start time of the event
   * @param end          The end time of the event
   * @throws Exception If the calendar is not found or the event cannot be created
   */
  @Override
  public void createSingleEvent(String calendarName, String name, LocalDateTime start,
                                LocalDateTime end) throws Exception {
    if (!calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar not found.");
    }
    try {
      calendars.get(calendarName).createSingleEvent(name, start, end);
    } catch (Exception e) {
      throw new InvalidEventException("Error creating event: " + e.getMessage());
    }

  }

  /**
   * create a recurring event in the given calendar.
   *
   * @param calendarName The name of the calendar
   * @param name         The name of the event
   * @param start        The start time of the event
   * @param end          The end time of the event
   * @param daysOfWeek   The days of the week the event occurs
   * @param numRepeats   The number of times the event repeats
   * @throws Exception If the calendar is not found or the event cannot be created
   */
  @Override
  public void createRecurringEvent(String calendarName, String name, LocalDateTime start,
                                   LocalDateTime end, List<Character> daysOfWeek, int numRepeats)
      throws Exception {
    if (!calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar not found.");
    }
    try {
      calendars.get(calendarName).createRecurringEvent(name, start, end, daysOfWeek, numRepeats);
    } catch (Exception e) {
      throw new InvalidEventException("Error creating event: " + e.getMessage());
    }
  }


  /**
   * create a recurring event in the given calendar. This is an overloaded method.
   *
   * @param calendarName The name of the calendar.
   * @param name         The name of the event.
   * @param start        The start time of the event.
   * @param end          The end time of the event.
   * @param daysOfWeek   The days of the week the event occurs.
   * @param until        The end date of the event (inclusive).
   * @throws Exception If the calendar is not found or the event cannot be created.
   */
  @Override
  public void createRecurringEvent(String calendarName, String name, LocalDateTime start,
                                   LocalDateTime end, List<Character> daysOfWeek,
                                   LocalDateTime until) throws Exception {
    if (!calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar not found.");
    }
    try {
      calendars.get(calendarName).createRecurringEvent(name, start, end, daysOfWeek, until);
    } catch (Exception e) {
      throw new InvalidEditException("Error creating event: " + e.getMessage());
    }

  }

  /**
   * edit a single event in the given calendar.
   *
   * @param calendarName The name of the calendar.
   * @param eventName    The name of the event to edit.
   * @param start        The start time of the event to edit.
   * @param end          The end time of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws Exception If the calendar is not found or the event cannot be edited.
   */
  @Override
  public void editSingleEvent(String calendarName, String eventName, LocalDateTime start,
                              LocalDateTime end, String propertyName, String newValue)
      throws Exception {
    if (!calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar not found.");
    }
    try {
      calendars.get(calendarName).editSingleEvent(eventName, start, end, propertyName, newValue);
    } catch (Exception e) {
      throw new InvalidEditException("Error editing event: " + e.getMessage());
    }

  }

  /**
   * edit all events after given time in the given calendar.
   *
   * @param calendarName The name of the calendar.
   * @param eventName    The name of the event to edit.
   * @param start        The start time of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws Exception If the calendar is not found or the event cannot be edited.
   */
  @Override
  public void editMultipleEventsFrom(String calendarName, String eventName, LocalDateTime start,
                                     String propertyName, String newValue) throws Exception {
    if (!calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar not found.");
    }
    try {
      calendars.get(calendarName).editMultipleEvents(eventName, start, propertyName, newValue);
    } catch (Exception e) {
      throw new InvalidEditException("Error editing event: " + e.getMessage());
    }

  }

  /**
   * edit all events with the name in the given calendar.
   *
   * @param calendarName The name of the calendar.
   * @param eventName    The name of the events to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws Exception If the calendar is not found or the event cannot be edited.
   */
  @Override
  public void editMultipleEvents(String calendarName, String eventName, String propertyName,
                                 String newValue) throws Exception {
    if (!calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar not found.");
    }
    try {
      calendars.get(calendarName).editMultipleEvents(eventName, propertyName, newValue);
    } catch (Exception e) {
      throw new InvalidEditException("Error editing event: " + e.getMessage());
    }

  }

  /**
   * return the list of events on the given date in the given calendar.
   *
   * @param calendarName The name of the calendar.
   * @param date         The date to get events for.
   * @return The list of events on the given date.
   */
  @Override
  public Optional<List<ReadOnlyEvent>> getEventsOnDate(String calendarName, LocalDate date) {
    if (!calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar not found.");
    }
    Optional<List<InternalEvent>> events = calendars.get(calendarName).getEventsOnDate(date);
    return events.map(List::copyOf);
  }

  /**
   * return the list of events in the given date range in the given calendar.
   *
   * @param calendarName The name of the calendar
   * @param start        The start of the date range
   * @param end          The end of the date range
   * @return The list of events in the given date range
   */
  @Override
  public Optional<List<ReadOnlyEvent>> getEventsInRange(String calendarName, LocalDateTime start,
                                                        LocalDateTime end) {
    if (!calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar not found.");
    }
    Optional<List<InternalEvent>> events = calendars.get(calendarName).getEventsInRange(start, end);
    return events.map(List::copyOf);
  }

  /**
   * return the list of events with the given name in the given calendar.
   *
   * @param calendarName The name of the calendar
   *                     throws CalendarException if the calendar is not found
   */
  @Override
  public Optional<List<ReadOnlyEvent>> exportCalendar(String calendarName) throws Exception {
    if (!calendars.containsKey(calendarName)) {
      throw new CalendarException("Calendar not found.");
    }
    return calendars.get(calendarName).exportCalendar();
  }

  /**
   * return the list of events with the given name in the given calendar.
   *
   * @param calendarName The name of the calendar
   * @param dateTime     The time to check
   * @return True if the calendar is busy at the given time, false otherwise.
   */
  @Override
  public boolean isBusy(String calendarName, LocalDateTime dateTime) {
    if (!calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar not found.");
    }
    return calendars.get(calendarName).isBusy(dateTime);
  }

  /**
   * This maps the metrics with the calendar using map function.
   */
  public Map<String, Object> getCalendarMetrics(String calendarName, LocalDate startDate,
      LocalDate endDate) {
    ICalendar calendar = calendars.get(calendarName);
    Map<String, Object> metrics = new HashMap<>();

    try {
      if (calendar == null) {
        throw new CalendarException("Calendar not found.");
      }

      List<InternalEvent> allEventInstances = new ArrayList<>();
      
      for (Map.Entry<String, List<InternalEvent>> entry : calendar.getEvents().entrySet()) {
        for (InternalEvent event : entry.getValue()) {
          if (event instanceof RecurringEvent) {
            
            List<InternalEvent> instances = expandRecurringEvent(event, startDate, endDate);
            allEventInstances.addAll(instances);
          } else {
            
            LocalDate eventDate = event.getStart().toLocalDate();
            if (!eventDate.isBefore(startDate) && !eventDate.isAfter(endDate)) {
              allEventInstances.add(event);
            }
          }
        }
      }

      
      int totalEvents = allEventInstances.size();
      metrics.put("totalEvents", totalEvents);

      
      Map<String, Integer> weekdayCount = new HashMap<>();
      for (InternalEvent event : allEventInstances) {
        String weekday = event.getStart().getDayOfWeek().toString();
        weekdayCount.merge(weekday, 1, Integer::sum);
      }
      metrics.put("weekdayCount", weekdayCount);

      
      Map<String, Integer> eventNameCount = new HashMap<>();
      for (InternalEvent event : allEventInstances) {
        eventNameCount.merge(event.getName(), 1, Integer::sum);
      }
      metrics.put("eventNameCount", eventNameCount);

      
      int onlineEvents = 0;
      for (InternalEvent event : allEventInstances) {
        if (event.getLocation() != null && event.getLocation().
            equalsIgnoreCase("online")) {
          onlineEvents++;
        }
      }
      double onlineEventsPercentage = totalEvents > 0 ? (double) onlineEvents /
          totalEvents * 100 : 0.0;
      metrics.put("onlineEventsPercentage", onlineEventsPercentage);

      
      Map<LocalDate, Integer> eventsPerDay = new HashMap<>();
      for (InternalEvent event : allEventInstances) {
        LocalDate eventDate = event.getStart().toLocalDate();
        eventsPerDay.merge(eventDate, 1, Integer::sum);
      }

      if (eventsPerDay.isEmpty()) {
        metrics.put("busiestDay", null); 
        metrics.put("leastBusyDay", null);
      } else {
        
        int maxEvents = eventsPerDay.values().stream().max(Integer::compareTo).orElse(0);
        int minEvents = eventsPerDay.values().stream().min(Integer::compareTo).orElse(0);

        
        List<LocalDate> busiestDays = eventsPerDay.entrySet().stream()
            .filter(entry -> entry.getValue() == maxEvents)
            .map(Map.Entry::getKey)
            .sorted() 
            .collect(Collectors.toList());
        metrics.put("busiestDay", busiestDays);

        
        List<LocalDate> leastBusyDays = eventsPerDay.entrySet().stream()
            .filter(entry -> entry.getValue() == minEvents)
            .map(Map.Entry::getKey)
            .sorted() 
            .collect(Collectors.toList());
        metrics.put("leastBusyDay", leastBusyDays);
      }

      
      long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
      double averageEventsPerDay = totalDays > 0 ? (double) totalEvents / totalDays : 0.0;
      metrics.put("averageEventsPerDay", averageEventsPerDay);

    } catch (CalendarException e) {
      System.out.println("Error: " + e.getMessage());
      
      metrics.put("totalEvents", 0);
      metrics.put("weekdayCount", new HashMap<>());
      metrics.put("eventNameCount", new HashMap<>());
      metrics.put("onlineEventsPercentage", 0.0);
      metrics.put("busiestDay", null);
      metrics.put("leastBusyDay", null);
      metrics.put("averageEventsPerDay", 0.0);
    }

    return metrics;
  }

  private List<InternalEvent> expandRecurringEvent(InternalEvent event, LocalDate startDate,
      LocalDate endDate) {
    if (!(event instanceof RecurringEvent)) {
      throw new IllegalArgumentException("Event must be a RecurringEvent");
    }
    RecurringEvent recurringEvent = (RecurringEvent) event;

    
    LocalDateTime rangeStart = startDate.atStartOfDay();
    LocalDateTime rangeEnd = endDate.atTime(23, 59, 59);
    Optional<List<InternalEvent>> occurrencesOpt = recurringEvent.getEventInRange(rangeStart,
        rangeEnd);

    return occurrencesOpt.orElse(new ArrayList<>());
  }


}
