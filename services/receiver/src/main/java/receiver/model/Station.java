package receiver.model;

import java.util.ArrayList;

public class Station {

    private int id;
    private String url;

    private ArrayList<Charge> charges;

    private Boolean received = Boolean.FALSE;

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

    public void setCharges(ArrayList<Charge> charges) {
        this.charges = charges;
    }

    public ArrayList<Charge> getCharges() {
        return this.charges;
    }

    public void addCharge(Charge charge) {
        this.charges.add(charge);
    }

    public Boolean getReceived() {
        return received;
    }

    public void setReceived(Boolean received) {
        this.received = received;
    }
}
