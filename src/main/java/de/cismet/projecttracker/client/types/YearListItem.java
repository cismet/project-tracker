package de.cismet.projecttracker.client.types;

/**
 * A ListItem implementation that wraps years
 *
 * @author therter
 */
public class YearListItem extends ListItem {
    int year;

    public YearListItem(int year) {
        super("" + year, "" + year);
        this.year = year;
    }

    public YearListItem(String id, String name) {
        super(id, name);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        setId("" + year);
        setName("" + year);
    }
}