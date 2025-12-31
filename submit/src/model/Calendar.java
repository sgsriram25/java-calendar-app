package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.exceptions.ConflictingEventException;
import model.exceptions.InvalidEditException;
import model.exceptions.InvalidEventException;

import static utils.CommandParser.getDaysOfWeek;

/**
 * This class implements the ICalendar interface and is responsible for managing events within a
 * calendar. It supports the creation and modification of events, detects conflicts between
 * events, and provides methods for querying the calendar.
 */
class Calendar implements ICalendar {

  private final Map<String, List<InternalEvent>> eventMap;
  private ZoneId timezone;

  @Override
  public Map<String, List<InternalEvent>> getEvents() {
    return this.eventMap;
  }

  /**
   * Constructs an empty Calendar.
   */
  public Calendar(ZoneId timezone) {
    this.timezone = timezone;
    this.eventMap = new HashMap<>();
  }

  /**
   * Get the timezone of the calendar.
   *
   * @return the timezone of the calendar.
   */
  @Override
  public ZoneId getTimezone() {
    return timezone;
  }

  /**
   * Set the timezone of the calendar.
   *
   * @param newTimezone the new timezone to set.
   */
  @Override
  public void setTimezone(ZoneId newTimezone) {
    ZoneId oldTimezone = this.timezone;
    this.timezone = newTimezone;

    for (List<InternalEvent> events : eventMap.values()) {
      for (InternalEvent event : events) {
        ZonedDateTime oldZdt = event.getStart().atZone(oldTimezone);
        ZonedDateTime newZdt = oldZdt.withZoneSameInstant(newTimezone);
        LocalDateTime newLocalStart = newZdt.toLocalDateTime();
        event.shiftStart(newLocalStart);
      }
    }
  }

  /**
   * Adds an event to the calendar. If the event already exists, it will be added to the list of
   * events with the same name.
   *
   * @param name  The name of the event.
   * @param event The event to add.
   */
  private void add(String name, InternalEvent event) {
    if (!eventMap.containsKey(name)) {
      eventMap.put(name, new ArrayList<>());
    }
    eventMap.get(name).add(event);
  }

  /**
   * Adds a list of events to the calendar.
   *
   * @param events The list of events to add.
   */
  private void addAll(List<InternalEvent> events) {
    for (InternalEvent event : events) {
      add(event.getName(), event);
    }
  }

  /**
   * Removes an event from the calendar. If the event is the last one with that name, it will be
   * removed from the map.
   *
   * @param name  The name of the event.
   * @param event The event to remove.
   */
  private void remove(String name, InternalEvent event) {
    eventMap.get(name).remove(event);
    if (eventMap.get(name).isEmpty()) {
      eventMap.remove(name);
    }
  }

  /**
   * Removes a list of events from the calendar. If the event is the last one with that name, it
   * will
   * be removed from the map.
   *
   * @param events The list of events to remove.
   */
  private void removeAll(List<InternalEvent> events) {
    for (InternalEvent event : events) {
      remove(event.getName(), event);
    }
  }

  /**
   * Creates a single event with the given name, start, and end times.
   *
   * @param name  The name of the event.
   * @param start The start time of the event.
   * @param end   The end time of the event.
   * @throws InvalidEventException     If the event is invalid.
   * @throws ConflictingEventException If the event conflicts with an existing event.
   */
  @Override
  public void createSingleEvent(String name, LocalDateTime start, LocalDateTime end)
      throws InvalidEventException, ConflictingEventException {
    SingleEvent newEvent = new SingleEvent.SingleEventBuilder(name, start).end(end).build();
    if (isConflicting(newEvent)) {
      throw new ConflictingEventException("Event conflicts with existing event.");
    }
    add(newEvent.getName(), newEvent);
  }

  /**
   * Creates a single event that's a copy of an existing event but starts at newStart.
   *
   * @param event    the event to copy
   * @param newStart the new start time
   * @return the new event
   * @throws InvalidEventException     if the event is invalid
   * @throws ConflictingEventException if the event conflicts with an existing event
   */
  @Override
  public InternalEvent createSingleEventCopy(InternalEvent event, LocalDateTime newStart)
      throws InvalidEventException, ConflictingEventException {
    InternalEvent newEvent = event.copy();
    newEvent.shiftStart(newStart);
    if (isConflicting(newEvent)) {
      throw new ConflictingEventException("Event conflicts with existing event.");
    }
    add(newEvent.getName(), newEvent);
    return newEvent;
  }

  /**
   * Removes an event from the calendar.
   *
   * @param event The event to remove.
   */
  @Override
  public void removeEvent(InternalEvent event) {
    List<InternalEvent> events = eventMap.get(event.getName());
    events.remove(event);
    if (events.isEmpty()) {
      eventMap.remove(event.getName());
    }
  }

