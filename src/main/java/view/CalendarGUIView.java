package view;

import controller.IFeatures;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import model.ReadOnlyEvent;

/**
 * A graphical user interface (GUI) view for a calendar application.
 *
 * <p>This view interacts with the user through buttons, menus, and dialogs,
 * then delegates control to the controller via callback methods. It provides feedback for both
 * successful and failed operations, and uses visual cues to guide users in providing valid input.
 */
public class CalendarGUIView extends JFrame implements IView {

  DateTimeFormatter dtFormatter;

  private JMenuItem createCalendarMenuItem;
  private JMenuItem editCalendarMenuItem;
  private JMenuItem showDashboardMenuItem;

  private JMenuItem createEventMenuItem;

  private JMenuItem importCalendarMenuItem;
  private JMenuItem exportCalendarMenuItem;


  private JButton prevMonthButton;
  private JButton nextMonthButton;
  private YearMonth currentMonth;
  private JLabel monthYearLabel;

  private JPanel calendarInfoPanel;
  private JLabel calendarNameLabel;
  private JLabel timezoneLabel;
  private JPanel dayGridPanel;
  private List<DayButton> dayButtons;
  private JPanel calendarGridPanel;

  private DefaultListModel<String> calendarListModel;
  private JList<String> calendarList;
  private IFeatures features;

  /**
   * Constructor for the CalendarGUIView. Sets up the size and calls initComponents to set up the
   * main components.
   */
  public CalendarGUIView() {
    super("Calendars");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    initComponents();
    setVisible(true);
  }

  /**
   * Displays the calendar metrics in the GUI.
   *
   * @param metrics A map containing various calendar metrics (total events, events by weekdays,
   *                etc.)
   */
  @Override
  public void displayMetrics(Map<String, Object> metrics) {
    System.out.println("Displaying metrics: " + metrics);
    SwingUtilities.invokeLater(() -> {
      JDialog metricsDialog = new JDialog(this, "Calendar Analytics "
          + "Dashboard", true);
      JPanel metricsPanel = new JPanel();
      metricsPanel.setLayout(new BoxLayout(metricsPanel, BoxLayout.Y_AXIS));
      metricsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      Object totalEventsObj = metrics.get("totalEvents");
      boolean noEvents = (totalEventsObj == null || (totalEventsObj instanceof Number
          && ((Number) totalEventsObj).intValue() == 0));

      metricsPanel.add(createLabel("Calendar Analytics Dashboard:"));
      if (noEvents) {
        metricsPanel.add(createLabel("No data to generate a dashboard"));
      }
      metricsPanel.add(Box.createVerticalStrut(10));
      metricsPanel.add(createLabel("Total number of events: "
          + (metrics.get("totalEvents") != null ? metrics.get("totalEvents") : "N/A")));
      metricsPanel.add(createLabel("Events by weekdays: "
          + (metrics.get("weekdayCount") != null ? metrics.get("weekdayCount") : "N/A")));
      metricsPanel.add(createLabel("Events by name: "
          + (metrics.get("eventNameCount") != null ? metrics.get("eventNameCount") : "N/A")));
      metricsPanel.add(createLabel("Online events percentage: "
          + (metrics.get("onlineEventsPercentage") != null ? metrics.get("onlineEventsPercentage")
              + "%" : "N/A")));
      metricsPanel.add(createLabel("Busiest day: "
          + (metrics.get("busiestDay") != null ? metrics.get("busiestDay") : "N/A")));
      metricsPanel.add(createLabel("Least busy day: "
          + (metrics.get("leastBusyDay") != null ? metrics.get("leastBusyDay") : "N/A")));
      metricsPanel.add(createLabel("Average events per day: "
          +  (metrics.get("averageEventsPerDay") != null ? metrics.get("averageEventsPerDay")
              : "N/A")));

      JScrollPane scrollPane = new JScrollPane(metricsPanel);
      metricsDialog.add(scrollPane);
      metricsDialog.pack();
      metricsDialog.setLocationRelativeTo(this);
      metricsDialog.setVisible(true);
    });
  }


  private JLabel createLabel(String text) {
    JLabel label = new JLabel(text);
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
    return label;
  }

  /**
   * Initializes the components of the GUI.
   */
  private void initComponents() {
    dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    setupMenuBar();
    setupTopPanel();
    setupCalendarSideList();
    setupCalendarGridPanel();
    setCalendarBackgroundColor("default");
  }

  /**
   * Sets up the menu bar with options for creating/editing calendars, creating events, and
   * importing/exporting calendars.
   */
  private void setupMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    JMenu calendarMenu = new JMenu("Calendar");
    createCalendarMenuItem = new JMenuItem("Create New Calendar");
    editCalendarMenuItem = new JMenuItem("Edit Calendar");
    showDashboardMenuItem = new JMenuItem("Show Dashboard");
    calendarMenu.add(createCalendarMenuItem);
    calendarMenu.add(editCalendarMenuItem);
    calendarMenu.add(showDashboardMenuItem);
    menuBar.add(calendarMenu);

    JMenu eventMenu = new JMenu("Event");
    createEventMenuItem = new JMenuItem("Create Event");
    eventMenu.add(createEventMenuItem);
    menuBar.add(eventMenu);

    JMenu fileMenu = new JMenu("File");
    importCalendarMenuItem = new JMenuItem("Import Calendar");
    exportCalendarMenuItem = new JMenuItem("Export Calendar");
    fileMenu.add(importCalendarMenuItem);
    fileMenu.add(exportCalendarMenuItem);
    menuBar.add(fileMenu);

