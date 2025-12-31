package utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.exceptions.InvalidEventException;

/**
 * Utility class for parsing user commands.
 */
public class CommandParser {

  /**
   * Parses a string into a LocalDateTime.
   *
   * @param dateTimeStr the string to parse.
   * @param errorMsg    additional error message context.
   * @return a LocalDateTime object parsed from the string.
   */
  public static LocalDateTime safeParseDateTime(String dateTimeStr, String errorMsg) {
    try {
      return LocalDateTime.parse(dateTimeStr);
    } catch (Exception e) {
      throw new IllegalArgumentException(errorMsg + ": " + e.getMessage(), e);
    }
  }

  /**
   * Parses a string as a LocalDate and appends time 00:00 to return a LocalDateTime.
   *
   * @param dateStr  the string to parse.
   * @param errorMsg additional error message context.
   * @return a LocalDateTime object with time set to 00:00.
   */
  public static LocalDate safeParseDate(String dateStr, String errorMsg) {
    try {
      return LocalDate.parse(dateStr);
    } catch (Exception e) {
      throw new IllegalArgumentException(errorMsg + ": " + e.getMessage(), e);
    }
  }

  /**
   * Converts a string of weekday characters to a list of DayOfWeek enums.
   *
   * @param days the string representing weekdays.
   * @return a list of DayOfWeek enums.
   */
  public static List<DayOfWeek> getDaysOfWeek(String days) {
    List<Character> daysChar = parseWeekdays(days);
    return getDaysOfWeek(daysChar);
  }

  /**
   * Converts a list of characters representing weekdays to a list of DayOfWeek enums.
   *
   * @param days the list of characters representing weekdays.
   * @return a list of DayOfWeek enums.
   */
  public static List<DayOfWeek> getDaysOfWeek(List<Character> days) {
    List<DayOfWeek> daysOfWeek = new ArrayList<>();
    for (Character day : days) {
      switch (day) {
        case 'M':
          daysOfWeek.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          daysOfWeek.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          daysOfWeek.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          daysOfWeek.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          daysOfWeek.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          daysOfWeek.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          daysOfWeek.add(DayOfWeek.SUNDAY);
          break;
        default:
          throw new InvalidEventException("Invalid day of week: " + day);
      }
    }
    return daysOfWeek;
  }

  /**
   * Validates and splits the weekdays token, accepting only M, T, W, R, F, S, U.
   *
   * @param weekdaysToken the token representing the weekdays.
   * @return a List of Characters representing the weekdays.
   */
  public static List<Character> parseWeekdays(String weekdaysToken) {
    if (weekdaysToken == null || weekdaysToken.isEmpty()) {
      throw new IllegalArgumentException("Weekdays token cannot be empty.");
    }
    List<Character> days = new ArrayList<>();
    for (int i = 0; i < weekdaysToken.length(); i++) {
      char c = weekdaysToken.charAt(i);
      if ("MTWRFSU".indexOf(c) == -1) {
        throw new IllegalArgumentException("Invalid weekday character: " + c);
      }
      days.add(c);
    }
    return days;
  }

  /**
   * Verifies that there are no extra tokens after the expected end.
   *
   * @param parts the token array.
   * @param index the current token index.
   */
  public static void ensureNoExtraTokens(String[] parts, int index) {
    if (index < parts.length) {
      throw new IllegalArgumentException("Unexpected extra tokens at the end of command.");
    }
  }
}