  /**
   * Creates a recurring event with the given name, start, end, days of the week, and number of
   * occurrences.
   *
   * @param name       The name of the event.
   * @param start      The start time of the event.
   * @param end        The end time of the event.
   * @param daysOfWeek The days of the week the event occurs.
   * @param numRepeats The number of times the event repeats.
   * @throws InvalidEventException     If the event is invalid.
   * @throws ConflictingEventException If the event conflicts with an existing event.
   */
  @Override
  public void createRecurringEvent(String name, LocalDateTime start, LocalDateTime end,
                                   List<Character> daysOfWeek, int numRepeats)
      throws InvalidEventException, ConflictingEventException {
    RecurringEvent newEvent =
        new RecurringEvent.RecurringEventBuilder(name, start).end(end).daysOfWeek(
            getDaysOfWeek(daysOfWeek)).numRepeats(numRepeats).build();
    if (isConflicting(newEvent)) {
      throw new ConflictingEventException("Event conflicts with existing event.");
    }
    add(newEvent.getName(), newEvent);
  }

  /**
   * Creates a new recurring event,specifying the date on which the event stops repeating. Recurring
   * events must start and end within the same day. The until parameter is inclusive, and only
   * checks the date. If time is provided in the until parameter, it will be ignored.
   *
   * @param name       The name of the event.
   * @param start      The start time of the event.
   * @param end        The end time of the event.
   * @param daysOfWeek The days of the week the event occurs.
   * @param until      The end date of the event (inclusive).
   * @throws InvalidEventException     If the event is invalid.
   * @throws ConflictingEventException If the event conflicts with an existing event.
   */
  @Override
  public void createRecurringEvent(String name, LocalDateTime start, LocalDateTime end,
                                   List<Character> daysOfWeek, LocalDateTime until)
      throws InvalidEventException, ConflictingEventException {
    RecurringEvent newEvent =
        new RecurringEvent.RecurringEventBuilder(name, start).end(end).daysOfWeek(
            getDaysOfWeek(daysOfWeek)).until(until).build();
    if (isConflicting(newEvent)) {
      throw new ConflictingEventException("Event conflicts with existing event.");
    }
    add(newEvent.getName(), newEvent);
  }

