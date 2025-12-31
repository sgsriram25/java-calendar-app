package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import model.ICalendarManager;
import view.IView;

/**
 * Headless mode controller handling commands from a file.
 * <p>
 * This controller reads commands sequentially from a given file and executes them.
 * The last command in the file must be 'exit', otherwise the program terminates with an error.
 * If a command fails to execute, the program terminates immediately, reporting the offending
 * command and the reason for failure.
 * </p>
 *
 * <p>Exceptions such as {@code IOException} may occur if the file is missing or unreadable.</p>
 */
public class HeadlessTextController extends AbstractTextController {
  private final String filePath;

  /**
   * Constructs a HeadlessController with the given calendar, view, and file path.
   *
   * @param manager  The calendar model to use.
   * @param view     The view to use for displaying output.
   * @param filePath The path to the file containing commands to process.
   */
  public HeadlessTextController(ICalendarManager manager, IView view, String filePath) {
    super(manager, view);
    this.filePath = filePath;
  }

  /**
   * Reads commands from a file and processes them. If last command is not "exit", it will make the
   * view display the error, and exit the program. If there is an error reading the file, it will
   * make the view display the error and exit, too. If any command is invalid, the processCommand
   * method called will throw an exception, which will be caught by the run method, and the program
   * will exit.
   */
  @Override
  public void run() {
    view.displayMessage("Running in headless mode. Reading commands from file: "
                        + filePath);
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String command;
      while ((command = reader.readLine())
             != null) {
        command = command.trim();
        if (command.isEmpty()) {
          
          continue;
        }
        if (command.equalsIgnoreCase("exit")) {
          view.displayMessage("Headless execution complete. Exiting.");
          return;
        }
        try {
          processCommand(command);
        } catch (Exception e) {
          view.displayError(e.getMessage());
          return;
        }
      }
      view.displayError("Last command in file must be 'exit'.");
    } catch (IOException e) {
      view.displayError("Error reading file: "
                        + e.getMessage());
    }
  }
}
