
//Packages and Imports

package calendar.ui.addevent;

import calendar.data.model.MyCalendar;
import calendar.database.DBHandler;
import calendar.ui.main.Controller;
import com.jfoenix.controls.*;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddEventController implements Initializable {

    // Controllers
    private Controller mainController;


    //--------------------------------------------------------------------
    //---------Database Object -------------------------------------------
    DBHandler databaseHandler;
    //--------------------------------------------------------------------


    //Set main controller
    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    // Structure
    @FXML
    private Label topLabel;
    @FXML
    private AnchorPane rootPane;

    // Text fields
    @FXML
    private JFXTextField subject;
    @FXML
    private JFXTextArea comment;

    @FXML
    private JFXComboBox<String> categorieSelect;

    // Buttons
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;

    // Date and Time Picker
    @FXML
    private JFXTimePicker startTime;
    @FXML
    private JFXDatePicker startDate;

    @FXML
    private JFXTimePicker endTime;
    @FXML
    private JFXDatePicker endDate;

    // These fields are for mouse dragging of window
    private double xOffset;
    private double yOffset;

    @FXML
    void exit(MouseEvent event) {
        // Close the window
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void cancel(MouseEvent event) {
        // Close the window
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    //Function that inserts a new event in the database
    @FXML
    void save(MouseEvent event) {

        // Get the calendar name
        String calendarName = MyCalendar.getInstance().calendar_name;

        // Define Date format '2011-12-03'
        DateTimeFormatter myFormat = DateTimeFormatter.ISO_LOCAL_DATE;
        // Define Time format '10:15:30'
        DateTimeFormatter myTimeFormat = DateTimeFormatter.ISO_LOCAL_TIME;


        //Check if the user inputted information in all required fields!
        if (subject.getText().isEmpty() || categorieSelect.getSelectionModel().isEmpty()
                || startDate.getValue() == null || startTime.getValue() == null
                || endDate.getValue() == null || endTime.getValue() == null) {
            showMessage(Alert.AlertType.ERROR, "Please fill out all fields");
            return;
        } else if (startDate.getValue().isAfter(endDate.getValue())) {
            showMessage(Alert.AlertType.ERROR, "The start date can never be after the end date");
            return;
        } else if (startDate.getValue().equals(endDate.getValue())) {

            if (startTime.getValue().isAfter(endTime.getValue())) {
                showMessage(Alert.AlertType.ERROR, "The start time can not be after the end time if both are on the same date");
                return;
            }
        }


        //Check if the event descritption contains the character ~ because it cannot contain it due to database and filtering issues
        else if (subject.getText().contains("~") || comment.getText().contains("~")) {
            //Show message indicating that the event description cannot contain the character ~
            showMessage(Alert.AlertType.WARNING, "Event Description cannot contain the character ~");
            return;
        }

        //If all data is inputted correctly and validated, then add the event:

        // Get values from the DatePickers and TimePickers
        String sDate = startDate.getValue().format(myFormat);
        String sTime = startTime.getValue().format(myTimeFormat);

        String eDate = endDate.getValue().format(myFormat);
        String eTime = endTime.getValue().format(myTimeFormat);

        // Subject and comment for the event
        String eventSubject = subject.getText();
        String eventComment = comment.getText();

        // Get term that was selected by the user
        String categorie = categorieSelect.getValue();

        // variable that holds the ID value of the categorie selected by the user. It set to 0 becasue no selection has been made yet
        int chosenCategorieID = 0;

        // Get the ID of the selected categorie from the database based on the selected categirie's name
        chosenCategorieID = databaseHandler.getCategorieID(categorie);

        //---------------------------------------------------------
        //Insert new event into the EVENTS table in the database und create new Event instance

        //Query to get ID for the selected Term
        String insertQuery = "INSERT INTO EVENTS(EventDescription,CategorieID,CalendarName,EventStartDate,EventStartTime,EventEndDate,EventEndTime,EventComment) " +
                "VALUES ("
                + "'" + eventSubject + "', "
                + chosenCategorieID + ", "
                + "'" + calendarName + "', "
                + "'" + sDate + "', "
                + "'" + sTime + "', "
                + "'" + eDate + "', "
                + "'" + eTime + "', "
                + "'" + eventComment + "'"
                + ")";


        //Check if insertion into database was successful, and show message either if it was or not
        if (databaseHandler.executeAction(insertQuery)) {
            showMessage(Alert.AlertType.INFORMATION, "Event was added successfully");
        } else //if there is an error
        {
            showMessage(Alert.AlertType.ERROR, "Adding Event Failed!\nThere is already an event with the same information");
        }

        //Show the new event on the calendar according to the selected filters
        mainController.repaintView();

        // Close the window
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }


    //Function that fills the startDate picker based on the clicked startDate
    void autofillDatePicker() {
        startDate.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd-MM-YYYY";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                startDate.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        endDate.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd-MM-YYYY";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                endDate.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        int hours = LocalTime.now().getHour();

        // Set default value for datepickers and timepickers
        startDate.setValue(LocalDate.of(MyCalendar.getInstance().event_year, MyCalendar.getInstance().event_month, MyCalendar.getInstance().event_day));
        endDate.setValue(LocalDate.of(MyCalendar.getInstance().event_year, MyCalendar.getInstance().event_month, MyCalendar.getInstance().event_day));

        if (LocalTime.now().getMinute() < 30) {
            startTime.setValue(LocalTime.of(hours, 30));
            endTime.setValue(LocalTime.now().getHour() == 23 ? LocalTime.of(hours, 59) : LocalTime.of(hours + 1, 0));
        } else {
            startTime.setValue(LocalTime.now().getHour() == 23 ? LocalTime.now() : LocalTime.of(hours + 1, 0));
            endTime.setValue(LocalTime.now().getHour() == 23 ? LocalTime.of(hours, 59) : LocalTime.of(hours + 1, 30));
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {


        //*** Instantiate DBHandler object *******************
        databaseHandler = new DBHandler();
        //****************************************************


        //Fill the startDate picker
        autofillDatePicker();

        //Get the list of exisitng terms from the database and show them in the correspondent drop-down menu
        try {
            //Get terms from database and store them in the ObservableList variable "terms"
            ObservableList<String> terms = databaseHandler.getListOfCategories();
            //Show list of terms in the drop-down menu
            categorieSelect.setItems(terms);
        } catch (SQLException ex) {
            Logger.getLogger(AddEventController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //**********************************************************************
        // ************* Everything below is for Draggable Window ********

        // Set up Mouse Dragging for the Event pop up window
        topLabel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
        // Set up Mouse Dragging for the Event pop up window
        topLabel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });
        // Change cursor when hover over draggable area
        topLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = stage.getScene();
                scene.setCursor(Cursor.HAND); //Change cursor to hand
            }
        });

        // Change cursor when hover over draggable area
        topLabel.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = stage.getScene();
                scene.setCursor(Cursor.DEFAULT); //Change cursor to hand
            }
        });
    }

    private void showMessage(Alert.AlertType type, String message) {
        Alert alertMessage = new Alert(type);
        alertMessage.setHeaderText(null);
        alertMessage.setContentText(message);
        alertMessage.showAndWait();
    }
}