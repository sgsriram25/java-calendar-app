package controller;

import java.util.Scanner;
import model.ICalendarManager;
import view.IView;


/**
 * Interactive mode controller handling user input via CLI.
 *
 * <p>This controller continuously listens for user input until the user explicitly types 'exit'.
 * It processes each command and handles exceptions gracefully, ensuring the program
 * does not terminate unexpectedly due to command errors.</p>
 */
public class InteractiveTextController extends AbstractTextController {
  private final Scanner scanner;

  /**
   * Constructs an InteractiveController with the given calendar model and view.
   *
   * @param manager The calendar model handling event logic.
   * @param view     The view used to display messages and errors.
   * @param in       The input stream to read user input from.
   */
  public InteractiveTextController(ICalendarManager manager, IView view, Readable in) {
    super(manager, view);
    this.scanner = new Scanner(in);
  }

  /**
   * Runs the interactive mode, continuously reading user input and processing commands.
   *
   * <p>If a user enters an invalid command or if a command execution fails, an error
   * message is displayed, but the program continues running. The program will only
   * terminate when the user explicitly types 'exit'.</p>
   */
  @Override
  public void run() {
    view.displayMessage("Interactive mode started. Type 'exit' to quit.");
    while (true) {
      view.displayMessage("Enter command:");
      String command = scanner.nextLine().trim();
      if (command.equalsIgnoreCase("exit")) {
        view.displayMessage("Exiting interactive mode.");
        break;
      }
      try {
        processCommand(command);
      } catch (Exception e) {
        view.displayError(e.getMessage());
      }
    }
  }

  @Override
  public void processCommand(String command) throws Exception {
    super.processCommand(command);


  }
}
