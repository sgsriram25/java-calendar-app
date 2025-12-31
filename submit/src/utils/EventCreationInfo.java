package utils;

import java.time.LocalDateTime;

/**
 * Class representing the information needed to create an event. This will be used by the reader
 * to read the event information from a file. This is for the import functionality of the calendar.
 */
public class EventCreationInfo {
  public String subject;
  public LocalDateTime startTime;
  public LocalDateTime endTime;
  public String description;
  public String location;
  public boolean isPrivate;
}
