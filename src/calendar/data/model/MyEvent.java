package calendar.data.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalTime;


public class MyEvent {
    private final SimpleStringProperty subject;
    private final SimpleIntegerProperty categorie;
    private final SimpleStringProperty calendar;
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final LocalDate endDate;
    private final LocalTime endTime;
    private final SimpleStringProperty comment;

    private MyCategorie eventCategorie;

    public MyEvent(String subject, int categorie, String calendar, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        this.subject = new SimpleStringProperty(subject);
        this.categorie = new SimpleIntegerProperty(categorie);
        this.calendar = new SimpleStringProperty(calendar);
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.comment = new SimpleStringProperty(null);
    }

    public MyEvent(String subject, int categorie, String calendar, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String comment) {
        this.subject = new SimpleStringProperty(subject);
        this.categorie = new SimpleIntegerProperty(categorie);
        this.calendar = new SimpleStringProperty(calendar);
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.comment = new SimpleStringProperty(comment);
    }

    public MyEvent(MyEvent event) {
        this.subject = event.subject;
        this.categorie = event.categorie;
        this.calendar = event.calendar;
        this.startDate = event.startDate;
        this.startTime = event.startTime;
        this.endDate = event.endDate;
        this.endTime = event.endTime;
        this.comment = event.comment;
    }


    public String getSubject() {
        return subject.get();
    }
    public Integer getCategorie() {
        return categorie.get();
    }
    public String getCalendar() {
        return calendar.get();
    }
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
    public String getComment() {
        return comment.get();
    }
    public SimpleStringProperty commentProperty() {
        return comment;
    }
}
