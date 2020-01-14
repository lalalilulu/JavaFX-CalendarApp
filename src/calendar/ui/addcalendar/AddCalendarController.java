package calendar.ui.addcalendar;

import calendar.data.model.MyCalendar;
import calendar.database.DBHandler;
import calendar.ui.main.Controller;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddCalendarController implements Initializable {
    
    
    //--------------------------------------------------------------------
    //---------Database Object -------------------------------------------
    DBHandler databaseHandler;
    //--------------------------------------------------------------------

    // Controllers
    private Controller mainController ;

    public void setMainController(Controller mainController) {
        this.mainController = mainController ;
    }
    
    @FXML
    private Label topLabel;
    @FXML
    private Label exit;
    @FXML
    private JFXTextField calendarName;

    @FXML
    private JFXButton generate;
    @FXML
    private JFXButton cancel;
    
    // These fields are for mouse dragging of window
    private double xOffset;
    private double yOffset;
    
    @FXML
    private AnchorPane rootPane;
    
    @FXML
    void generateNewCalendar(MouseEvent event) {
        
        //Variable that holds the calendar name entered by the user
        String calName = calendarName.getText();
        
        //Check if the user actually gave input for the calendar name and the start date of the calendar
        if (!calendarName.getText().isEmpty()) {
            
            //Check if the calendar name contains the character ~ because it cannot contain it due to database and filtering issues
            if (calName.contains("~"))
            {
                //Show message indicating that the calendar cannot contain the character ~
                Alert alertMessage = new Alert(AlertType.WARNING);
                alertMessage.setHeaderText(null);
                alertMessage.setContentText("Calendar name cannot contain the character ~");
                alertMessage.showAndWait();
            }
            else
            {
               // Set the starting year, the starting date and store them in a Model object
                MyCalendar.getInstance().calendar_name = calendarName.getText();

                //Store calendar's information in String and Integer variables that will be used to build the query to insert it into the database
                String calName2 = calendarName.getText();
                String startingDate = MyCalendar.getInstance().getCalendar_start_date();

                //************************************************************************
                //************************************************************************
                //
                //********  Inserting the new calendar data into the database  ***********

                //*** Instantiate DBHandler object *******************
                databaseHandler = new DBHandler();
                //****************************************************

                // Query that inserts the new calendar into the database
                String calendarQuery = "INSERT INTO CALENDARS VALUES ("
                        + "'" + calName2 + "', '" + startingDate + "')";

                //Insert the new calendar into the database and show a message wheher the insertion was successful or not
                if(databaseHandler.executeAction(calendarQuery)) 
                {
                    Alert alertMessage = new Alert(AlertType.INFORMATION);
                    alertMessage.setHeaderText(null);
                    alertMessage.setContentText("Calendar was created successfully");
                    alertMessage.showAndWait();

                    // Load the calendar in the main window in month's view
                    mainController.calendarGenerate();

                    //Enable the checkboxes for filtering events, now that the user is actually working on a calendar
//                    mainController.enableCheckBoxes();

                    //Enable the buttons that work with rules
                    //mainController.enableButtons();
                }
                else //if there is an error
                {
                    Alert alertMessage = new Alert(AlertType.ERROR);
                    alertMessage.setHeaderText(null);
                    alertMessage.setContentText("Creating Calendar Failed!\nPlease use a different name for the calendar");
                    alertMessage.showAndWait();
                }

            }
            
        }
        else 
        {
            Alert alert = new Alert(AlertType.WARNING, "Please fill out all fields.");
            alert.showAndWait();
        }        
    }
   
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // ******** Code below is for Draggable windows **********    
        
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
        //Close the window
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancel(MouseEvent event) {
        //Close the window
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    
}
