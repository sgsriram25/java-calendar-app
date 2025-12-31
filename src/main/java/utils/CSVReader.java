package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * CSVReader class for reading events from a CSV file and creating them in the calendar.
 */
public class CSVReader implements Reader {

  /**
   * Reads a CSV file and returns a list of EventCreationInfo objects.
   *
   * @param filePath the path to the CSV file
   * @return a list of EventCreationInfo objects
   * @throws Exception if an error occurs while reading the file
   */
  @Override
  public List<EventCreationInfo> read(String filePath) throws Exception {
    if (filePath == null || !filePath.endsWith(".csv")) {
      throw new IllegalArgumentException("Invalid filename. Must end with .csv");
    }

    List<EventCreationInfo> events = new ArrayList<>();
    String cvsSplitBy = ",";
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      br.readLine(); 
      String line;
      while ((line = br.readLine()) != null) {
        String[] tokens = line.split(cvsSplitBy, -1);

        String subject = tokens[0].trim();
        String startDateStr = tokens[1].trim();
        String startTimeStr = tokens[2].trim();
        String endDateStr = tokens[3].trim();
        String endTimeStr = tokens[4].trim();
        String description = tokens[6].trim();
        String location = tokens[7].trim();
        String privateStr = tokens[8].trim();

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse(startDateStr, dateFormatter),
            LocalTime.parse(startTimeStr.substring(0, 5), timeFormatter));

        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse(endDateStr, dateFormatter),
            LocalTime.parse(endTimeStr.substring(0, 5), timeFormatter));

        boolean isPrivate = Boolean.parseBoolean(privateStr);

        EventCreationInfo eventInfo = new EventCreationInfo();
        eventInfo.subject = subject;
        eventInfo.startTime = startDateTime;
        eventInfo.endTime = endDateTime;
        eventInfo.description = description;
        eventInfo.location = location;
        eventInfo.isPrivate = isPrivate;

        events.add(eventInfo);
      }
    }

    return events;
  }

}