    setJMenuBar(menuBar);
  }

  /**
   * Sets up the top panel of the GUI, which includes a toolbar for month navigation, and contains
   * key information about current calendar (name and timezone).
   */
  private void setupTopPanel() {

    JPanel toolbar = new JPanel(new FlowLayout());
    currentMonth = YearMonth.now();
    prevMonthButton = new JButton("<");
    prevMonthButton.setFocusable(false);
    nextMonthButton = new JButton(">");
    nextMonthButton.setFocusable(false);
    monthYearLabel = new JLabel(currentMonth.getMonth() + " " + currentMonth.getYear());
    toolbar.add(prevMonthButton);
    toolbar.add(monthYearLabel);
    toolbar.add(nextMonthButton);

    calendarInfoPanel = new JPanel();
    calendarInfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

    calendarNameLabel = new JLabel("📅 Active Calendar: default");
    calendarNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

    timezoneLabel = new JLabel("🕒 Timezone: America/Toronto");
    timezoneLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

    calendarInfoPanel.add(calendarNameLabel);
    calendarInfoPanel.add(Box.createHorizontalStrut(20));
    calendarInfoPanel.add(timezoneLabel);

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    topPanel.add(calendarInfoPanel);
    topPanel.add(toolbar);
    add(topPanel, BorderLayout.NORTH);
  }

  /**
   * Sets up the side list that displays the available calendars. Selected calendar (calendar in
   * use) is highlighted.
   */
  private void setupCalendarSideList() {
    JPanel calendarListPanel = new JPanel(new BorderLayout());
    calendarListPanel.setPreferredSize(new Dimension(200, 0));

    calendarListModel = new DefaultListModel<>();
    calendarList = new JList<>(calendarListModel);
    calendarList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(calendarList);

    JPanel labelBar = createCalendarListHeader();

    calendarListPanel.add(labelBar, BorderLayout.NORTH);
    calendarListPanel.add(scrollPane, BorderLayout.CENTER);

    add(calendarListPanel, BorderLayout.WEST);
  }

  /**
   * Creates the header for the calendar list panel, including buttons for creating and editing
   * calendars.
   *
   * @return the header panel for the calendar list
   */
  private JPanel createCalendarListHeader() {
    JLabel calendarListLabel = new JLabel("Calendars");
    calendarListLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
    calendarListLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

    JButton plusButton = new JButton("+");
    setupHeaderButton(plusButton, createCalendarMenuItem);

    JButton editButton = new JButton("✎");
    setupHeaderButton(editButton, editCalendarMenuItem);

    JPanel labelPanel = new JPanel(new BorderLayout());
    labelPanel.setBackground(Color.LIGHT_GRAY);
    labelPanel.setPreferredSize(new Dimension(300, 30));

    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    leftPanel.setOpaque(false);
    leftPanel.add(plusButton);

    JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
    centerPanel.setOpaque(false);
    centerPanel.add(calendarListLabel);

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
    rightPanel.setOpaque(false);
    rightPanel.add(editButton);

    labelPanel.add(leftPanel, BorderLayout.WEST);
    labelPanel.add(centerPanel, BorderLayout.CENTER);
    labelPanel.add(rightPanel, BorderLayout.EAST);

    return labelPanel;
  }

  /**
   * Sets up the calendar grid panel, which displays the days of the month in a grid format. It also
   * calls updateCalendarGrid to initialize the day buttons for current month.
   */
  private void setupCalendarGridPanel() {
    calendarGridPanel = new JPanel(new BorderLayout());
    JPanel calendarHeaderPanel = new JPanel(new GridLayout(1, 7));
    String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    Dimension cellSize = new Dimension(50, 50);
    for (String dayName : weekdays) {
      JLabel label = new JLabel(dayName, SwingConstants.CENTER);
      label.setOpaque(true);
      label.setBackground(Color.LIGHT_GRAY);
      label.setPreferredSize(cellSize);
      calendarHeaderPanel.add(label);
    }
    calendarGridPanel.add(calendarHeaderPanel, BorderLayout.NORTH);

    add(calendarGridPanel, BorderLayout.CENTER);
    updateCalendarGrid();
  }

  /**
   * Sets up the button for the header of the calendar list. The button is linked to a menu item
   * (because they have same effect) and has a specific font and cursor style.
   *
   * @param button         the button to set up
   * @param linkedMenuItem the menu item to link the button to
   */
  private void setupHeaderButton(JButton button, JMenuItem linkedMenuItem) {
    button.setFont(new Font("SansSerif", Font.BOLD, 18));
    button.setFocusable(false);
    button.setBorderPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.addActionListener(e -> linkedMenuItem.doClick());
  }

  /**
   * Registers the event listeners for the menu items and buttons in the GUI.
   */
  private void setupEventListeners() {
    setupCreateCalendarListener();
    setupEditCalendarListener();
    setupCalendarListListener();
    setupCreateEventListener();
    setupExportCalendarListener();
    setupImportCalendarListener();
    setupMonthSwitchListener();
    setupShowDashboardListener();
  }

  /**
   * Sets up the listener for the "Show Dashboard" menu item.
   */
  private void setupShowDashboardListener() {
    showDashboardMenuItem.addActionListener(e -> {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      JSpinner startDateSpinner = createDateSpinner(LocalDateTime.now());
      JSpinner endDateSpinner = createDateSpinner(LocalDateTime.now());

      panel.add(new JLabel("Select Start Date:"));
      panel.add(startDateSpinner);
      panel.add(new JLabel("Select End Date:"));
      panel.add(endDateSpinner);

      int result = JOptionPane.showConfirmDialog(this, panel,
          "Select Date Range for Dashboard",
          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

      if (result == JOptionPane.OK_OPTION) {
        LocalDate startDate = getDateFromSpinner(startDateSpinner);
        LocalDate endDate = getDateFromSpinner(endDateSpinner);
        if (startDate.isAfter(endDate)) {
          displayError("Start date must be before or equal to end date.");
          return;
        }
        System.out.println("Triggering showDashboard for range: " + startDate + " to " + endDate);
        features.showDashboard(startDate, endDate);
      }
    });
  }

  /**
   * Set up the month switch listener for the previous and next month buttons.
   */
  private void setupMonthSwitchListener() {
    prevMonthButton.addActionListener(e -> {
      currentMonth = currentMonth.minusMonths(1);
      monthYearLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
      updateCalendarGrid();
    });

    nextMonthButton.addActionListener(e -> {
      currentMonth = currentMonth.plusMonths(1);
      monthYearLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
      updateCalendarGrid();
    });
  }

  /**
   * Creates a date spinner initialized to the given {@link LocalDateTime} value. The spinner allows
   * selection of a date in the format "yyyy-MM-dd".
   *
   * @param value the initial date and time to set in the spinner
   * @return a {@link JSpinner} configured for selecting a date
   */
  private JSpinner createDateSpinner(LocalDateTime value) {
    JSpinner spinner = new JSpinner(
        new SpinnerDateModel(Date.from(value.atZone(ZoneId.systemDefault()).toInstant()), null,
            null, Calendar.DAY_OF_MONTH));
    spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
    return spinner;
  }

  /**
   * Creates a time spinner initialized to the given {@link LocalDateTime} value. The spinner allows
   * selection of a time in the format "HH:mm".
   *
   * @param value the initial time to set in the spinner
   * @return a {@link JSpinner} configured for selecting a time
   */
  private JSpinner createTimeSpinner(LocalDateTime value) {
    Date date = Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    JSpinner spinner = new JSpinner(new SpinnerDateModel(date, null, null,
        Calendar.MINUTE));
    spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
    return spinner;
  }

  /**
   * Creates a time spinner initialized to the current system time. The spinner allows selection of
   * a time in the format "HH:mm".
   *
   * @return a {@link JSpinner} configured for selecting a time
   */
  private JSpinner createTimeSpinner() {
    JSpinner spinner = new JSpinner(new SpinnerDateModel());
    spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
    return spinner;
  }

  /**
   * Extracts the selected date from a {@link JSpinner} and converts it to a {@link LocalDate}.
   *
   * @param spinner the spinner containing a date value
   * @return the selected {@link LocalDate}
   */
  private LocalDate getDateFromSpinner(JSpinner spinner) {
    Date date = (Date) spinner.getValue();
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  /**
   * Combines the date and time values from two {@link JSpinner} components into a
   * {@link LocalDateTime}.
   *
   * @param dateSpinner the spinner providing the date
   * @param timeSpinner the spinner providing the time
   * @return a combined {@link LocalDateTime} from the selected date and time
   */
  private LocalDateTime combineDateAndTime(JSpinner dateSpinner, JSpinner timeSpinner) {
    LocalDate date = getDateFromSpinner(dateSpinner);
    LocalTime time =
        ((Date) timeSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    return LocalDateTime.of(date, time);
  }

  /**
   * Handles the creation of a single event by prompting the user for details.
   *
   * @param current the current date and time
   * @return an {@link EventDetails} object containing the event details
   */
  private EventDetails handleCreateSingleEvent(LocalDateTime current) {
    int allDayChoice =
        JOptionPane.showConfirmDialog(this, "Do you want to create an "
                + "all-day event?",
            "All-Day Event", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (allDayChoice == JOptionPane.YES_OPTION) {
      return promptAllDayEventDetails(current);
    } else if (allDayChoice == JOptionPane.NO_OPTION) {
      return promptTimedEventDetails(current);
    } else {
      return null;
    }
  }

  /**
   * Prompts the user for details of a timed event, including name, start date, start time, end
   * date, and end time.
   *
   * @param current the current date and time
   * @return an {@link EventDetails} object containing the event details
   */
  private EventDetails promptTimedEventDetails(LocalDateTime current) {
    EventDetails details = new EventDetails();
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JTextField eventNameField = new JTextField(20);
    panel.add(new JLabel("Enter Event Name:"));
    panel.add(eventNameField);

    JSpinner startDateSpinner = createDateSpinner(current);
    JSpinner startTimeSpinner = createTimeSpinner();
    panel.add(new JLabel("Select Start Date:"));
    panel.add(startDateSpinner);
    panel.add(new JLabel("Select Start Time:"));
    panel.add(startTimeSpinner);

    JSpinner endDateSpinner = createDateSpinner(current);
    JSpinner endTimeSpinner = createTimeSpinner();
    panel.add(new JLabel("Select End Date:"));
    panel.add(endDateSpinner);
    panel.add(new JLabel("Select End Time:"));
    panel.add(endTimeSpinner);

    int result =
        JOptionPane.showConfirmDialog(this, panel, "Event Details",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String eventName = eventNameField.getText().trim();
      if (eventName.isEmpty()) {
        displayError("Event name cannot be empty.");
        return null;
      }

      LocalDateTime startDateTime = combineDateAndTime(startDateSpinner, startTimeSpinner);
      LocalDateTime endDateTime = combineDateAndTime(endDateSpinner, endTimeSpinner);

      details.eventName = eventName;
      details.start = startDateTime;
      details.end = endDateTime;
      return details;
    } else {
      return null;
    }
  }

  /**
   * Prompts the user for details of an all-day event, including name and date.
   *
   * @param current the current date and time
   * @return an {@link EventDetails} object containing the event details
   */
  private EventDetails promptAllDayEventDetails(LocalDateTime current) {
    EventDetails details = new EventDetails();
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JTextField eventNameField = new JTextField(20);
    panel.add(new JLabel("Enter Event Name:"));
    panel.add(eventNameField);

    JSpinner dateSpinner = createDateSpinner(current);
    panel.add(new JLabel("Select Date:"));
    panel.add(dateSpinner);

    int result = JOptionPane.showConfirmDialog(this, panel,
        "All-Day Event Details",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String eventName = eventNameField.getText().trim();
      if (eventName.isEmpty()) {
        displayError("Event name cannot be empty.");
        return null;
      }
      LocalDate date = getDateFromSpinner(dateSpinner);
      details.eventName = eventName;
      details.start = date.atStartOfDay();
      details.end = null;
      return details;
    } else {
      return null;
    }
  }

  /**
   * In addFeatures, we register all the event listeners for this view. The passed-in IFeatures
   * object provides the feature operations, which internally delegate to the controller.
   */
  @Override
  public void addFeatures(IFeatures features) {
    this.features = features;
    setupEventListeners();
  }

  /**
   * Sets up the action listener for the "Import Calendar" menu item. When triggered, opens a file
   * chooser for the user to select a CSV file and calls the {@code importCalendar} method from the
   * features class.
   */
  private void setupImportCalendarListener() {
    importCalendarMenuItem.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      int result = fileChooser.showOpenDialog(this);
      if (result == JFileChooser.APPROVE_OPTION) {
        features.importCalendar(fileChooser.getSelectedFile().getAbsolutePath());
      }
    });
  }

  /**
   * Sets up the action listener for the "Export Calendar" menu item. When triggered, opens a file
   * chooser for the user to select a save location, and calls the {@code exportCalendar} method
   * from the features class.
   */
  private void setupExportCalendarListener() {
    exportCalendarMenuItem.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      int result = fileChooser.showSaveDialog(this);
      if (result == JFileChooser.APPROVE_OPTION) {
        features.exportCalendar(fileChooser.getSelectedFile().getAbsolutePath());
      }
    });
  }

  /**
   * Sets up the action listener for the "Create Event" menu item. When triggered, opens the event
   * creation dialog for the current date and time.
   */
  private void setupCreateEventListener() {
    createEventMenuItem.addActionListener(e -> {
      createEvents(LocalDateTime.now());
    });
  }

  /**
   * Sets up a listener for the calendar list component. When a calendar is selected from the list,
   * this method extracts the calendar name (excluding timezone info) and activates it using the
   * {@code useCalendar} method.
   */
  private void setupCalendarListListener() {
    calendarList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        String selectedCalendarWithTZ = calendarList.getSelectedValue();
        if (selectedCalendarWithTZ != null) {
          int idx = selectedCalendarWithTZ.indexOf(" (");
          String selectedCalendar =
              (idx != -1) ? selectedCalendarWithTZ.substring(0, idx) : selectedCalendarWithTZ;
          features.useCalendar(selectedCalendar);
        }
      }
    });
  }

  /**
   * Sets up the action listener for the "Edit Calendar" menu item. When triggered, prompts the user
   * to enter the name of the calendar to edit. If the calendar exists, the user is then asked to
   * select a property ("name" or "timezone") to edit. Depending on the selected property, the user
   * is prompted to enter a new value. The calendar is updated accordingly using the
   * {@code editCalendar} feature method.
   */
  private void setupEditCalendarListener() {
    editCalendarMenuItem.addActionListener(e -> {
      String calendarName = JOptionPane.showInputDialog(this, "Enter Calendar Name to Edit:");
      if (calendarName == null) {
        return;
      } else if (calendarName.trim().isEmpty()) {
        displayError("Please input a valid Calendar Name.");
        return;
      }
      if (!features.detectCalendar(calendarName)) {
        return;
      }
      String[] properties = {"name", "timezone"};
      String property = (String) JOptionPane.showInputDialog(this,
          "Select property to edit:",
          "Edit Calendar Property", JOptionPane.QUESTION_MESSAGE, null, properties, properties[0]);

      if (property != null) {
        String newValue = null;
        if (property.equals("name")) {
          newValue = JOptionPane.showInputDialog(this, "Enter new name:");
        } else if (property.equals("timezone")) {
          Object[] timeZones = ZoneId.getAvailableZoneIds().toArray();
          newValue = (String) JOptionPane.showInputDialog(this,
              "Select new timezone:",
              "Timezone Selection", JOptionPane.QUESTION_MESSAGE, null, timeZones,
              ZoneId.systemDefault().getId());
        }
        if (newValue != null && !newValue.trim().isEmpty()) {
          features.editCalendar(calendarName, property, newValue);
        }
      }
    });
  }

  /**
   * Sets up the action listener for the "Create Calendar" menu item. When triggered, prompts the
   * user to enter a calendar name and select a timezone. If valid inputs are provided, it calls the
   * {@code createCalendar} method from the features class to create a new calendar with the
   * specified name and timezone.
   */
  private void setupCreateCalendarListener() {
    createCalendarMenuItem.addActionListener(e -> {
      String calendarName = JOptionPane.showInputDialog(this,
          "Enter Calendar Name:");
      if (calendarName != null && !calendarName.trim().isEmpty()) {
        Object[] timeZones = ZoneId.getAvailableZoneIds().toArray();
        String timezoneId =
            (String) JOptionPane.showInputDialog(this,
                "Select Timezone:", "Timezone Selection",
                JOptionPane.QUESTION_MESSAGE, null, timeZones, ZoneId.systemDefault().getId());
        if (timezoneId != null && !timezoneId.trim().isEmpty()) {
          ZoneId zone = ZoneId.of(timezoneId);
          features.createCalendar(calendarName, zone);
        }
      }
    });
  }

  /**
   * Prompts the user to create either a single or recurring event. Based on the user's choice,
   * delegates to the appropriate method.
   *
   * @param current the current LocalDateTime used as a reference when creating the event
   */
  private void createEvents(LocalDateTime current) {
    Object[] options = {"Single Event", "Recurring Event"};
    int choice = JOptionPane.showOptionDialog(this,
        "Select event type to create:", "Create Event",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

    if (choice == JOptionPane.YES_OPTION) {
      createSingleEvent(current);
    } else if (choice == JOptionPane.NO_OPTION) {
      createRecurringEvent(current);
    }
  }

  /**
   * Prompts the user for details and creates a single event.
   *
   * @param current the current LocalDateTime used to initialize default values
   */
  private void createSingleEvent(LocalDateTime current) {
    EventDetails eventDetails = handleCreateSingleEvent(current);
    if (eventDetails != null) {
      features.addSingleEvent(eventDetails.eventName, eventDetails.start, eventDetails.end);
    }
  }

  /**
   * Prompts the user for details and creates a recurring event. Allows selection of repeat days and
   * either an end date or a number of occurrences.
   *
   * @param current the current LocalDateTime used to initialize default values
   */
  private void createRecurringEvent(LocalDateTime current) {
    EventDetails eventDetails = handleCreateSingleEvent(current);
    if (eventDetails == null) {
      return;
    }

    List<Character> selectedDays = promptRecurringWeekdays();
    if (selectedDays == null || selectedDays.isEmpty()) {
      return;
    }

    Object[] recurrenceOptions = {"Set End Date", "Set Occurrences"};
    int recurrenceChoice = JOptionPane.showOptionDialog(this,
        "How do you want to set the end of this recurring event?",
        "Recurring Event Termination", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
        null, recurrenceOptions, recurrenceOptions[0]);

    if (recurrenceChoice == JOptionPane.YES_OPTION) {
      LocalDateTime endDate = promptRecurrenceEndDate();
      if (endDate != null) {
        features.addRecurringEventEndDate(eventDetails.eventName, eventDetails.start,
            eventDetails.end, selectedDays, endDate);
      }
    } else if (recurrenceChoice == JOptionPane.NO_OPTION) {
      Integer occurrences = promptRecurrenceOccurrences();
      if (occurrences != null) {
        features.addRecurringEventOccurrences(eventDetails.eventName, eventDetails.start,
            eventDetails.end, selectedDays, occurrences);
      }
    }
  }

  /**
   * Displays a dialog for the user to select the weekdays on which a recurring event should
   * repeat.
   */
  private List<Character> promptRecurringWeekdays() {
    JPanel weekdayPanel = new JPanel();
    weekdayPanel.setLayout(new BoxLayout(weekdayPanel, BoxLayout.Y_AXIS));
    weekdayPanel.add(new JLabel("Select the weekdays for recurrence:"));

    JCheckBox[] boxes =
        {new JCheckBox("Monday"), new JCheckBox("Tuesday"),
            new JCheckBox("Wednesday"),
            new JCheckBox("Thursday"), new JCheckBox("Friday"),
            new JCheckBox("Saturday"),
            new JCheckBox("Sunday")};
    char[] dayChars = {'M', 'T', 'W', 'R', 'F', 'S', 'U'};

    for (JCheckBox box : boxes) {
      weekdayPanel.add(box);
    }

    int result = JOptionPane.showConfirmDialog(this, weekdayPanel,
        "Select Recurring Weekdays",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result != JOptionPane.OK_OPTION) {
      return null;
    }

    List<Character> selected = new ArrayList<>();
    for (int i = 0; i < boxes.length; i++) {
      if (boxes[i].isSelected()) {
        selected.add(dayChars[i]);
      }
    }

    if (selected.isEmpty()) {
      JOptionPane.showMessageDialog(this, "No weekdays "
              + "selected for recurrence.", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
    return selected;
  }

  /**
   * Displays a date picker dialog for the user to select an end date for a recurring event.
   */
  private LocalDateTime promptRecurrenceEndDate() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("Select Recurrence End Date:"));
    JSpinner spinner = new JSpinner(new SpinnerDateModel());
    spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
    panel.add(spinner);

    int result = JOptionPane.showConfirmDialog(this, panel,
        "Recurring End Date",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result != JOptionPane.OK_OPTION) {
      return null;
    }

    Date date = (Date) spinner.getValue();
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
  }

  /**
   * Prompts the user to input the number of occurrences for a recurring event.
   */
  private Integer promptRecurrenceOccurrences() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("Enter number of occurrences:"));
    JTextField field = new JTextField(10);
    panel.add(field);

    int result = JOptionPane.showConfirmDialog(this, panel,
        "Recurring Occurrences",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result != JOptionPane.OK_OPTION) {
      return null;
    }

    try {
      return Integer.parseInt(field.getText().trim());
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Invalid number for "
              + "occurrences.", "Error",
          JOptionPane.ERROR_MESSAGE);
      return null;
    }
  }

  /**
   * Displays an informational message dialog to the user.
   *
   * @param message the message to display
   */
  @Override
  public void displayMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Displays an error dialog to the user.
   *
   * @param errorMessage the error message to display
   */
  @Override
  public void displayError(String errorMessage) {
    JOptionPane.showMessageDialog(this, errorMessage, "Error",
        JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Adds the new calendar to the calendar list in the sidebar. Displays a notifying message unless
   * the calendar is named "default".
   *
   * @param calendarName the name of the calendar that created
   * @param timeZone     the time zone associated with the calendar
   */
  @Override
  public void createCalendar(String calendarName, ZoneId timeZone) {
    calendarListModel.addElement(calendarName + " (" + timeZone.getId() + ")");
    if (!calendarName.equals("default")) {
      JOptionPane.showMessageDialog(this,
          "Calendar '" + calendarName + "' created successfully.\nTimezone: "
              + timeZone.getId(),
          "Create Calendar", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Switches the current calendar view to the specified calendar. Updates UI labels and highlights
   * the selected calendar in the list. Calls updateCalendarGrid to render the day Buttons and
   * reflect the events on each day. If the calendar is not found, shows an error dialog.
   *
   * @param calendarName the name of the calendar to activate
   */
  @Override
  public void useCalendar(String calendarName) {
    String fullCalendarEntry = null;
    for (int i = 0; i < calendarListModel.getSize(); i++) {
      String element = calendarListModel.getElementAt(i);
      if (element.startsWith(calendarName + " (")) {
        fullCalendarEntry = element;
        break;
      }
    }
    if (fullCalendarEntry == null) {
      JOptionPane.showMessageDialog(this, "Calendar '" +
              calendarName + "' not found.", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    String timeZone = fullCalendarEntry.substring(fullCalendarEntry.indexOf("(") + 1,
        fullCalendarEntry.indexOf(")"));
    calendarNameLabel.setText("📅 Active Calendar: " + calendarName);
    timezoneLabel.setText("🕒 Timezone: " + timeZone);
    setCalendarBackgroundColor(calendarName);
    updateCalendarGrid(calendarName);

    calendarList.setSelectedValue(fullCalendarEntry, true);
  }

  /**
   * Generates a light color based on the hash code of the calendar name. This method creates a soft
   * color by adjusting the saturation and brightness.
   *
   * @param name the name of the calendar
   * @return a Color object representing a light color
   */
  private Color generateLightColorFromName(String name) {
    int hash = name.hashCode();
    float hue = (hash & 0xFFFFFFF) % 360 / 360f;
    float saturation = 0.2f;
    float brightness = 0.95f;
    return Color.getHSBColor(hue, saturation, brightness);
  }

  /**
   * Sets the background color of the calendar info panel and the day grid panel based on the
   * calendar name.
   *
   * @param calendarName the name of the calendar
   */
  private void setCalendarBackgroundColor(String calendarName) {
    Color color = generateLightColorFromName(calendarName);
    calendarInfoPanel.setBackground(color);
    calendarInfoPanel.setOpaque(true);
    dayGridPanel.setBackground(color);
    dayGridPanel.setOpaque(true);
  }

  /**
   * This method is called to reflect the changes on view side after editing a calendar. It Updates
   * the calendar list model and the active calendar info panel.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     the property to edit (name or timezone)
   * @param newValue     the new value for the property
   */
  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    int index = -1;
    String currentEntry = null;
    for (int i = 0; i < calendarListModel.getSize(); i++) {
      String entry = calendarListModel.getElementAt(i);
      if (entry.startsWith(calendarName + " (")) {
        index = i;
        currentEntry = entry;
        break;
      }
    }

    if (index != -1) {
      int parenIndex = currentEntry.indexOf(" (");
      String nameOnly = (parenIndex != -1) ? currentEntry.substring(0, parenIndex) : currentEntry;
      String timezonePart = (parenIndex != -1) ? currentEntry.substring(parenIndex) : "";

      if (property.equalsIgnoreCase("name")) {
        nameOnly = newValue;
      } else if (property.equalsIgnoreCase("timezone")) {
        timezonePart = " (" + newValue + ")";
      }

      String newEntry = nameOnly + timezonePart;
      calendarListModel.set(index, newEntry);

      String selected = calendarList.getSelectedValue();
      if (selected != null && selected.startsWith(nameOnly + " (")) {
        calendarList.setSelectedValue(newEntry, true);
        calendarNameLabel.setText("📅 Active Calendar: " + nameOnly);
        timezoneLabel.setText("🕒 Timezone: " + newValue);
        setCalendarBackgroundColor(nameOnly);
        updateCalendarGrid(nameOnly);
      }
    }

    JOptionPane.showMessageDialog(this,
        "Calendar '" + calendarName + "' updated: " + property + " changed to " + newValue,
        "Edit Calendar", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Updates the calendar grid to display the days of the current month. This method is called when
   * the month is changed or the events have changed, but the calendar stays as the current
   * calendar.
   */
  private void updateCalendarGrid() {
    updateCalendarGrid(null);
  }

  /**
   * Updates the calendar grid to display the days of the current month. Effectively rerender the
   * dayButtons according to the events happening that day for that calendar (may not be the current
   * calendar!)
   */
  private void updateCalendarGrid(String calendarName) {
    if (dayButtons == null) {
      dayButtons = new ArrayList<>();
    } else {
      dayButtons.clear();
    }
    JPanel newGridPanel = new JPanel(new GridLayout(0, 7));

    LocalDate firstOfMonth = currentMonth.atDay(1);
    int firstDayValue = firstOfMonth.getDayOfWeek().getValue();
    int offset = firstDayValue % 7;

    for (int i = 0; i < offset; i++) {
      newGridPanel.add(new JLabel(""));
    }

    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      Optional<List<ReadOnlyEvent>> events;
      if (features == null) {
        events = Optional.empty();
      } else {
        if (calendarName == null) {
          events = features.onlyGetEvents(date);
        } else {
          events = features.onlyGetEvents(calendarName, date);
        }
      }
      DayButton dayButton =
          new DayButton(String.valueOf(day), events.orElse(new ArrayList<>()), date);
      dayButton.addActionListener(e -> features.showEvents(date));

      dayButtons.add(dayButton);
      newGridPanel.add(dayButton);
    }

    if (dayGridPanel != null) {
      calendarGridPanel.remove(dayGridPanel);
    }
    dayGridPanel = newGridPanel;
    dayGridPanel.setBackground(calendarInfoPanel.getBackground());
    dayGridPanel.setOpaque(true);
    calendarGridPanel.add(dayGridPanel, BorderLayout.CENTER);

    calendarGridPanel.revalidate();
    calendarGridPanel.repaint();
  }

  /**
   * Sets the events for each day button in the calendar grid.
   */
  private void setDayGridWithEvents() {
    for (DayButton button : dayButtons) {
      LocalDate date = currentMonth.atDay(Integer.parseInt(button.getText()));
      Optional<List<ReadOnlyEvent>> events = features.onlyGetEvents(date);
      button.setEvents(events.orElse(new ArrayList<>()));
    }
  }

  /**
   * Displays a message dialog to inform the user about the creation of a single event. The message
   * includes the event name, start and end times, and whether it is an all-day event.
   *
   * @param name        the name of the event
   * @param start       the start date and time of the event
   * @param end         the end date and time of the event (can be null for all-day events)
   * @param autoDecline whether to automatically decline conflicting events
   */
  @Override
  public void createSingle(String name, LocalDateTime start, LocalDateTime end,
      boolean autoDecline) {
    String message =
        (end != null) ? "Single event '" + name + "' created from " + start.format(dtFormatter)
            + " to " + end.format(dtFormatter)
            : "All-day event '" + name + "' created on " + start.format(dtFormatter);
    JOptionPane.showMessageDialog(this, message, "Create Single Event",
        JOptionPane.INFORMATION_MESSAGE);
    setDayGridWithEvents();
  }

  /**
   * Displays a message dialog to inform the user about the creation of a recurring event. The
   * message includes the event name, start and end times, and the days of the week on which it
   * occurs.
   *
   * @param name        the name of the event
   * @param start       the start date and time of the event
   * @param end         the end date and time of the event (can be null for all-day events)
   * @param daysOfWeek  a list of characters representing the days of the week (e.g., 'M' for
   *                    Monday)
   * @param occurrences the number of occurrences (if applicable)
   */
  @Override
  public void createRecurringOccurrences(String name, LocalDateTime start, LocalDateTime end,
      List<Character> daysOfWeek, int occurrences) {
    String readableDays = formatDaysOfWeek(daysOfWeek);

    String message =
        String.format("Recurring event '%s' created for %d times on: %s.", name, occurrences,
            readableDays);

    JOptionPane.showMessageDialog(this, message, "Create Recurring Event",
        JOptionPane.INFORMATION_MESSAGE);
    setDayGridWithEvents();
  }

  /**
   * Displays a message dialog to inform the user about the creation of a recurring event. The
   * message includes the event name, start and end times, and the days of the week on which it
   * occurs.
   *
   * @param name       the name of the event
   * @param start      the start date and time of the event
   * @param end        the end date and time of the event (can be null for all-day events)
   * @param daysOfWeek a list of characters representing the days of the week (e.g., 'M' for
   *                   Monday)
   * @param until      the end date and time for the recurring event
   */
  @Override
  public void createRecurringUntil(String name, LocalDateTime start, LocalDateTime end,
      List<Character> daysOfWeek, LocalDateTime until) {
    String readableDays = formatDaysOfWeek(daysOfWeek);

    String message =
        String.format("Recurring event '%s' created until %s on: %s.", name, until.toLocalDate(),
            readableDays);

    JOptionPane.showMessageDialog(this, message, "Create Recurring Event",
        JOptionPane.INFORMATION_MESSAGE);
    setDayGridWithEvents();
  }

  /**
   * Formats a list of characters representing days of the week into a human-readable string.
   *
   * @param daysOfWeek a list of characters representing the days of the week
   * @return a formatted string listing the names of the selected days
   */
  private String formatDaysOfWeek(List<Character> daysOfWeek) {
    Map<Character, String> dayNameMap =
        Map.of('M', "Monday", 'T', "Tuesday", 'W', "Wednesday", 'R',
            "Thursday", 'F', "Friday", 'S',
            "Saturday", 'U', "Sunday");

    return daysOfWeek.stream().map(dayNameMap::get).filter(Objects::nonNull).collect(
        Collectors.joining(", "));
  }

  /**
   * Displays a message dialog to inform the user about the editing of a single event. The message
   * includes the event name, start and end times, and the property that was changed.
   *
   * @param name     the name of the event
   * @param start    the start date and time of the event
   * @param end      the end date and time of the event (can be null for all-day events)
   * @param property the property that was changed
   * @param newValue the new value of the property
   */
  @Override
  public void editSingle(String name, LocalDateTime start, LocalDateTime end, String property,
      String newValue) {
    String message = "Event '" + name + "' updated: " + property + " changed to " + newValue;
    JOptionPane.showMessageDialog(this, message, "Edit Single Event",
        JOptionPane.INFORMATION_MESSAGE);
    setDayGridWithEvents();
  }

  /**
   * Displays a message dialog to inform the user about the editing of multiple events. The message
   * includes the event name, start date and time, and the property that was changed.
   *
   * @param name     the name of the event
   * @param start    the start date and time of the event
   * @param property the property that was changed
   * @param newValue the new value of the property
   */
  @Override
  public void editMultiple(String name, LocalDateTime start, String property, String newValue) {
    String message =
        "All events named '" + name + "' after " + start.format(dtFormatter) + " updated: "
            + property + " " + "changed to " + newValue;
    JOptionPane.showMessageDialog(this, message, "Edit Multiple Events",
        JOptionPane.INFORMATION_MESSAGE);
    setDayGridWithEvents();
  }

  /**
   * Displays a message dialog to inform the user about the editing of all events. The message
   * includes the event name and the property that was changed.
   *
   * @param name     the name of the event
   * @param property the property that was changed
   * @param newValue the new value of the property
   */
  @Override
  public void editAll(String name, String property, String newValue) {
    String message =
        "All events named '" + name + "' updated: " + property + " changed to " + newValue;
    JOptionPane.showMessageDialog(this, message, "Edit All Events",
        JOptionPane.INFORMATION_MESSAGE);
    setDayGridWithEvents();
  }

  /**
   * Displays a dialog showing all events scheduled on a given date. If there are no events on the
   * specified date, it shows a message indicating so. Otherwise, it renders the events in a
   * scrollable table with an option to edit each one. Includes a "Create Event" button to allow
   * users to add new events for that date.
   *
   * @param date   the date to display events for
   * @param events an optional list of events occurring on the given date
   */
  @Override
  public void printEventsOnDate(LocalDate date, Optional<List<ReadOnlyEvent>> events) {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    final JDialog[] dialogHolder = new JDialog[1];

    if (events.isEmpty() || events.get().isEmpty()) {
      panel.add(new JLabel("No events on " + date), BorderLayout.CENTER);
    } else {
      panel.add(new JScrollPane(createEventTablePanel(events.get(), dialogHolder)),
          BorderLayout.CENTER);
    }

    JPanel buttonPanel = new JPanel();
    JButton createButton = new JButton("Create Event");
    createButton.addActionListener(e -> {
      dialogHolder[0].dispose();
      createEvents(date.atStartOfDay());
    });
    buttonPanel.add(createButton);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JOptionPane optionPane =
        new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{}, null);
    dialogHolder[0] = optionPane.createDialog(this, "Events on " + date);
    dialogHolder[0].setModal(true);
    dialogHolder[0].setVisible(true);
  }

  /**
   * Creates a panel that displays a list of events in a table-like format. Each row includes event
   * details and an "Edit" button.
   *
   * @param eventList    the list of events to display
   * @param dialogHolder an array holding the parent dialog, used to close it when an edit occurs
   * @return a JPanel containing the formatted list of events
   */
  private JPanel createEventTablePanel(List<ReadOnlyEvent> eventList, JDialog[] dialogHolder) {
    JPanel eventListPanel = new JPanel();
    eventListPanel.setLayout(new BoxLayout(eventListPanel, BoxLayout.Y_AXIS));

    JPanel headerPanel = new JPanel(new GridLayout(1, 7));
    String[] headers = {"Name", "Start", "End", "Description", "Location", "Publicity", "Actions"};
    for (String header : headers) {
      JLabel label = new JLabel(header, SwingConstants.CENTER);
      label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      headerPanel.add(label);
    }
    eventListPanel.add(headerPanel);

    for (ReadOnlyEvent event : eventList) {
      eventListPanel.add(createEventRowPanel(event, dialogHolder));
    }

    return eventListPanel;
  }

  /**
   * Creates a row panel representing a single event. Each row includes labels for event
   * details and
   * a button to edit the event.
   *
   * @param event        the event to display
   * @param dialogHolder an array holding the parent dialog, used to close it when editing
   * @return a JPanel representing one row in the event list
   */
  private JPanel createEventRowPanel(ReadOnlyEvent event, JDialog[] dialogHolder) {
    JPanel rowPanel = new JPanel(new GridLayout(1, 7));

    JLabel nameLabel = createCenteredLabel(event.getName());
    JLabel startLabel = createCenteredLabel(event.getStart().format(dtFormatter));
    JLabel endLabel = createCenteredLabel(event.getEnd().format(dtFormatter));
    JLabel descriptionLabel = createCenteredLabel(event.getDescription());
    JLabel locationLabel = createCenteredLabel(event.getLocation());
    JLabel publicityLabel = createCenteredLabel(event.isPublic() ? "Public" : "Private");

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> {
      dialogHolder[0].dispose();
      showEditEventDialog(event);
    });

    rowPanel.add(nameLabel);
    rowPanel.add(startLabel);
    rowPanel.add(endLabel);
    rowPanel.add(descriptionLabel);
    rowPanel.add(locationLabel);
    rowPanel.add(publicityLabel);
    rowPanel.add(editButton);

    rowPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    return rowPanel;
  }

  /**
   * Creates a centered label with a border for consistent cell formatting in the event table.
   *
   * @param text the text to display in the label
   * @return a JLabel centered and bordered
   */
  private JLabel createCenteredLabel(String text) {
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    return label;
  }

  /**
   * Displays a dialog to edit the properties of a selected event. The user can modify properties
   * like subject, start time, end time, description, location, and publicity status.
   *
   * @param event the event to be edited
   */
  private void showEditEventDialog(ReadOnlyEvent event) {
    JDialog editDialog = new JDialog(this, "Edit Event", true);
    editDialog.setLayout(new BorderLayout(10, 10));

    String[] propertyNames = {"subject", "start", "end", "description", "location", "public"};
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String[] currentValues =
        {event.getName(), event.getStart().format(dtf), event.getEnd().format(dtf),
            event.getDescription(), event.getLocation(), String.valueOf(event.isPublic())};

    String[] updatedValues = Arrays.copyOf(currentValues, currentValues.length);
    List<String> updatedProperties = new ArrayList<>();
    JPanel tablePanel = new JPanel(new GridLayout(0, 3, 10, 10));

    for (int i = 0; i < propertyNames.length; i++) {
      String property = propertyNames[i];
      JLabel propertyLabel = new JLabel(property);
      JLabel valueLabel = new JLabel(currentValues[i]);
      JButton editButton = new JButton("Edit");

      final int index = i;
      editButton.addActionListener(
          e -> handleEditProperty(editDialog, event, property, currentValues[index],
              valueLabel,
              updatedValues, updatedProperties, index));

      tablePanel.add(propertyLabel);
      tablePanel.add(valueLabel);
      tablePanel.add(editButton);
    }

    JPanel paddedTablePanel = new JPanel(new BorderLayout());
    paddedTablePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
    paddedTablePanel.add(tablePanel, BorderLayout.CENTER);
    editDialog.add(paddedTablePanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);
    editDialog.add(buttonPanel, BorderLayout.SOUTH);

    saveButton.addActionListener(
        e -> processUpdateChoice(editDialog, event, propertyNames, currentValues,
            updatedValues,
            updatedProperties));
    cancelButton.addActionListener(e -> editDialog.dispose());

    editDialog.pack();
    editDialog.setLocationRelativeTo(this);
    editDialog.setVisible(true);
  }

  /**
   * Handles the editing of a specific property of an event. Displays appropriate input fields based
   * on the property type (date, time, checkbox, or text).
   *
   * @param editDialog        the dialog in which the edit is taking place
   * @param event             the event being edited
   * @param property          the property to be edited
   * @param currentValue      the current value of the property
   * @param valueLabel        the label displaying the current value
   * @param updatedValues     array to store updated values
   * @param updatedProperties list to track which properties have been updated
   * @param index             index of the property in the arrays
   */
  private void handleEditProperty(JDialog editDialog, ReadOnlyEvent event, String property,
      String currentValue, JLabel valueLabel, String[] updatedValues,
      List<String> updatedProperties, int index) {
    if (property.equalsIgnoreCase("start") ||
        property.equalsIgnoreCase("end")) {
      LocalDateTime originalValue = property.equals("start") ? event.getStart() : event.getEnd();

      JSpinner dateSpinner = createDateSpinner(originalValue);
      JSpinner timeSpinner = createTimeSpinner(originalValue);

      JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
      inputPanel.add(new JLabel("Date:"));
      inputPanel.add(dateSpinner);
      inputPanel.add(new JLabel("Time:"));
      inputPanel.add(timeSpinner);

      int result =
          JOptionPane.showConfirmDialog(editDialog, inputPanel, "Set value for " + property,
              JOptionPane.OK_CANCEL_OPTION);

      if (result == JOptionPane.OK_OPTION) {
        LocalDateTime newDateTime = combineDateAndTime(dateSpinner, timeSpinner);
        updatedValues[index] = newDateTime.toString();
        valueLabel.setText(newDateTime.toLocalDate() + " " + newDateTime.toLocalTime());
        updatedProperties.add(0, property);
      }

    } else if (property.equalsIgnoreCase("public")) {
      JCheckBox checkBox = new JCheckBox("Public", Boolean.parseBoolean(currentValue));
      int result = JOptionPane.showConfirmDialog(editDialog, checkBox, "Set public property:",
          JOptionPane.OK_CANCEL_OPTION);

      if (result == JOptionPane.OK_OPTION) {
        String newValue = Boolean.toString(checkBox.isSelected());
        updatedValues[index] = newValue;
        valueLabel.setText(newValue);
        updatedProperties.add(0, property);
      }

    } else {
      String newValue =
          JOptionPane.showInputDialog(editDialog, "Enter new value for " + property + ":",
              currentValue);
      if (newValue != null && !newValue.trim().isEmpty()) {
        updatedValues[index] = newValue.trim();
        valueLabel.setText(newValue.trim());
        updatedProperties.add(0, property);
      }
    }
  }

  /**
   * Processes the user's choice on how to apply the update to recurring events. Offers the options
   * to apply changes to a single event, this and all following events, or all events with the same
   * name.
   *
   * @param dialog            the dialog window to close after processing
   * @param event             the original event that was edited
   * @param propertyNames     the list of editable properties
   * @param currentValues     the original values before editing
   * @param updatedValues     the updated values after editing
   * @param updatedProperties the list of properties that were changed
   */
  private void processUpdateChoice(JDialog dialog, ReadOnlyEvent event, String[] propertyNames,
      String[] currentValues, String[] updatedValues,
      List<String> updatedProperties) {
    String eventName = currentValues[0];
    String message = "How many " + eventName + " do you want to be updated for this change?";
    String[] options =
        {"just this one", "this and following " + eventName + "(s)", "all " + eventName + "(s)"};

    int choice = JOptionPane.showOptionDialog(this, message,
        "Select Update Option",
        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

    switch (choice) {
      case 0:
        for (String property : updatedProperties) {
          features.editSingleEvent(event.getName(), event.getStart(), event.getEnd(), property,
              updatedValues[Arrays.asList(propertyNames).indexOf(property)]);
        }
        break;
      case 1:
        for (String property : updatedProperties) {
          features.editMultipleEventsFrom(eventName, event.getStart(), property,
              updatedValues[Arrays.asList(propertyNames).indexOf(property)]);
        }
        break;
      case 2:
        for (String property : updatedProperties) {
          features.editAllEvents(eventName, property,
              updatedValues[Arrays.asList(propertyNames).indexOf(property)]);
        }
        break;
      default:
        displayError("Update canceled.");
        break;
    }
    dialog.dispose();
  }

  /**
   * This is currently not used in the GUI view.
   */
  @Override
  public void printEventsInRange(LocalDateTime start, LocalDateTime end,
      Optional<List<ReadOnlyEvent>> events) {
    displayError("This shouldn't be called");
  }

  /**
   * Displays a message dialog to inform the user about the successful export of the calendar.
   *
   * @param filepath The file path where the calendar was exported.
   */
  @Override
  public void exportCalendar(String filepath) {
    JOptionPane.showMessageDialog(this, "Calendar exported "
            + "successfully to " + filepath, "Export",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Displays a message dialog to inform the user about the successful import of the calendar.
   *
   * @param filepath The file path from which the calendar was imported.
   */
  @Override
  public void importCalendar(String filepath) {
    JOptionPane.showMessageDialog(this, "Calendar imported "
            + "successfully from " + filepath, "Import",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * This is currently not used in the GUI view.
   */
  @Override
  public void showStatus(LocalDateTime dateTime, boolean isBusy) {
    displayError("This shouldn't be called");
  }

  /**
   * This is currently not used in the GUI view.
   */
  @Override
  public void copySingleEvent(String curCalendar, String eventName, LocalDateTime sourceDateTime,
      String targetCalendarName, LocalDateTime targetDateTime) {
    displayError("This shouldn't be called");
  }

  /**
   * This is currently not used in the GUI view.
   */
  @Override
  public void copyEventsOnDate(String curCalendar, LocalDate sourceDate, String targetCalendarName,
      LocalDate targetDate) {
    displayError("This shouldn't be called");
  }

  /**
   * This is currently not used in the GUI view.
   */
  @Override
  public void copyEventsBetweenDates(String curCalendar, LocalDate sourceStartDate,
      LocalDate sourceEndDate, String targetCalendarName,
      LocalDate targetStartDate) {
    displayMessage("This shouldn't be called");
  }

  /**
   * A custom JButton subclass to represent each day in the calendar grid. Use setEvents to adjust
   * the look based on the events for that day. Each horizontal bar represents an event happening on
   * that day. Its verticale length is proportional to the corresponding event's duration.
   */
  private static class DayButton extends JButton {

    Dimension cellSize;
    List<ReadOnlyEvent> events;
    LocalDate date;

    public DayButton(String label, List<ReadOnlyEvent> events, LocalDate date) {
      super(label);
      cellSize = new Dimension(50, 50);
      this.setPreferredSize(cellSize);
      setOpaque(true);
      this.events = events;
      this.date = date;
    }

    /**
     * Sets the events for this button and repaints it. This will change the look of the button if
     * the events aren't the same.
     *
     * @param events the list of events to set
     */
    public void setEvents(List<ReadOnlyEvent> events) {
      this.events = events;
      repaint();
    }

    /**
     * Paints the button and draws horizontal bars for each event.
     *
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      Graphics2D g2 = (Graphics2D) g;
      int width = getWidth();
      int height = getHeight();

      g2.setColor(new Color(255, 0, 0, 100));

      for (ReadOnlyEvent event : events) {
        float startFraction;
        if (event.getStart().isBefore(date.atStartOfDay())) {
          startFraction = 0;
        } else {
          startFraction = getFractionOfDay(event.getStart().toLocalTime());
        }
        float endFraction;
        if (event.getEnd().isAfter(date.atTime(23, 59))) {
          endFraction = 1;
        } else {
          endFraction = getFractionOfDay(event.getEnd().toLocalTime());
        }

        int yStart = (int) (height * startFraction);
        int yEnd = (int) (height * endFraction);
        int barHeight = Math.max(2, yEnd - yStart);

        g2.fillRect(2, yStart, width - 4, barHeight);
      }
    }

    private float getFractionOfDay(LocalTime time) {
      return (time.getHour() * 60 + time.getMinute()) / (24f * 60);
    }
  }

  /**
   * A simple class to hold event details for creating events. This is just to help with helper
   * methods passing around information.
   */
  public static class EventDetails {

    String eventName;
    LocalDateTime start;
    LocalDateTime end;
  }
}