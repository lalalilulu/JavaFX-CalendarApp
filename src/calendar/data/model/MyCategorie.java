package calendar.data.model;

import javafx.beans.property.SimpleStringProperty;

public class MyCategorie {
    private final SimpleStringProperty categorieName;

    public MyCategorie(String categorieName) {
        this.categorieName = new SimpleStringProperty(categorieName);
    }

    public String getCategorieName() {
        return categorieName.get();
    }

}
