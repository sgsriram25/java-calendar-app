package model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import model.exceptions.InvalidEventException;
import model.exceptions.InvalidEditException;
import utils.CommandParser;

/**
 * Class to represent a recurring event. A recurring event is an event that occurs on multiple days
 * of the week, for a specified number of times. If the Calender tries to create using until date,
 * the number of repeats will be calculated based on the given until date.
 */
class RecurringEvent extends Event implements InternalEvent {

  private List<DayOfWeek> daysOfWeek;
  private int repeat;

  /**
   * A private constructor for RecurringEvent. This constructor is used by the builder to create a
   * new RecurringEvent.
   *
   * @param builder The builder used to construct the RecurringEvent.
   */
  private RecurringEvent(RecurringEventBuilder builder) {
    super(builder);
    if (!this.getEnd().toLocalDate().equals(this.getStart().toLocalDate())) {
      throw new InvalidEventException("Start and end time must be on the same day");
    }
    if (builder.daysOfWeek == null || builder.daysOfWeek.isEmpty()) {
      throw new InvalidEventException("Days of the week cannot be null or empty");
    }
    if (builder.repeat == null) {
      throw new InvalidEventException("Number of repeats must be specified");
    }
    if (builder.repeat < 1) {
      throw new InvalidEventException("Number of repeats must be one or more");
    }
    this.daysOfWeek = builder.daysOfWeek;
    this.repeat = builder.repeat;
  }

  /**
   * Calculates the number of event occurrences between the start date and the until date
   * based on the specified days of the week.
   * For example, if an event starts on a Monday and recurs only on Mondays, Wednesdays, and
   * Fridays,
   * this method will count how many Mondays, Wednesdays, and Fridays occur between the start
   * date and the until date, inclusive.
   *
   * @param start      The start date and time.
   * @param until      The until date and time.
   * @param daysOfWeek The days of the week on which the event occurs.
   * @return The total number of occurrences (repeats) between start and until, inclusive.
   */
  private static int getRepeatFromUntil(LocalDateTime start, LocalDateTime until,
      List<DayOfWeek> daysOfWeek) {
    int count = 0;
    LocalDate currentDate = start.toLocalDate();
    LocalDate untilDate = until.toLocalDate();

    
    while (!currentDate.isAfter(untilDate)) {
      if (daysOfWeek.contains(currentDate.getDayOfWeek())) {
        count++;
      }
      currentDate = currentDate.plusDays(1);
    }
    return count;
  }

  /**
   * Shifts the start time of the first occurrence to the newStart time, and also shifts every
   * occurrence by the same duration. If the shift causes a change in the day of the week, change
   * the days of the week accordingly.
   *
   * @param newStart the new start time of the first occurrence
   */
  @Override
  public void shiftStart(LocalDateTime newStart) {
    LocalDateTime oldStart = this.getStart();

    
    super.shiftStart(newStart);

    
    int offsetDays = newStart.getDayOfWeek().getValue() - oldStart.getDayOfWeek().getValue();

    
    List<DayOfWeek> adjustedDays = new ArrayList<>();
    for (DayOfWeek day : this.daysOfWeek) {
      int newDayValue = Math.floorMod(day.getValue() - 1 + offsetDays, 7) + 1;
      adjustedDays.add(DayOfWeek.of(newDayValue));
    }
    this.daysOfWeek = adjustedDays;
  }

  /**
   * Creates a deep copy of the RecurringEvent.
   *
   * @return a deep copy of the RecurringEvent
   */
  @Override
  public InternalEvent copy() {
    return new RecurringEvent(
        new RecurringEventBuilder(this.getName(), this.getStart()).end(this.getEnd()).description(
            this.getDescription()).location(this.getLocation()).isPublic(
            this.isPublic()).daysOfWeek(this.daysOfWeek).numRepeats(this.repeat));
  }

  /**
   * Sets the days of the week the event occurs.
   *
   * @param days The days of the week the event occurs.
   */
  public void setDaysOfWeek(String days) throws InvalidEditException {
    this.daysOfWeek = CommandParser.getDaysOfWeek(days);
  }


