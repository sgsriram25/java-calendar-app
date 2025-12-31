import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import model.CalendarManager;
import model.ReadOnlyEvent;

import org.junit.Before;
import org.junit.Test;

import utils.CSVWriter;

/**
 * Test class for CSVWriter.
 */
public class CSVWriterTest {

  private CalendarManager manager;
  private String calendarName;
  private String validFileName;
  private String invalidFileName;

  @Before
  public void setUp() {
    calendarName = "TestCal";
    validFileName = "validCalendar.csv";
    invalidFileName = "nonexistent_dir\\invalidCalendar.csv";

    // Create a CalendarManager, a new calendar with the system default time zone, and use it.
    manager = new CalendarManager();
    try {
      manager.createCalendar(calendarName, ZoneId.systemDefault());
      manager.detectCalendar(calendarName);
      manager.createSingleEvent(calendarName, "Event1", LocalDateTime.of(2020, 1, 1, 12, 0),
                                LocalDateTime.of(2020, 1, 1, 13, 0));
      // Create an all-day event by passing null as the end time
      manager.createSingleEvent(calendarName, "Event2", LocalDateTime.of(2020, 1, 2, 12, 0), null);
    } catch (Exception e) {
      fail("Failed to create events: " + e.getMessage());
    }
  }

  @Test
  public void testValidCSVWrite() throws Exception {
    // Retrieve events from the calendar via exportCalendar (this returns an
    // Optional<List<ReadOnlyEvent>>)
    Optional<List<ReadOnlyEvent>> exportOpt = manager.exportCalendar(calendarName);
    assertTrue("Exported events should be present", exportOpt.isPresent());
    List<ReadOnlyEvent> events = exportOpt.get();

    // Use CSVWriter to write the events to a valid file path.
    CSVWriter writer = new CSVWriter();
    String validFilePath = System.getProperty("user.dir") + "\\" + validFileName;
    writer.write(validFilePath, events);

    // Verify that the file was created and contains expected content.
    Path path = Paths.get(validFilePath);
    assertTrue("CSV file should exist", Files.exists(path));
    try {
      List<String> lines = Files.readAllLines(path);
      assertFalse("CSV file should not be empty", lines.isEmpty());
      // Verify that the first line is the header.
      String expectedHeader =
          "Subject,Start Date,Start Time,End Date,End Time,All Day Event,Description,Location,"
          + "Private";
      assertEquals(expectedHeader, lines.get(0));
    } catch (IOException e) {
      fail("IOException while reading the valid CSV file: " + e.getMessage());
    }
  }

  @Test
  public void testInvalidCSVWrite() throws Exception {
    // Retrieve events from the calendar via exportCalendar.
    Optional<List<ReadOnlyEvent>> exportOpt = manager.exportCalendar(calendarName);
    assertTrue("Exported events should be present", exportOpt.isPresent());
    List<ReadOnlyEvent> events = exportOpt.get();

    // Prepare an invalid file path (the directory "nonexistent_dir" does not exist).
    String invalidFilePath = System.getProperty("user.dir") + "\\" + invalidFileName;

    // Capture System.err output.
    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    PrintStream originalErr = System.err;
    System.setErr(new PrintStream(errContent));

    CSVWriter writer = new CSVWriter();
    writer.write(invalidFilePath, events);

    // Restore original System.err.
    System.setErr(originalErr);

    // Verify that an error message was printed to System.err.
    String errOutput = errContent.toString();
    assertTrue("Expected error message in System.err output",
               errOutput.contains("Error writing to CSV file:"));
  }
}
