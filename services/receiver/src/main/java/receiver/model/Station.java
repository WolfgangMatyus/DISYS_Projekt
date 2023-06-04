package receiver.model;

import java.util.ArrayList;

public class Station {

    private int id;
    private String url;

    private ArrayList<Charge> charges;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
