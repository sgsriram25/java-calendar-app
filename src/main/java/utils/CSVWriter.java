package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.ReadOnlyEvent;

/**
 * Utility class for writing data to a CSV file.
 */
public class CSVWriter implements Writer {
  /**
   * Writes the given data to a CSV file at the given file path.
   *
   * @param filePath The path to the CSV file.
   * @param events   The data to write to the CSV file.
   */
  public void write(String filePath, List<ReadOnlyEvent> events) {
    if (filePath == null || !filePath.endsWith(".csv")) {
      throw new IllegalArgumentException("Invalid filename. Must end with .csv");
    }
    List<String[]> eventsInFormat = new ArrayList<>();
    eventsInFormat.add(
        new String[]{"Subject", "Start Date", "Start Time", "End Date", "End Time", "All Day Event",
                     "Description", "Location", "Private"});
    for (ReadOnlyEvent event : events) {
      eventsInFormat.add(getEventInfo(event));
    }
    try {
      FileWriter writer = new FileWriter(filePath);
      for (String[] row : eventsInFormat) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
          line.append(row[i]);
          if (i < row.length - 1) {
            line.append(",");
          }
        }
        line.append("\n");
        writer.write(line.toString());
      }
      writer.close();
    } catch (IOException e) {
      System.err.println("Error writing to CSV file: " + e.getMessage());
    }
  }

  /**
   * Returns the event information in the format required for the CSV file.
   *
   * @param event The event to get the information from
   * @return The event information in the format required for the CSV file
   */
  private String[] getEventInfo(ReadOnlyEvent event) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    String[] eventData = new String[9];
    eventData[0] = event.getName();
    eventData[1] = event.getStart().toLocalDate().toString();
    eventData[2] = event.getStart().toLocalTime().format(formatter);
    eventData[3] = event.getEnd().toLocalDate().toString();
    eventData[4] = event.getEnd().toLocalTime().format(formatter);
    if (event.isAllDay()) {
      eventData[5] = "true";
    } else {
      eventData[5] = "false";
    }
    eventData[6] = event.getLocation();
    eventData[7] = event.getDescription();
    eventData[8] = Boolean.toString(!event.isPublic());
    return eventData;
  }
}