  /**
   * Checks if in the current calendar, we remove a bunch of events and add back another bunch of
   * events, will there be any conflicts. This method will be useful for edit operations, since
   * the way editions work on recurring events is to remove the old recurring event and add back a
   * series of new events representing the modified result.
   *
   * @param eventsToRemove the events that are being replaced.
   * @param eventsToAdd    the modified events.
   * @return true if any event in eventsToAdd conflicts with another candidate, false otherwise.
   */
  private boolean hasConflicts(List<InternalEvent> eventsToRemove,
                               List<InternalEvent> eventsToAdd) {
    List<InternalEvent> candidateEvents = new ArrayList<>();
    for (List<InternalEvent> events : eventMap.values()) {
      candidateEvents.addAll(events);
    }
    candidateEvents.removeAll(eventsToRemove);
    candidateEvents.addAll(eventsToAdd);

    for (InternalEvent newEvent : eventsToAdd) {
      for (InternalEvent candidate : candidateEvents) {
        if (candidate == newEvent) {
          continue; 
        }
        if (newEvent.isConflictingWith(candidate)) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * Edits a single event in the calendar. Since in A5 we don't allow concurrently happening events,
   * we can assume that the event to edit is unique.
   *
   * @param eventName    The name of the event to edit.
   * @param start        The start time of the event to edit.
   * @param end          The end time of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws InvalidEditException If the edit is invalid.
   */
  @Override
  public void editSingleEvent(String eventName, LocalDateTime start, LocalDateTime end,
                              String propertyName, String newValue) throws InvalidEditException {

    boolean editOccurred = false;
    
    
    
    List<InternalEvent> eventsToRemove = new ArrayList<>();
    List<InternalEvent> eventsToAdd = new ArrayList<>();

    
    for (InternalEvent event : eventMap.get(eventName)) {
      if (event.containsOccurrence(start, end)) {
        
        
        List<InternalEvent> modifiedEvents =
            event.applyOccurrenceModification(eventName, start, end, propertyName, newValue);
        eventsToRemove.add(event);
        eventsToAdd.addAll(modifiedEvents);
        editOccurred = true;
        break;
      }
    }

    if (!editOccurred) {
      throw new InvalidEditException("No such event exists");
    }

    
    if (hasConflicts(eventsToRemove, eventsToAdd)) {
      throw new InvalidEditException("Edit would cause conflict with existing event");
    }
    removeAll(eventsToRemove);
    addAll(eventsToAdd);
  }


  /**
   * Edits all events with the given name in the calendar. There could be multiple events with the
   * same name.
   *
   * @param eventName    The name of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws InvalidEditException If the edit is invalid.
   */
  @Override
  public void editMultipleEvents(String eventName, String propertyName, String newValue)
      throws InvalidEditException {
    boolean editOccurred = false;
    List<InternalEvent> eventsToRemove = new ArrayList<>();
    List<InternalEvent> eventsToAdd = new ArrayList<>();


    for (InternalEvent event : eventMap.get(eventName)) {
      
      InternalEvent modifiedEvent = event.copy();

      
      modifiedEvent.editProperty(propertyName, newValue);
      eventsToRemove.add(event);
      eventsToAdd.add(modifiedEvent);
      editOccurred = true;

    }


    if (!editOccurred) {
      throw new InvalidEditException("No event with name " + eventName);
    }

    
    if (hasConflicts(eventsToRemove, eventsToAdd)) {
      throw new InvalidEditException("Edit would cause conflict with existing event");
    }

    
    removeAll(eventsToRemove);
    addAll(eventsToAdd);
  }

  /**
   * Edits all events with the given name in the calendar that occur after the specified start time.
   *
   * @param eventName    The name of the event to edit.
   * @param start        The start time of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws InvalidEditException If the edit is invalid.
   */
  @Override
  public void editMultipleEvents(String eventName, LocalDateTime start, String propertyName,
                                 String newValue) throws InvalidEditException {

    boolean editOccurred = false;
    
    
    
    List<InternalEvent> eventsToRemove = new ArrayList<>();
    List<InternalEvent> eventsToAdd = new ArrayList<>();

    for (InternalEvent event : eventMap.get(eventName)) {
      if (event.containsOccurrenceAfter(start)) {
        
        
        List<InternalEvent> modifiedEvents =
            event.applySeriesModification(eventName, start, propertyName, newValue);

        eventsToRemove.add(event);
        eventsToAdd.addAll(modifiedEvents);
        editOccurred = true;
      }
    }


    if (!editOccurred) {
      throw new InvalidEditException("No such event exists");
    }
    
    if (hasConflicts(eventsToRemove, eventsToAdd)) {
      throw new InvalidEditException("Edit would cause conflict with existing event");
    }

    
    removeAll(eventsToRemove);
    addAll(eventsToAdd);

  }

  /**
   * Get all events on a given date.
   *
   * @param date The date of the event
   * @return A list of events on the given date
   */
  @Override
  public Optional<List<InternalEvent>> getEventsOnDate(LocalDate date) {
    List<InternalEvent> singleEventsOnDate = new ArrayList<>();
    for (List<InternalEvent> events : eventMap.values()) {
      for (InternalEvent event : events) {
        Optional<List<InternalEvent>> eventOnDate = event.getEventOnDate(date);
        eventOnDate.ifPresent(singleEventsOnDate::addAll);
      }
    }

    return Optional.of(singleEventsOnDate);
  }

  /**
   * Get all events on a given time.
   *
   * @param name     The name of the event
   * @param dateTime The time to check
   * @return The event that occurs at the given time
   */
  @Override
  public Optional<InternalEvent> getEventOnTime(String name, LocalDateTime dateTime) {
    for (InternalEvent event : eventMap.get(name)) {
      if (event.containsOccurrence(dateTime)) {
        return Optional.of(event);
      } else {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  /**
   * Get all events in a given date range.
   *
   * @param start The start of the date range.
   * @param end   The end of the date range.
   * @return A list of events in the given date range.
   */
  @Override
  public Optional<List<InternalEvent>> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    List<InternalEvent> eventsInRange = new ArrayList<>();
    for (List<InternalEvent> events : eventMap.values()) {
      for (InternalEvent event : events) {
        Optional<List<InternalEvent>> curEvents = event.getEventInRange(start, end);
        curEvents.ifPresent(eventsInRange::addAll);
      }
    }

    return Optional.of(eventsInRange);
  }

  /**
   * Get all events in the calendar.
   *
   * @return A list of all events in the calendar.
   */
  @Override
  public Optional<List<ReadOnlyEvent>> exportCalendar() {
    List<ReadOnlyEvent> flattenedEvents = new ArrayList<>();
    for (List<InternalEvent> events : eventMap.values()) {
      for (InternalEvent event : events) {
        flattenedEvents.addAll(event.flatten());
      }
    }
    return Optional.of(flattenedEvents);
  }

  /**
   * Check if the calendar is busy at a given time.
   *
   * @param dateTime The time to check.
   * @return True if the calendar is busy at the given time, false otherwise.
   */
  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    for (List<InternalEvent> events : eventMap.values()) {
      for (InternalEvent event : events) {
        if (event.isBusy(dateTime)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Check if the new event conflicts with any existing events.
   */
  private boolean isConflicting(InternalEvent newEvent) {
    for (List<InternalEvent> events : eventMap.values()) {
      for (InternalEvent existingEvent : events) {
        if (newEvent.isConflictingWith(existingEvent)) {
          return true;
        }
      }
    }
    return false;
  }
}
