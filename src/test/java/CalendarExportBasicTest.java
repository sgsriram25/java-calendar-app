import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import model.CalendarManager;
import model.ReadOnlyEvent;

/**
 * Test class for CalendarExportBasic.
 */
public class CalendarExportBasicTest {

  @Test
  public void testExportCalendarSuccess() throws Exception {
    // Create a CalendarManager and a new calendar
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("TestCal", ZoneId.of("America/New_York"));

    // Use the calendar (this might simply verify existence)
    manager.detectCalendar("TestCal");

    // Create an event in the calendar.
    LocalDateTime start = LocalDateTime.of(2024, 3, 11, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 3, 11, 11, 0);
    LocalDateTime start2 = LocalDateTime.of(2024, 3, 12, 10, 0);
    manager.createSingleEvent("TestCal", "Meeting", start, end);
    manager.createSingleEvent("TestCal", "Meeting2", start2, null);
    manager.createRecurringEvent("TestCal", "Meeting3", start2, null, List.of('M'), 5);

    // Export the calendar. The exportCalendar method returns an Optional<List<ReadOnlyEvent>>
    Optional<List<ReadOnlyEvent>> exportedOpt = manager.exportCalendar("TestCal");
    assertTrue("Exported calendar should be present", exportedOpt.isPresent());

    List<ReadOnlyEvent> exported = exportedOpt.get();
    // We expect one event in the exportCalendar.
    assertEquals("There should be one exported event", 7, exported.size());

    ReadOnlyEvent event = exported.get(0);
    // Use the getters from ReadOnlyEvent to check if the event's data is as expected.
    assertEquals("Meeting", event.getName());
    assertEquals(start, event.getStart());
    assertEquals(end, event.getEnd());
    // You can add further assertions to check description, location, and isPublic if needed.

    ReadOnlyEvent event2 = exported.get(1);
    // Use the getters from ReadOnlyEvent to check if the event's data is as expected.
    assertEquals("Meeting3", event2.getName());

  }

  @Test(expected = Exception.class)
  public void testExportCalendarNotFound() throws Exception {
    CalendarManager manager = new CalendarManager();
    // Attempt to exportCalendar a calendar that hasn't been created.
    manager.exportCalendar("NonExistentCal");
  }
}
