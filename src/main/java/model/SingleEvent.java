package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import model.exceptions.InvalidEditException;

/**
 * SingleEvent class represents an event that occurs only once. It extends the Event class and
 * implements the InternalEvent interface.
 */
class SingleEvent extends Event implements InternalEvent {

  /**
   * A private constructor for the SingleEvent class. This constructor is using the builder to
   * create a new SingleEvent.
   *
   * @param builder The builder object to construct the SingleEvent.
   */
  private SingleEvent(SingleEventBuilder builder) {
    super(builder);
  }

  /**
   * Uses a SingleEventBuilder object to build a new SingleEvent that's a copy of this event.
   *
   * @return A new SingleEvent that's a copy of this event.
   */
  @Override
  public InternalEvent copy() {
    return new SingleEvent(
        new SingleEventBuilder(this.getName(), this.getStart()).end(this.getEnd())
            .description(this.getDescription()).location(this.getLocation())
            .isPublic(this.isPublic()));
  }

  /**
   * Edits the property of the event with the given new value.
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
      default:
        throw new InvalidEditException(
            "Invalid property name, this event has properties: subject, start, end, "
            + "description, location, public");
    }
  }


  /**
   * Applies the occurrence modification to the event. We create a copy, modify the copy, and return
   * the copy.
   *
   * @param name         the name of the event to modify
   * @param start        the start time of the occurrence to be modified
   * @param end          the end time of the occurrence to be modified
   * @param propertyName the name of the property to change
   * @param newValue     the new value for the property
   * @return a list containing the modified event
   * @throws InvalidEditException if the property name is invalid
   */
  @Override
  public List<InternalEvent> applyOccurrenceModification(String name, LocalDateTime start,
                                                         LocalDateTime end, String propertyName,
                                                         String newValue)
      throws InvalidEditException {
    InternalEvent modifiedEvent = copy();
    modifiedEvent.editProperty(propertyName, newValue);
    return List.of(modifiedEvent);
  }

  /**
   * Applies the series modification to the event. For single event, this method is the same as
   * applyOccurrenceModification.
   *
   * @param name         the name of the event to modify
   * @param start        the start time after which the modification should be applied
   * @param propertyName the name of the property to change
   * @param newValue     the new value for the property
   * @return a list containing the modified event
   * @throws InvalidEditException if the property name is invalid
   */
  @Override
  public List<InternalEvent> applySeriesModification(String name, LocalDateTime start,
                                                     String propertyName, String newValue)
      throws InvalidEditException {
    InternalEvent modifiedEvent = copy();
    modifiedEvent.editProperty(propertyName, newValue);
    return List.of(modifiedEvent);
  }

  /**
   * Check if the event contains an occurrence with the given name and start and end. For single
   * event, this is just to check if the event is the occurrence.
   *
   * @param start the start time of the occurrence
   * @param end   the end time of the occurrence
   * @return true if the event is the occurrence, false otherwise
   */
  @Override
  public boolean containsOccurrence(LocalDateTime start, LocalDateTime end) {
    return start.equals(this.getStart()) && end.equals(
        this.getEnd());
  }

  /**
   * Check if the event contains an occurrence with the given name and start. For single event, this
   * is just to check if the event is the occurrence starting at the given start time.
   *
   * @param start the start time of the occurrence
   * @return true if the event is the described occurrence, false otherwise
   */
  @Override
  public boolean containsOccurrence(LocalDateTime start) {
    return start.equals(this.getStart());
  }


  /**
   * Check if the event contains an occurrence with the given name and start after the given time.
   * For single event, this is just to check if the event is starting after the given time.
   *
   * @param start the time threshold
   * @return true if the event contains the occurrence after the given time, false otherwise
   */
  @Override
  public boolean containsOccurrenceAfter(LocalDateTime start) {
    return !this.getStart().isBefore(start);
  }