  /**
   * Sets the date and time until the event repeats. Since we are only using occurrences to record
   * the event, we will calculate the number of repeats based on the until date.
   *
   * @param until The date and time until the event repeats.
   */
  public void setUntil(String until) throws InvalidEditException {
    LocalDateTime newUntil;
    try {
      newUntil = LocalDateTime.parse(until);
    } catch (Exception e) {
      throw new InvalidEditException("Invalid until time format, please use yyyy-mm-ddThh:mm:ss");
    }
    if (!newUntil.toLocalDate().isAfter(this.getEnd().toLocalDate())) {
      throw new InvalidEditException("Until date must be after event end date");
    }
    this.repeat = getRepeatFromUntil(this.getStart(), newUntil, this.daysOfWeek);
  }

  /**
   * Sets the number of times the event repeats.
   *
   * @param numRepeats The number of times the event repeats.
   */
  public void setNumRepeats(String numRepeats) throws InvalidEditException {
    int numOccurrences;
    try {
      numOccurrences = Integer.parseInt(numRepeats);
      if (numOccurrences <= 0) {
        throw new InvalidEditException("Number of repeats must be one or more");
      }
    } catch (NumberFormatException e) {
      throw new InvalidEditException("Invalid number of repeats");
    }
    this.repeat = numOccurrences;
  }

  /**
   * Sets the end time of all occurrences. The given end time must be on the same day with the
   * start time of the first occurrence of the event. And all occurrences' end time will be
   * adjusted.
   *
   * @param end the end time of first occurrence
   * @throws InvalidEditException if the end time is invalid
   */
  @Override
  public void setEnd(String end) throws InvalidEditException {
    LocalDateTime parsedEnd;
    try {
      parsedEnd = LocalDateTime.parse(end);
    } catch (Exception e) {
      throw new InvalidEditException("Invalid end time format, please use yyyy-MM-dd'T'HH:mm:ss");
    }
    if (!this.getStart().isBefore(parsedEnd)) {
      throw new InvalidEditException("End time must be after start time");
    }
    if (!parsedEnd.toLocalDate().equals(this.getStart().toLocalDate())) {
      throw new InvalidEditException("Start and end time must be on the same day");
    }
    super.setEnd(parsedEnd.toString());
  }


  /**
   * Edits the property of the event. The property name must be one of the properties defined in
   * the body.
   *
   * @param propertyName the name of the property
   * @param newValue     the new value of the property
   * @throws InvalidEditException if the property name is invalid
   */
  @Override
  public void editProperty(String propertyName, String newValue) throws InvalidEditException {
    switch (propertyName.toLowerCase()) {
      case "subject":
        this.setName(newValue);
        break;
      case "start":
        this.setStart(newValue);
        break;
      case "end":
        this.setEnd(newValue);
        break;
      case "description":
        this.setDescription(newValue);
        break;
      case "location":
        this.setLocation(newValue);
        break;
      case "public":
        this.setIsPublic(newValue);
        break;
      case "daysofweek":
        this.setDaysOfWeek(newValue);
        break;
      case "repeats":
        this.setNumRepeats(newValue);
        break;
      case "until":
        this.setUntil(newValue);
        break;
      default:
        throw new InvalidEditException(
            "Invalid property name, this event has properties: name, start, end, "
                + "description, location, public");
    }
  }


  /**
   * Helper to create a recurring event copy starting from startDate with a given number of repeats.
   * Here, the 'repeats' parameter represents the number of occurrences in this segment (a
   * positive integer).
   */
  private RecurringEvent createRecurringCopy(LocalDate startDate, int repeats) {
    boolean spansOvernight = !this.getEnd().toLocalTime().isAfter(this.getStart().toLocalTime());
    LocalDate endDate = spansOvernight ? startDate.plusDays(1) : startDate;

    return new RecurringEvent.RecurringEventBuilder(this.getName(),
        LocalDateTime.of(startDate, this.getStart().toLocalTime())).end(
        LocalDateTime.of(endDate, this.getEnd().toLocalTime())).description(
        this.getDescription()).location(this.getLocation()).isPublic(this.isPublic()).daysOfWeek(
        this.daysOfWeek).numRepeats(repeats).build();
  }

