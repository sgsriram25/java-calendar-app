import controller.GUIController;
import controller.HeadlessTextController;
import controller.IController;
import controller.InteractiveTextController;
import java.io.InputStreamReader;
import model.CalendarManager;
import model.ICalendarManager;
import view.CalendarCLIView;
import view.CalendarGUIView;
import view.IView;

/**
 * Main class for the CalendarApp. Parses command line arguments and initializes the CalendarApp.
 * The CalendarApp can be run in interactive mode, headless mode, or GUI mode, specified by the
 * --mode flag. If no arguments are provided, the GUI is launched by default. Double-clicking the
 * jar file will also launch the GUI. Interactive mode allows the user to interact with the calendar
 * through the command line. Headless mode reads commands from a file and executes them without user
 * input. The commands file must be in the format of one command per line.
 */
public class CalendarApp {

  /**
   * Launches the GUI mode by initializing the manager, view, and controller for the GUI.
   */
  private static void launchGUI() {
    System.out.println("Launching Calendar Application in GUI Mode...");
    ICalendarManager manager = new CalendarManager();
    IView view = new CalendarGUIView();
    IController controller = new GUIController(manager, view);
    controller.run();
  }

  /**
   * Main method for the CalendarApp. Depending on the command-line arguments, this method either: -
   * Launches the GUI (no arguments or "--mode gui"), - Runs in interactive text mode (if "--mode
   * interactive" is specified), or - Runs in headless mode using a script file (if "--mode headless
   * path-of-script-file" is specified).
   *
   * <p>Usage:
   * java -jar Program.jar --mode headless path-of-script-file java -jar Program.jar --mode
   * interactive java -jar Program.jar --mode gui java -jar Program.jar  (opens the graphical user
   * interface)
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    if (args.length == 0) {

      launchGUI();
    } else if (args.length >= 2 && args[0].equalsIgnoreCase("--mode")) {

      ICalendarManager manager = new CalendarManager();
      IView view;
      IController controller = null;

      if (args[1].equalsIgnoreCase("gui")) {

        launchGUI();
      } else {

        view = new CalendarCLIView();

        if (args[1].equalsIgnoreCase("interactive")) {

          controller = new InteractiveTextController(
              manager, view, new InputStreamReader(System.in));
        } else if (args[1].equalsIgnoreCase("headless") && args.length == 3) {

          controller = new HeadlessTextController(manager, view, args[2]);
        } else {
          System.err.println(
              "Error: Invalid mode '" + args[1] + "'. Use '--mode interactive', '--mode "
                  + "headless path-of-script-file', or '--mode gui'.");
          System.exit(1);
        }
        controller.run();
      }
    } else {
      System.err.println("Usage:");
      System.err.println("  java -jar Program.jar --mode headless path-of-script-file");
      System.err.println("  java -jar Program.jar --mode interactive");
      System.err.println("  java -jar Program.jar --mode gui");
      System.err.println("  java -jar Program.jar   (opens the GUI)");
      System.exit(1);
    }
  }
}