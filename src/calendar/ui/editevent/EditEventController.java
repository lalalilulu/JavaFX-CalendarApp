package calendar.ui.editevent;

import calendar.data.model.MyCalendar;
import calendar.database.DBHandler;
import calendar.ui.addevent.AddEventController;
import calendar.ui.main.Controller;
import com.jfoenix.controls.*;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditEventController implements Initializable {

    // Main Controller -------------------------------
    private Controller mainController;
    // -------------------------------------------------------------------

    //--------------------------------------------------------------------
    //---------Database Object -------------------------------------------
    DBHandler databaseHandler;
    //--------------------------------------------------------------------
    @FXML
    private Label topLabel;
    @FXML
    private JFXTextField subject;
    @FXML
    private JFXTextArea comment;
    @FXML
    private JFXComboBox<String> categorieSelect;
    // Date and Time Picker
    @FXML
    private JFXTimePicker startTime;
    @FXML
    private JFXDatePicker startDate;
    @FXML
    private JFXTimePicker endTime;
    @FXML
    private JFXDatePicker endDate;
    @FXML
    private AnchorPane rootPane;

    //Set main controller
    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    // These fields are for mouse dragging of window
    private double xOffset;
    private double yOffset;


    //Function that fills the date picker based on the clicked event's date
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

        String calName = MyCalendar.getInstance().calendar_name;
        int categorieID = MyCalendar.getInstance().event_categorie;
        String descript = MyCalendar.getInstance().event_subject;

        String chosenCategorieName = "";

        //Query to get ID for the selected CATEGORIES
        String getIDQuery = "SELECT CategorieName From CATEGORIES "
                + "WHERE CategorieID= " + categorieID + " ";

        ResultSet result = databaseHandler.executeQuery(getIDQuery);

        try {
            while (result.next()) {
                //store ID into the corresponding variable
                chosenCategorieName = result.getString("CategorieName");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddEventController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Store the results from executing the Query
        String query = "select EventStartDate, EventStartTime, EventEndDate, EventEndTime, EventComment " +
                "from EVENTS " + "WHERE CategorieID= " + categorieID + " and EventDescription= '" + descript + "' and CalendarName= '" + calName + "'";

        try {
            ResultSet rs = databaseHandler.executeQuery(query);
            while (rs.next()) {

                Date staDate = rs.getDate("EventStartDate");
                Time staTime = rs.getTime("EventStartTime");
                Date eDate = rs.getDate("EventEndDate");
                Time eTime = rs.getTime("EventEndTime");
                String evComment = rs.getString("EventComment");

                subject.setText(descript);
                categorieSelect.getSelectionModel().select(chosenCategorieName);
                startTime.setValue(staTime.toLocalTime());
                startDate.setValue(LocalDate.parse(staDate.toString()));
                endDate.setValue(LocalDate.parse(eDate.toString()));
                endTime.setValue(eTime.toLocalTime());
                comment.setText(evComment);
            }
        } catch (SQLException e) {
            showMessage(AlertType.ERROR, "The Event cannot be modified!\nPlease check conditions!");
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

        //Fill the date picker
        autofillDatePicker();

        //Get the list of exisitng terms from the database and show them in the correspondent drop-down menu
        ObservableList<String> termsList;
        try {
            //Get terms from database and store them in the ObservableList variable "termsList"
            termsList = databaseHandler.getListOfCategories();
            //Show list of terms in the drop-down menu
            categorieSelect.setItems(termsList);
        } catch (SQLException ex) {
            Logger.getLogger(EditEventController.class.getName()).log(Level.SEVERE, null, ex);
        }


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

    @FXML
    private void exit(MouseEvent event) {
        // Close the window
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void update(MouseEvent event) {
        updateEvent();
    }

    //Function that deletes a selected event
    @FXML
    private void delete(MouseEvent event) {

        //Show confirmation dialog to make sure the user want to delete the selected event
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Event");
        alert.setContentText("Are you sure you want to delete this event?");
        //Customize the buttons in the confirmation dialog
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        //Set buttons onto the confirmation window
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        //Get the user's answer on whether deleting or not
        Optional<ButtonType> result = alert.showAndWait();

        //If the user wants to delete the event, call the function that deletes the event. Otherwise, close the window
        if (result.get() == buttonTypeYes) {
            deleteEvent();
        } else {
            // Close the window
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
    }


    //Function that updates the information of a selected event from the calendar
    public void updateEvent() {

        // Define Date format '2011-12-03'
        DateTimeFormatter myFormat = DateTimeFormatter.ISO_LOCAL_DATE;
        // Define Time format '10:15:30'
        DateTimeFormatter myTimeFormat = DateTimeFormatter.ISO_LOCAL_TIME;

        // Get the date value from the date picker
        String newStartDate = startDate.getValue().format(myFormat);
        String newStartTime = startTime.getValue().format(myTimeFormat);

        String newEndDate = endDate.getValue().format(myFormat);
        String newEndTime = endTime.getValue().format(myTimeFormat);

        // Subject for the event
        String newEventSubject = subject.getText();
        // Get categorie that was selected by the user
        String categorie = categorieSelect.getValue();
        // Get comment that was inputed by the user
        String comments = comment.getText();

        //Check if the user inputted information in all required fields!
        if (subject.getText().isEmpty() || categorieSelect.getSelectionModel().isEmpty()
                || startDate.getValue() == null || startTime.getValue() == null
                || endDate.getValue() == null || endTime.getValue() == null) {
            showMessage(AlertType.ERROR, "Please fill out all fields");
            return;
        }

        //Check if the event descritption contains the character ~ because it cannot contain it due to database and filtering issues
        else if (newEventSubject.contains("~")) {
            //Show message indicating that the event description cannot contain the character ~
            showMessage(AlertType.WARNING, "Event Description cannot contain the character ~");
            return;
        } else if (startDate.getValue().isAfter(endDate.getValue())) {
            showMessage(AlertType.ERROR, "The start date can never be after the end date");
            return;
        } else if (startDate.getValue().equals(endDate.getValue())) {

            if (startTime.getValue().isAfter(endTime.getValue())) {
                showMessage(AlertType.ERROR, "The start time can not be after the end time if both are on the same date");
                return;
            }
        }

        //Get the ID of the new categorie selected by the user when editing the event's information
        int newCategorie = databaseHandler.getCategorieID(categorie);

        //Query to will update the selected event with the new information
        String updateEventQuery = "UPDATE EVENTS"
                + " SET "
                + "EventDescription='" + newEventSubject + "', "
                + "CategorieID=" + newCategorie + ","
                + "EVENTS.CalendarName='" + MyCalendar.getInstance().calendar_name + "', "
                + "EventStartDate='" + newStartDate + "', "
                + "EventStartTime='" + newStartTime + "', "
                + "EventEndDate='" + newEndDate + "', "
                + "EventEndTime='" + newEndTime + "', "
                + "EventComment='" + comments + "'"
                + " WHERE "
                + "EVENTS.EventDescription='" + MyCalendar.getInstance().event_subject + "' AND "
                //+ "EVENTS.EventStartDate='" + eventStartDate + "' AND "
                + "EVENTS.CategorieID=" + MyCalendar.getInstance().event_categorie + " AND "
                + "EVENTS.CalendarName='" + MyCalendar.getInstance().calendar_name + "' ";


        //Execute query in otder to update the info for the selected event
        //and
        //Check if the update of the event in the database was successful, and show message either if it was or not
        if (databaseHandler.executeAction(updateEventQuery)) {
            showMessage(AlertType.INFORMATION, "Event was updated successfully");

            // Update view
            mainController.repaintView();

            //if there is an error
        } else {
            showMessage(AlertType.ERROR, "Updating Event Failed!\nThere is already an event with the same information");
        }

        // Close the window
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }


    public void deleteEvent() {

        int categorieID = MyCalendar.getInstance().event_categorie;
        String descript = MyCalendar.getInstance().event_subject;
        String calName = MyCalendar.getInstance().calendar_name;

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        //String eventStartDate = dateFormat.format(new Date(MyCalendar.getInstance().event_year, MyCalendar.getInstance().event_month, MyCalendar.getInstance().event_day));

        //Query that will delete the selected event
        String deleteEventQuery = "DELETE FROM EVENTS "
                + "WHERE "
                + "EVENTS.EventDescription='" + descript + "' AND "
                //+ "EVENTS.EventStartDate='" + eventStartDate + "' AND "
                + "EVENTS.CategorieID=" + categorieID + " AND "
                + "EVENTS.CalendarName='" + calName + "' ";

        //Execute query that deletes the selected event
        boolean eventWasDeleted = databaseHandler.executeAction(deleteEventQuery);

        if (eventWasDeleted) {
            //Show message indicating that the selected rule was deleted
            showMessage(AlertType.INFORMATION, "Selected event was successfully deleted");

            // Update view
            mainController.repaintView();

            // Close the window, so that when user clicks on "Manage Rules" only the remaining existing rules appear
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        } else {
            //Show message indicating that the rule could not be deleted
            showMessage(AlertType.ERROR, "Deleting Event Failed!");
        }
    }

    private void showMessage(AlertType type, String error) {
        Alert alertMessage = new Alert(type);
        alertMessage.setHeaderText(null);
        alertMessage.setContentText(error);
        alertMessage.showAndWait();
    }
}
