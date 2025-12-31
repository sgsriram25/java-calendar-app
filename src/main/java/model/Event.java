package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import model.exceptions.InvalidEditException;
import model.exceptions.InvalidEventException;

/**
 * Abstract class for events. Implements common functionalities of single event and recurring event.
 */
abstract class Event implements InternalEvent {

  private String name;
  private LocalDateTime start;
  private LocalDateTime end;
  private String description;
  private String location;
  private boolean isPublic;

  /**
   * Constructor for Event.
   * @param builder The builder object to construct the Event.
   */
  Event(Builder<?> builder) {
    if (builder.name == null || builder.start == null) {
      throw new IllegalArgumentException("Name, start or end time cannot be null");
    }
    if (builder.end == null) {
      builder.start = builder.start.toLocalDate().atStartOfDay();
      builder.end = builder.start.toLocalDate().atTime(23, 59, 59);
    }
    if (builder.end.isBefore(builder.start) || builder.end.isEqual(builder.start)) {
      throw new InvalidEventException("End time must be after start time");
    }
    this.name = builder.name;
    this.start = builder.start;
    this.end = builder.end;
    this.description = builder.description;
    this.location = builder.location;
    this.isPublic = builder.isPublic;
  }

  /*
   ***********************************************************************************************
   * OVERRIDDEN METHODS
   ***********************************************************************************************
   */

  /**
   * Returns the name of the event.
   * @return the name of the event
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the event.
   * @param name the name of the event
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the end time of the event.
   * @return the end time of the event
   */
  @Override
  public LocalDateTime getEnd() {
    return end;
  }

  /**
   * Sets the end time of the event.
   * @param end the end time of the event
   * @throws InvalidEditException if the end time is invalid
   */
  @Override
  public void setEnd(String end) throws InvalidEditException {
    LocalDateTime newEnd;
    try {
      newEnd = LocalDateTime.parse(end);
    } catch (Exception e) {
      throw new InvalidEditException(
          "Invalid end time format, must be in the format " + "yyyy-MM-dd'T'HH:mm" + e);
    }

    if (!newEnd.isAfter(start)) { 
      throw new InvalidEditException("End time must be after start time");
    }
    this.end = newEnd;
  }

  /**
   * Returns the start time of the event.
   * @return the start time of the event
   */
  @Override
  public LocalDateTime getStart() {
    return start;
  }

  /**
   * Sets the start time of the event.
   * @param start the start time of the event
   * @throws InvalidEditException if the start time is invalid
   */
  @Override
  public void setStart(String start) throws InvalidEditException {
    LocalDateTime newStart;
    try {
      newStart = LocalDateTime.parse(start);
    } catch (Exception e) {
      throw new InvalidEditException(
          "Invalid start time format, must be in the format " + "yyyy-MM-dd'T'HH:mm" + e);
    }

    if (newStart.isAfter(end) || newStart.isEqual(end)) {
      throw new InvalidEditException("Start time must be before end time");
    }
    this.start = newStart;
  }

  /**
   * Returns the description of the event.
   * @return the description of the event
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the event.
   * @param description the description of the event
   */
  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the location of the event.
   * @return the location of the event
   */
  @Override
  public String getLocation() {
    return location;
  }

  /**
   * Sets the location of the event.
   * @param location the location of the event
   */
  @Override
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Returns whether the event is public.
   * @return whether the event is public
   */
  @Override
  public boolean isPublic() {
    return isPublic;
  }

  /**
   * Sets whether the event is public.
   * @param isPublic whether the event is public
   * @throws InvalidEditException if the value is invalid
   */
  @Override
  public void setIsPublic(String isPublic) throws InvalidEditException {
    switch (isPublic.toLowerCase()) {
      case "true":
        this.isPublic = true;
        break;
      case "false":
        this.isPublic = false;
        break;
      default:
        throw new InvalidEditException("Invalid public value, must be true or false");
    }
  }

  /**
   * Returns whether the event is an all-day event.
   * @return whether the event is an all-day event
   */
  @Override
  public boolean isAllDay() {
    return this.start.equals(this.start.toLocalDate().atStartOfDay()) && this.end.toLocalTime()
        .equals(LocalTime.of(23, 59, 59));
  }

  /**
   * Shifts the event to start at the new start time.
   * @param newStart the new start time of the event
   */
  @Override
  public void shiftStart(LocalDateTime newStart) {
    Duration shift = Duration.between(this.start, newStart);
    this.start = this.start.plus(shift);
    this.end = this.end.plus(shift);
  }

  /**
   * Check if the event overlaps with the given interval.
   * @param start the start time of the interval
   * @param end the end time of the interval
   * @param intervalStart the start time of the event
   * @param intervalEnd the end time of the event
   * @return true if the event overlaps with the interval
   */
  @Override
  public boolean intervalOverlap(LocalDateTime start, LocalDateTime end,
                                 LocalDateTime intervalStart, LocalDateTime intervalEnd) {
    return start.isBefore(intervalEnd) && end.isAfter(intervalStart);
  }

  /**
   * Abstract builder class for InternalEvent. Every concrete class that extends InternalEvent
   * must define its own
   * builder.
   *
   * @param <T> The type of the builder.
   */
  abstract static class Builder<T extends Builder<T>> {

    String name;
    LocalDateTime start;
    LocalDateTime end;
    String description;
    String location;
    boolean isPublic;

    Builder(String name, LocalDateTime start) {
      this.name = name;
      this.start = start;
      this.description = "";
      this.location = "";
      this.isPublic = true;
    }

    /**
     * Sets the end time of the event.
     *
     * @param end The end time of the event.
     * @return The builder object after the end time has been set.
     */
    T end(LocalDateTime end) {
      this.end = end;
      return self();
    }

    /**
     * Sets the description of the event.
     *
     * @param description The description of the event.
     * @return The builder object after the description has been set.
     */
    T description(String description) {
      this.description = description;
      return self();
    }

    /**
     * Sets the location of the event.
     *
     * @param location The location of the event.
     * @return The builder object after the location has been set.
     */
    T location(String location) {
      this.location = location;
      return self();
    }

    /**
     * Sets whether the event is public.
     *
     * @param isPublic True if the event is public, otherwise false.
     * @return The builder object after the public status has been set.
     */
    T isPublic(boolean isPublic) {
      this.isPublic = isPublic;
      return self();
    }

    /**
     * Abstract method to return the builder object. Must be implemented by each concrete class that
     * extends this Builder.
     *
     * @return The builder object.
     */
    abstract T self();

    /**
     * Abstract method that builds the InternalEvent object. Must be implemented by each concrete
     * class that
     * extends this Builder.
     *
     * @return The InternalEvent object.
     */
    abstract InternalEvent build();
  }
}
