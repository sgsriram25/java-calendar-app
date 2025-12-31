package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import controller.commands.CopyEventsBetweenDatesCommand;
import controller.commands.CopyEventsOnDateCommand;
import controller.commands.CopySingleEventCommand;
import controller.commands.CreateRecurringEventUntilCommand;
import controller.commands.CreateRecurringEventOccurrencesCommand;
import controller.commands.CreateSingleEventCommand;
import controller.commands.EditMultipleEventsFromDateCommand;
import controller.commands.EditSingleEventCommand;
import controller.commands.EditMultipleEventsCommand;
import controller.commands.ICommand;
import controller.commands.PrintEventsInRangeCommand;
import controller.commands.PrintEventsOnDateCommand;
import controller.commands.ExportCalendarCommand;
import controller.commands.ShowStatusCommand;
import controller.commands.EditCalendarCommand;

import controller.commands.CreateCalendarCommand;
import controller.commands.UseCalendarCommand;

/**
 * A factory class for creating Command instances based on the full command string.
 */
public class CommandFactory implements ICommandFactory {
  private final Map<String, Function<String[], ICommand>> commandMap;

  /**
   * Constructs a CommandFactory and initializes the command map.
   * The key is a string that contains the key word that identifies the command.
   * We are intentionally using a LinkedHashMap, and notice that more specific commands
   * are placed before more general commands. Using a LinkedHashMap ensures that the order
   * is preserved when iterating over the map.
   */
  public CommandFactory() {
    commandMap = new LinkedHashMap<>();
    commandMap.put("create event repeats until", CreateRecurringEventUntilCommand::new);
    commandMap.put("create event repeats for", CreateRecurringEventOccurrencesCommand::new);
    commandMap.put("create event", CreateSingleEventCommand::new);
    commandMap.put("edit event from to", EditSingleEventCommand::new);
    commandMap.put("edit events from", EditMultipleEventsFromDateCommand::new);
    commandMap.put("edit events", EditMultipleEventsCommand::new);
    commandMap.put("print events on", PrintEventsOnDateCommand::new);
    commandMap.put("print events from to", PrintEventsInRangeCommand::new);
    commandMap.put("exportCalendar cal", ExportCalendarCommand::new);
    commandMap.put("show status", ShowStatusCommand::new);

    commandMap.put("create calendar", CreateCalendarCommand::new);
    commandMap.put("edit calendar", EditCalendarCommand::new);
    commandMap.put("use calendar", UseCalendarCommand::new);
    commandMap.put("copy event", CopySingleEventCommand::new);
    commandMap.put("copy events on to", CopyEventsOnDateCommand::new);
    commandMap.put("copy events between and to", CopyEventsBetweenDatesCommand::new);
  }

  /**
   * Returns a Command based on the full command string.
   * It searches the commandMap to identify the full command (ignoring case)
   * and returns the Command created by the corresponding factory function.
   *
   * @param fullCommand The complete command string as entered by the user.
   * @return A Command instance corresponding to the matched key.
   * @throws IllegalArgumentException if no matching command is found.
   */
  public ICommand getCommand(String fullCommand) {
    String[] userCommand = tokenizeCommand(fullCommand);
    if (userCommand.length < 3) {
      throw new IllegalArgumentException(
          "Invalid command format, command is shorter than 3 words.");
    }
    
    for (Map.Entry<String, Function<String[], ICommand>> entry : commandMap.entrySet()) {
      String key = entry.getKey();
      String[] candidateCommand = key.split(" ");
      boolean commandFound = true;

      
      for (String token : candidateCommand) {
        boolean tokenFound = false;
        for (String arg : userCommand) {
          if (arg.equalsIgnoreCase(token)) {
            tokenFound = true;
            break;
          }
        }
        if (!tokenFound) {
          commandFound = false;
          break;
        }
      }

      
      
      if (commandFound) {
        return entry.getValue().apply(userCommand);
      }
    }
    throw new IllegalArgumentException("Unsupported command: " + fullCommand);
  }


  /**
   * Tokenizes the command string into an array of strings.
   * This method splits the command string by whitespace, but preserves quoted strings.
   *
   * @param command The command string to tokenize.
   * @return An array of strings representing the tokens in the command.
   */
  private String[] tokenizeCommand(String command) {
    List<String> tokens = new ArrayList<>();
    StringBuilder currentToken = new StringBuilder();
    boolean inQuotes = false;
    for (int i = 0; i < command.length(); i++) {
      char c = command.charAt(i);
      if (c == '"') {
        inQuotes = !inQuotes;
        continue;
      }
      if (Character.isWhitespace(c) && !inQuotes) {
        if (currentToken.length() > 0) {
          tokens.add(currentToken.toString());
          currentToken.setLength(0);
        }
      } else {
        currentToken.append(c);
      }
    }
    tokens.add(currentToken.toString());
    return tokens.toArray(new String[0]);
  }

}
