package calendar.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class DBHandler {

    private static DBHandler handler;

    private static final String DB_URL = "jdbc:derby:calendarDatabase;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;

    //Arrays that contain the default categories
    private static String[] categories = {"Work", "Study", "Sport", "Vacation",
            "Birthdays", "Holidays", "Other"};
    //Variable that contains the default color for the labels of events
    private static String defaultColor = "255-255-255";

    //Variable that controls whether or not the tables have to be created and populated
    private static boolean tablesAlreadyExist = false;

    //Constructor
    public DBHandler() {

        //call to createConnection method that creates the connection between the database and the Java application
        createConnection();

        //checks if tables have been already created by an instantatiation of another object in the program, and if
        //the tables have not being created, then they are created and filled with the correspondent default records
        if (tablesAlreadyExist) {
            System.out.println("Tables already exist, so connection was the only thing created and now you are ready to go!");
        } else {

            // Creates all tables for the database
            createCalendarTable();
            createCategoriesTable();
            createEventsTable();

            //Insert default values into the tables and print them so programmer can check they were added correctly
            insertDefaultValuesIntoTables();
            //The line below can be uncommented to allow programmer to check the default records present in tables
            printAllDefaultRecords();

            //Switched boolean variable tablesAlreadyExist to true because tables were just created
            tablesAlreadyExist = true;

            System.out.println("the static variable tablesAlreadyExist was changed to true. THEREFORE, NO other table should try to be created");

            // the following lines are just here to test the correct functionality of the getListOfTermIDs method
            ArrayList<String> auxList = new ArrayList();
            auxList.add("Work");
            auxList.add("Study");
            ArrayList<String> auxListOfCatIDs = this.getListOfCategorieIDs(auxList);

            // the following lines are just here to test the correct functionality of the getFilteredEvents method
            ArrayList<String> auxList2 = new ArrayList();
            auxList2.add("Work");
            auxList2.add("Study");
            ArrayList<String> auxListOfFilteredEvents = this.getFilteredEvents(auxList2, "TestCalendar");
        }
    }
    //***************************************************************************************************************************************************************

    //***************************************************************************************************************************************************************
    //Create Connection between Java Application and the JDBC
    void createConnection() {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //***************************************************************************************************************************************************************

    //***************************************************************************************************************************************************************
    //********** Functions that create the tables if they do not exist ***************************

    //**************************  CALENDARS Table  ***********************************************
    //Function that creates CALENDARS Table
    void createCalendarTable() {

        String TableName = "CALENDARS";
        try {
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet listOfTables = dbm.getTables(null, null, TableName.toUpperCase(), null);

            if (listOfTables.next()) {
                System.out.println("Table " + TableName + " already exists. Ready to go!");
            } else {
                String query1 = "CREATE TABLE " + TableName + "("
                        + "CalendarName varchar(45) primary key not null,\n"
                        + "StartDate date"
                        + ")";
                stmt.execute(query1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- setupDatabase");
        } finally {
        }
    }

    //***************************************************************************************************************************************************************
    //**************************  CATEGORIES Table  ***********************************************
    //Function that creates TERMS Table
    void createCategoriesTable() {

        String TableName = "CATEGORIES";
        try {
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet listOfTables = dbm.getTables(null, null, TableName.toUpperCase(), null);

            if (listOfTables.next()) {
                System.out.println("Table " + TableName + " already exists. Ready to go!");
            } else {
                String query1 = "CREATE TABLE " + TableName + "("
                        + "CategorieID integer primary key not null,\n"
                        + "CategorieName varchar(100),\n"
                        + "CategorieColor varchar(100)"
                        + ")";
                stmt.execute(query1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- setupDatabase");
        } finally {
        }
    }

    //***************************************************************************************************************************************************************
    //**************************  EVENTS Table  ***********************************************
    //Function that creates EVENTS Table
    void createEventsTable() {

        String TableName = "EVENTS";
        try {
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet listOfTables = dbm.getTables(null, null, TableName.toUpperCase(), null);

            if (listOfTables.next()) {
                System.out.println("Table " + TableName + " already exists. Ready to go!");
            } else {
                String query1 = "CREATE TABLE " + TableName + "("
                        + "EventID integer primary key not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n"
                        + "EventDescription varchar(45) not null,\n"
                        + "CategorieID integer not null,\n"
                        + "CalendarName varchar(45) not null,\n"
                        + "EventStartDate date not null,\n"
                        + "EventStartTime time not null,\n"
                        + "EventEndDate date not null,\n"
                        + "EventEndTime time not null,\n"
                        + "EventComment varchar(150), \n"
                        + "constraint " + TableName + "_FK1 foreign key (CategorieID) references CATEGORIES(CategorieID),\n"
                        + "constraint " + TableName + "_FK2 foreign key (CalendarName) references CALENDARS(CalendarName)"
                        + ")";
                stmt.execute(query1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- setupDatabase");
        } finally {
        }
    }

    //***************************************************************************************************************************************************************
    //Function that checks if a table in the database is empty (has no records), and return a boolean values based on the checking result
    boolean checkIfTableIsEmpty(String tableName) {
        boolean checkingResult = false;
        try {
            stmt = conn.createStatement();

            ResultSet res = stmt.executeQuery("SELECT * FROM " + tableName);
            while (res.next()) {
                checkingResult = true;
                break;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- checking Table failed/error");
            return false;
        } finally {
        }
        return checkingResult;
    }
    //***************************************************************************************************************************************************************

    //***************************************************************************************************************************************************************
    //Function that populates the tables TERMS and CALENDARS with default values
    void insertDefaultValuesIntoTables() {

        // Inserting default values in the TERMS table
        String TableName = "CATEGORIES";
        try {
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet listOfTables = dbm.getTables(null, null, TableName.toUpperCase(), null);

            if (listOfTables.next()) {

                boolean dataExistsInTable = checkIfTableIsEmpty(TableName);
                if (!dataExistsInTable) {
                    int id = 1;
                    for (int i = 0; i < categories.length; i++) {
                        String query2 = "INSERT INTO " + TableName + " VALUES(" + id + ", '" + categories[i] + "', '" + defaultColor + "')";
                        stmt.execute(query2);
                        id++;
                    }
                    System.out.println("Default values SUCCESSFULLY inserted Table " + TableName + "!!!");
                } else {
                    System.out.println("Default values already exist in Table " + TableName);
                }

            } else {
                System.out.println("Table " + TableName + " does not exist");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- setupDatabase");
        } finally {
        }


        // Inserting default values in the CALENDARS table. Creating a test calendar from the start
        // This test calendar can be deleted by the user.
        TableName = "CALENDARS";
        try {
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet listOfTables = dbm.getTables(null, null, TableName.toUpperCase(), null);

            if (listOfTables.next()) {

                boolean dataExistsInTable = checkIfTableIsEmpty(TableName);
                if (!dataExistsInTable) {
                    String query2 = "INSERT INTO " + TableName + " VALUES('TestCalendar', '2019-05-05')";
                    stmt.execute(query2);
                    System.out.println("Default values SUCCESSFULLY inserted Table " + TableName + "!!!");
                } else {
                    System.out.println("Default values already exist in Table " + TableName);
                }

            } else {
                System.out.println("Table " + TableName + " does not exist");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- setupDatabase");
        } finally {
        }

    }
    //***************************************************************************************************************************************************************

    //***************************************************************************************************************************************************************
    // This function is for testing purposes. Helps the programmer see all the terms in the TERMS Table and all the Rules in the RULES table
    void printAllDefaultRecords() {
        try {

            // Print default records from TERMS table
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM CATEGORIES");
            System.out.println("----------------------------------------");
            System.out.println("----------------------------------------");
            System.out.println("Table CATEGORIES default records:");
            while (res.next()) {
                System.out.println(res.getString("CategorieID") + " - " + res.getString("CategorieName")
                        + ", color: " + res.getString("CategorieColor"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- Error when printing");
        } finally {
        }
    }
    //***************************************************************************************************************************************************************

    //*****************************************************************************************************************************
    //Function that executes a SELECT query and returns the requested values/data from the database
    public ResultSet executeQuery(String query) {
        ResultSet result;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at executeQuery:dataHandler --> ERROR: " + ex.getLocalizedMessage());
            return null;
        } finally {
        }

        return result;
    }
    //*****************************************************************************************************************************

    //*****************************************************************************************************************************
    //Function that executes an insertion, deletion, or update query
    public boolean executeAction(String query2) {
        try {
            stmt = conn.createStatement();
            stmt.execute(query2);
            return true;
        } catch (SQLException ex) {
            System.out.println("Exception at executeQuery:dataHandler  --> ERROR: " + ex.getLocalizedMessage());
            return false;
        } finally {
        }
    }
    //*****************************************************************************************************************************

    //*****************************************************************************************************************************
    //Function that return the complete list of categories that exist in the database
    public ObservableList<String> getListOfCategories() throws SQLException {

        //ArrayList that will contain all terms saved in the TERMS Tables
        ObservableList<String> listOfCategories = FXCollections.observableArrayList();// = new ObservableList();

        //Query that will obtain all available Term Names from the database table TERMS
        String queryListOfTerms = "SELECT CategorieName FROM CATEGORIES";
        //Variable that will hold the result of executing the previous query
        ResultSet rs = executeQuery(queryListOfTerms);

        try {
            //While there are Term Names in the ResultSet variable, add each one of them to the ObservableList of Strings
            while (rs.next()) {
                //get the term name and store it in a String variable
                String categorieName = rs.getString("CategorieName");
                //add Term Name to list of terms
                listOfCategories.add(categorieName);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- error at getListOfCategories method in DBHandler class");
        }

        return listOfCategories;
    }

    //*****************************************************************************************************************************        
    //Function that returns the CategorieID based on a given term name
    public int getCategorieID(String categrieName) {

        int categorieID = 0;
        String getCatIDQuery = "Select CategorieID From CATEGORIES WHERE CategorieName='" + categrieName + "'";
        ResultSet res = executeQuery(getCatIDQuery);

        try {
            while (res.next()) {
                categorieID = res.getInt("CategorieID");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- error at getCategorieID method in DBHandler class");
        }

        return categorieID;
    }
    //*****************************************************************************************************************************

    public String getCategorieName(int catIDAux) {

        //Declare variable that will contain the name of the categorie
        String nameOfCategorie = "x";
        //Create query that will find a matching result for the categorieName based on the categorie's ID
        String getCategorieNameQuery = "SELECT CategorieName FROM CATEGORIES "
                + "WHERE CATEGORIES.CategorieID=" + catIDAux;

        //Execute query to get the name of the categorie based on the given categorie ID
        ResultSet res = executeQuery(getCategorieNameQuery);

        // get the name of the categorie and store it in the String variable nameOfCategorie
        // if the query obtained a result for the given categorie ID
        try {
            while (res.next()) {
                nameOfCategorie = res.getString("CategorieName");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- error at getCategorieID method in DBHandler class");
        }

        return nameOfCategorie;
    }
    //*****************************************************************************************************************************

    public String getCategorieColor(int auxCatID) {

        //Declare variable that will contain the color of the categorie to be returned
        String categorieColor = "x";

        //Create query that will find a matching result for the categorieColor based on the categorie's ID
        String getCategorieColorQuery = "SELECT CategorieColor FROM CATEGORIES "
                + "WHERE CATEGORIES.CategorieID=" + auxCatID;

        //Execute query to get the color of a term based a given cateorie ID
        ResultSet res = executeQuery(getCategorieColorQuery);

        // store color in a String variable of the query obtained a result
        try {
            while (res.next()) {
                categorieColor = res.getString("CategorieColor");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "--- error at getCategorieColor method in DBHandler class");
        }

        return categorieColor;
    }
    //*****************************************************************************************************************************

    //*****************************************************************************************************************************
    // Function that sets (updates) the color of a categorie.
    // Takes as arguments: the new color and the categorie editing
    public void setCategorieColor(String catIdentifier, String catRGB) {

        //Query that will update the color of the terms that end with the term ending specified by variable "termIdentifier"
        String setColorAction = "UPDATE CATEGORIES "
                + "SET CategorieColor ='" + catRGB + "' "
                + "WHERE CATEGORIES.CategorieName LIKE "
                + "'%" + catIdentifier + "%'";

        //Execute query to update the color for the speficied terms
        executeAction(setColorAction);

    }
    //*****************************************************************************************************************************

    //*****************************************************************************************************************************
    // Function that returns a list of Term IDs based on a list of term endings (identifiers)
    public ArrayList<String> getListOfCategorieIDs(ArrayList<String> auxIdentifiersList) {

        //Object that will hold all the list of Term IDs to be returned
        ArrayList<String> listOfCategorieIDs = new ArrayList<>();

        //Loop that will add to the ArrayList listOfTermIDs all the term IDs needed for filtering events
        for (int i = 0; i < auxIdentifiersList.size(); i++) {
            //Query that will select the term IDs that end with the each term identifier
            // in the ArrayList auxTermIdentifiersList
            String IDsQuery = "SELECT CategorieID FROM CATEGORIES "
                    + "WHERE CategorieName LIKE '%" + auxIdentifiersList.get(i) + "%'";

            //Variable that will hold the result of executing the previous query
            ResultSet rs = executeQuery(IDsQuery);

            try {
                //While there are Term IDs in the ResultSet variable, add each one of them to the ArrayList of Strings
                while (rs.next()) {
                    //get the Term ID and store it in a String variable
                    String auxID = rs.getString("CategorieID");
                    //add term ID to list of term IDs
                    listOfCategorieIDs.add(auxID);
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage() + "--- error at getListOfCategorieIDs method in DBHandler class");
            }
        }

        return listOfCategorieIDs;
    }
    //*****************************************************************************************************************************

    //*****************************************************************************************************************************
    // Function that returns list of filtered events to be shown in the calendar
    // this function takes as arguments: the list of term identiftiers and the current calendar
    public ArrayList<String> getFilteredEvents(ArrayList<String> auxIdentifiersList, String calName) {

        //Declare and instantiate ArrayList object that will hold all events for the requested term(s)
        ArrayList<String> filteredEventsList = new ArrayList();

        //has to call getListOfTermIDs first to know which events to get from EVENTS table
        ArrayList<String> listOfIDs = this.getListOfCategorieIDs(auxIdentifiersList);

        //Continue to get the events if the list of term IDs is not empty, i.e., if the user selected at least one filter/term
        if (!listOfIDs.isEmpty()) {
            for (int i = 0; i < listOfIDs.size(); i++) {
                //Query that will select all events that match the term ID and the calendar the user is working on
                String getEventsQuery = "SELECT * FROM EVENTS "
                        + "WHERE EVENTS.CategorieID=" + listOfIDs.get(i)
                        + " AND EVENTS.CalendarName='" + calName + "'";

                //Variable that will hold the result of executing the previous query
                ResultSet rs = executeQuery(getEventsQuery);

                try {
                    //While there are events in the ResultSet variable, add each one of them to the ArrayList of Strings
                    while (rs.next()) {
                        //get the full row of the event info and store it in a String variable
                        String filteredEvent = rs.getInt("EventID") + "~"
                                + rs.getString("EventDescription") + "~"
                                + rs.getInt("CategorieID") + "~"
                                + rs.getString("CalendarName") + "~"
                                + rs.getString("EventStartDate") + "~"
                                + rs.getString("EventStartTime") + "~"
                                + rs.getString("EventEndDate") + "~"
                                + rs.getString("EventEndTime") + "~"
                                + rs.getString("EventComment");
                        //add event to list of filtered events
                        filteredEventsList.add(filteredEvent);
                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage() + "--- error at getListOfRules method in DBHandler class");
                }
            }
        }
        return filteredEventsList;
    }
}