  /**
   * Applies a modification to a single occurrence of the event. The occurrence is identified by
   * the start time and end time of the occurrence. Current recurring event will not be changed,
   * copy of new recurring and single events are created and changes are applied there.
   *
   * @param eventName    the name of the event to modify
   * @param occStart     the start time of the occurrence to be modified
   * @param occEnd       the end time of the occurrence to be modified
   * @param propertyName the name of the property to change
   * @param newValue     the new value for the property
   * @return a list of event objects representing the modified results
   * @throws InvalidEditException if the recurring event doesn't contain the occurrence or the
   *                              edition is invalid
   */
  @Override
  public List<InternalEvent> applyOccurrenceModification(String eventName, LocalDateTime occStart,
      LocalDateTime occEnd, String propertyName,
      String newValue)
      throws InvalidEditException {

    List<LocalDate> allDates = getDates();
    LocalDate modifiedDate = occStart.toLocalDate();
    int index = allDates.indexOf(modifiedDate);
    if (index == -1) {
      throw new InvalidEditException("Occurrence not found in recurring event");
    }
    List<InternalEvent> newEvents = new ArrayList<>();

    
    SingleEvent modifiedOccurrence = createSingleCopy(modifiedDate);
    modifiedOccurrence.editProperty(propertyName, newValue);
    newEvents.add(modifiedOccurrence);

    
    if (index > 0) {
      
      newEvents.add(createRecurringCopy(allDates.get(0), index));
    }

    
    int afterRepeats = allDates.size() - index - 1;
    if (afterRepeats > 0) {
      newEvents.add(createRecurringCopy(allDates.get(index + 1), afterRepeats));
    }

    return newEvents;
  }


  /**
   * Applies a modification to the entire series of the event. The modification is applied to all
   * occurrences of the event starting from or after the given start time. Current recurring event
   * will not be changed, copy of new recurring events are created and changes are applied there.
   *
   * @param eventName    the name of the event to modify
   * @param start        the start time after which the modification should be applied
   * @param propertyName the name of the property to change
   * @param newValue     the new value for the property
   * @return a list of event objects representing the modified series
   * @throws InvalidEditException if the edition is invalid
   */
  @Override
  public List<InternalEvent> applySeriesModification(String eventName, LocalDateTime start,
      String propertyName, String newValue) throws InvalidEditException {
    
    if (!this.getName().equals(eventName)) {
      return new ArrayList<>(); 
    }

    List<LocalDate> allDates = getDates();
    
    LocalDateTime eventStart = this.getStart();
    LocalTime eventTime = eventStart.toLocalTime(); 

    
    int index = -1;
    for (int i = 0; i < allDates.size(); i++) {
      
      LocalDate occurrenceDate = allDates.get(i);
      LocalDateTime occurrenceDateTime = occurrenceDate.atTime(eventTime);

      
      if (!occurrenceDateTime.isBefore(start)) { 
        index = i;
        break;
      }
    }

    
    if (index == -1) {
      return new ArrayList<>();
    }

    List<InternalEvent> newEvents = new ArrayList<>();

    
    if (index > 0) {
      
      newEvents.add(createRecurringCopy(allDates.get(0), index));
    }

    
    int remainingRepeats = allDates.size() - index;
    RecurringEvent modifiedRecurring = createRecurringCopy(allDates.get(index), remainingRepeats);
    modifiedRecurring.editProperty(propertyName, newValue);
    newEvents.add(modifiedRecurring);

    return newEvents;
  }

  /**
   * Checks if the event contains an occurrence with the given name and start and end.
   *
   * @param start     the start time of the occurrence
   * @param end       the end time of the occurrence
   * @return true if the event contains the occurrence, false otherwise
   */
  @Override
  public boolean containsOccurrence(LocalDateTime start, LocalDateTime end) {
    if (!containsOccurrence(start)) {
      return false;
    }
    LocalDate occurrenceDate = start.toLocalDate();
    LocalDateTime expectedEnd = occurrenceDate.atTime(this.getEnd().toLocalTime());
    return expectedEnd.equals(end);
  }

