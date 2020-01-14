package calendar.data.model;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class MyCalendar {

    private final static MyCalendar instance = new MyCalendar();

    public static MyCalendar getInstance() {
        return instance;
    }

    // for adding/editing events
    public int event_day;
    public int event_month;
    public int event_year;
    public String event_subject;
    public int event_categorie;

    // for the year, month, week and day the user has open, is "viewing"
    public int viewing_day;
    public int viewing_week;
    public int viewing_month;
    public int viewing_year;
    public int viewing_day_of_month;


    // for the current calendar being worked on
    public String calendar_name;

    //public set this day as a start day for created calendar
    private String calendar_start_date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

    private int calendar_start_year = LocalDate.now().getYear();
    private int calendar_start_month = LocalDate.now().getMonthValue();
    private int calendar_start_day = LocalDate.now().getDayOfMonth();

    public String getCalendar_start_date() {
        return calendar_start_date;
    }

    public void setCalendar_start_date(String calendar_start_date) {
        this.calendar_start_date = calendar_start_date;
    }

    public String getMonth(int index) {
        switch (index)
        {
            case 1:
                return "JANUARY";
            case 2:
                return "FEBRUARY";
            case 3:
                return "MARCH";
            case 4:
                return "APRIL";
            case 5:
                return "MAY";
            case 6:
                return "JUNE";
            case 7:
                return "JULY";
            case 8:
                return "AUGUST";
            case 9:
                return "SEPTEMBER";
            case 10:
                return "OCTOBER";
            case 11:
                return "NOVEMBER";
            case 12:
                return "DECEMBER";
        }
        return "JANUARY";
    }

    //Function that returns a month Index based on the given month name
    public int getMonthIndex(String month){
        switch (month)
        {
            case "JANUARY":
                return 1;
            case "FEBRUARY":
                return 2;
            case "MARCH":
                return 3;
            case "APRIL":
                return 4;
            case "MAY":
                return 5;
            case "JUNE":
                return 6;
            case "JULY":
                return 7;
            case "AUGUST":
                return 8;
            case "SEPTEMBER":
                return 9;
            case "OCTOBER":
                return 10;
            case "NOVEMBER":
                return 11;
            case "DECEMBER":
                return 12;
        }
        return 0;
    }


    public LocalDate getSelectedFullDate(){
        return LocalDate.of(viewing_year, viewing_month, viewing_day_of_month);
    }

    // return days of selected week? whwre 0 is Monday
    public LocalDate[] getAllDaysOfSelectedWeek(){
        LocalDate[] allDaysOfSelectedWeek =new LocalDate[7];
        LocalDate startOfWeek;

        if (getSelectedFullDate().getDayOfWeek()==DayOfWeek.MONDAY){
            startOfWeek=getSelectedFullDate();
        }else {
            startOfWeek=getSelectedFullDate().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        }

        for (int i = 0; i< allDaysOfSelectedWeek.length; i++){
            allDaysOfSelectedWeek[i]=startOfWeek.plusDays(i);
        }
        return allDaysOfSelectedWeek;
    }



}