  /**
   * Check if the event is on the given date. For a SingleEvent, we check if the date is between the
   * start and end date. If so we return the event, otherwise we return an empty optional.
   *
   * @param date the date to check
   * @return the event on the date, if it exists
   */
  @Override
  public Optional<List<InternalEvent>> getEventOnDate(LocalDate date) {
    
    
    LocalDate startDate = getStart().toLocalDate();
    LocalDate endDate = getEnd().toLocalDate();
    if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
      return Optional.of(List.of(this));
    }
    return Optional.empty();
  }


  /**
   * Get the event in the given range. For a SingleEvent, we check if the range overlaps with the
   * event. If so, we return the event, otherwise we return an empty optional.
   *
   * @param start the start time of the range
   * @param end   the end time of the range
   * @return the list of events in the range
   */
  @Override
  public Optional<List<InternalEvent>> getEventInRange(LocalDateTime start, LocalDateTime end) {
    if (intervalOverlap(this.getStart(), this.getEnd(), start, end)) {
      return Optional.of(List.of(this));
    }
    return Optional.empty();
  }

  /**
   * Check if the event is busy at the given time. For a SingleEvent, we check if the time is
   * between
   * the start and end time of the event.
   *
   * @param time the time to check
   * @return true if the event is happening at the time, false otherwise
   */
  @Override
  public boolean isBusy(LocalDateTime time) {
    return !time.isBefore(this.getStart()) && !time.isAfter(this.getEnd());
  }

  /**
   * Returns a string representation of the event including the event name, start time, followed by
   * any of the following properties. This method is accessible outside model package (since
   * ReadOnlyEvent interface is public), and could be used by the views to display the event
   * information.
   *
   * @return A string representation of the event.
   */
  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(this.getName() + "; ");
    if (this.isAllDay()) {
      stringBuilder.append(this.getStart().toLocalDate());
      stringBuilder.append(" All Day; ");
    } else {
      stringBuilder.append("From ").append(this.getStart()).append(" to ").append(this.getEnd())
          .append("; ");
    }
    if (this.isPublic()) {
      stringBuilder.append("Public; ");
    } else {
      stringBuilder.append("Private; ");
    }
    if (!this.getDescription().isEmpty()) {
      stringBuilder.append("Description: ").append(this.getDescription()).append("; ");
    }
    if (!this.getLocation().isEmpty()) {
      stringBuilder.append("Location: ").append(this.getLocation()).append("; ");
    }
    stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
    return stringBuilder.toString();
  }

  /**
   * Flattens the event into a list of internal events. For a SingleEvent, we return a list
   * containing only this event.
   *
   * @return the list of internal events
   */
  @Override
  public List<InternalEvent> flatten() {
    return List.of(this);
  }


  /**
   * Checks if this event is conflicting with another event.
   *
   * @param event The other event to check against.
   * @return True if the events are conflicting, otherwise false.
   */
  @Override
  public boolean isConflictingWith(InternalEvent event) {
    return event.isConflictingWithSingleEvent(this);
  }

  /**
   * Checks if this single event is conflicting with another single event.
   *
   * @param other The other event to check against, must be a SingleEvent.
   * @return True if the events are conflicting, otherwise false.
   */
  @Override
  public boolean isConflictingWithSingleEvent(SingleEvent other) {
    return intervalOverlap(this.getStart(), this.getEnd(), other.getStart(), other.getEnd());
  }

  /**
   * Checks if this single event is conflicting with a recurring event.
   *
   * @param other The other event to check against, must be a RecurringEvent.
   * @return True if the events are conflicting, otherwise false.
   */
  @Override
  public boolean isConflictingWithRecurringEvent(RecurringEvent other) {
    return other.isConflictingWithSingleEvent(this);
  }

  /**
   * Builder class for SingleEvent. This class extends the InternalEvent.Builder class and is
   * used to create a new SingleEvent.
   */
  static class SingleEventBuilder extends Event.Builder<SingleEventBuilder> {

    SingleEventBuilder(String name, LocalDateTime start) {
      super(name, start);
    }

    @Override
    SingleEventBuilder self() {
      return this;
    }

    @Override
    SingleEvent build() {
      return new SingleEvent(this);
    }
  }

}