  /**
   * Checks if the event contains an occurrence with the given start time.
   *
   * @param start the start time of the occurrence
   * @return true if the event contains the occurrence, false otherwise
   */
  @Override
  public boolean containsOccurrence(LocalDateTime start) {
    List<LocalDate> occurrenceDates = getDates();
    for (LocalDate date : occurrenceDates) {
      LocalDateTime candidateStart = date.atTime(this.getStart().toLocalTime());
      if (candidateStart.equals(start)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the event contains an occurrence with the given name and start after the given time.
   *
   * @param start     the time threshold
   * @return true if the event contains the occurrence after the given time, false otherwise
   */
  @Override
  public boolean containsOccurrenceAfter(LocalDateTime start) {
    List<LocalDate> occurrenceDates = getDates();
    for (LocalDate date : occurrenceDates) {
      LocalDateTime candidateStart = date.atTime(this.getStart().toLocalTime());
      if (!candidateStart.isBefore(start)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the occurrence on the given date and create a single event and return. If there is no
   * occurrence on the given date, return an empty optional.
   *
   * @param date the date to check
   * @return the event on the date, if it exists
   */
  @Override
  public Optional<List<InternalEvent>> getEventOnDate(LocalDate date) {
    List<InternalEvent> matches = new ArrayList<>();
    List<LocalDate> occurrenceDates = getDates();
    boolean spansOvernight = !this.getEnd().toLocalTime().isAfter(this.getStart().toLocalTime());

    for (LocalDate occurrenceDate : occurrenceDates) {
      if (occurrenceDate.equals(date)) {
        matches.add(createSingleCopy(occurrenceDate));
      } else if (spansOvernight && occurrenceDate.plusDays(1).equals(date)) {
        matches.add(createSingleCopy(occurrenceDate));
      }
    }

    return matches.isEmpty() ? Optional.empty() : Optional.of(matches);
  }

  private SingleEvent createSingleCopy(LocalDate startDate) {
    LocalDate endDate = !this.getEnd().toLocalTime().isAfter(this.getStart().toLocalTime()) ?
        startDate.plusDays(1) : startDate;

    return new SingleEvent.SingleEventBuilder(this.getName(),
        LocalDateTime.of(startDate, this.getStart().toLocalTime())).end(
        LocalDateTime.of(endDate, this.getEnd().toLocalTime())).description(
        this.getDescription()).location(this.getLocation()).isPublic(this.isPublic()).build();
  }


  /**
   * Get the event in the given range. If there is no occurrence in the range, return an empty
   * optional. If there are occurrences in the range, return a list of single events created.
   *
   * @param rangeStart the start time of the range
   * @param rangeEnd   the end time of the range
   * @return the list of events in the range
   */
  @Override
  public Optional<List<InternalEvent>> getEventInRange(LocalDateTime rangeStart,
      LocalDateTime rangeEnd) {
    List<InternalEvent> events = new ArrayList<>();
    Duration offset = Duration.between(this.getStart(), this.getEnd());
    for (LocalDate date : getDates()) {
      LocalDateTime occStart = LocalDateTime.of(date, this.getStart().toLocalTime());
      LocalDateTime occEnd = occStart.plus(offset);
      if (occEnd.isAfter(rangeStart) && occStart.isBefore(rangeEnd)) {
        SingleEvent occurrence =
            new SingleEvent.SingleEventBuilder(this.getName(), occStart).end(occEnd).description(
                this.getDescription()).location(this.getLocation()).isPublic(
                this.isPublic()).build();
        events.add(occurrence);
      }
    }
    return events.isEmpty() ? Optional.empty() : Optional.of(events);
  }

  /**
   * Check if any occurrence of the recurring event happens at the given time.
   *
   * @param time the time to check
   * @return true if any occurrence happens at the time, false otherwise
   */
  @Override
  public boolean isBusy(LocalDateTime time) {
    LocalDate date = time.toLocalDate();
    List<LocalDate> occurrenceDates = getDates();
    if (!occurrenceDates.contains(date)) {
      return false;
    }
    LocalDateTime occurrenceStart = LocalDateTime.of(date, this.getStart().toLocalTime());
    LocalDateTime occurrenceEnd = LocalDateTime.of(date, this.getEnd().toLocalTime());
    return !time.isBefore(occurrenceStart) && !time.isAfter(occurrenceEnd);
  }


  /**
   * Flatten the recurring event into a list of single events. Each single event represents an
   * occurrence of the recurring event.
   *
   * @return a list of internal events
   */
  @Override
  public List<InternalEvent> flatten() {
    List<InternalEvent> events = new ArrayList<>();
    Duration offset = Duration.between(this.getStart(), this.getEnd());
    for (LocalDate date : getDates()) {
      LocalDateTime occurrenceStart = LocalDateTime.of(date, this.getStart().toLocalTime());
      LocalDateTime occurrenceEnd = occurrenceStart.plus(offset);
      SingleEvent event = new SingleEvent.SingleEventBuilder(this.getName(), occurrenceStart).end(
          occurrenceEnd).description(this.getDescription()).location(this.getLocation()).isPublic(
          this.isPublic()).build();
      events.add(event);
    }
    return events;
  }


  /**
   * Check if the recurring event is conflicting with another event. The recurring event is
   * conflicting with another event if any of its occurrences is conflicting with the other event.
   *
   * @param other the other event to check
   * @return true if the recurring event is conflicting with the other event, false otherwise
   */
  @Override
  public boolean isConflictingWith(InternalEvent other) {
    return other.isConflictingWithRecurringEvent(this);
  }


  /**
   * Check if the recurring event is conflicting with a single event. The recurring event is
   * conflicting with a single event if any of its occurrences is conflicting with the single
   * event.
   *
   * @param other the single event to check
   * @return true if the recurring event is conflicting with the single event, false otherwise
   */
  @Override
  public boolean isConflictingWithSingleEvent(SingleEvent other) {
    Optional<List<InternalEvent>> occurrencesOpt =
        getEventInRange(other.getStart(), other.getEnd());
    return occurrencesOpt.isPresent() && !occurrencesOpt.get().isEmpty();
  }

  /**
   * Check if the recurring event is conflicting with another recurring event. The recurring event
   * is conflicting with another recurring event if any of their occurrences overlap.
   *
   * @param other the other recurring event to check
   * @return true if the recurring event is conflicting with the other recurring event
   */
  @Override
  public boolean isConflictingWithRecurringEvent(RecurringEvent other) {
    
    LocalTime thisStart = this.getStart().toLocalTime();
    LocalTime thisEnd = this.getEnd().toLocalTime();
    LocalTime otherStart = other.getStart().toLocalTime();
    LocalTime otherEnd = other.getEnd().toLocalTime();
    if (!(thisStart.isBefore(otherEnd) && otherStart.isBefore(thisEnd))) {
      return false;
    }

    Set<LocalDate> thisDates = new HashSet<>(this.getDates());
    thisDates.retainAll(other.getDates());

    return !thisDates.isEmpty();
  }

  /**
   * Get the dates of the occurrences of the recurring event. Representing the dates events
   * could start.
   *
   * @return a list of dates of the occurrences
   */
  private List<LocalDate> getDates() {
    List<LocalDate> dates = new ArrayList<>();
    LocalDate current = this.getStart().toLocalDate();
    int count = 0;
    while (count < repeat) {
      if (daysOfWeek.contains(current.getDayOfWeek())) {
        dates.add(current);
        count++;
      }
      current = current.plusDays(1);
    }
    return dates;
  }


  /**
   * Builder class for RecurringEvent. Extends the builder class from InternalEvent. Allows users to
   * define
   * the days of the week the event occurs and the number of times the event repeats. Days of week
   * must be defined before the number of repeats, if used.
   */
  static class RecurringEventBuilder extends Event.Builder<RecurringEventBuilder> {

    List<DayOfWeek> daysOfWeek;
    Integer repeat;

    RecurringEventBuilder(String name, LocalDateTime start) {
      super(name, start);
      this.daysOfWeek = new ArrayList<>();
    }

    @Override
    RecurringEventBuilder end(LocalDateTime end) {
      super.end(end);
      return this;
    }

    RecurringEventBuilder daysOfWeek(List<DayOfWeek> daysOfWeek) {
      if (daysOfWeek == null || daysOfWeek.isEmpty()) {
        throw new InvalidEventException("Days of week cannot be null or empty");
      }
      this.daysOfWeek = daysOfWeek;
      return this;
    }

    RecurringEventBuilder numRepeats(int numRepeats) {
      if (numRepeats < 1) {
        throw new InvalidEventException("Number of repeats must be one or more");
      }
      this.repeat = numRepeats;
      return this;
    }

    RecurringEventBuilder until(LocalDateTime until) {
      if (until == null) {
        throw new InvalidEventException("Until date cannot be null");
      }
      if (until.toLocalDate().isBefore(this.start.toLocalDate())) {
        throw new InvalidEventException("Until date must be after event end date");
      }
      this.repeat = getRepeatFromUntil(start, until, daysOfWeek);
      return this;
    }

    @Override
    RecurringEventBuilder self() {
      return this;
    }

    @Override
    RecurringEvent build() {
      return new RecurringEvent(this);
    }
  }
}

