package paul.NLPTextDungeon.entities;

/**
 * Created by Paul Dennis on 9/6/2017.
 */
public class Feature {

    private boolean darklight;
    private String description;

    private transient boolean bumped;

    public Feature () {

    }

    public boolean isDarklight() {
        return darklight;
    }

    public void setDarklight(boolean darklight) {
        this.darklight = darklight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static final double DEFAULT_VISIBILITY_THRESHHOLD = 0.6;
    public boolean isVisible (double lighting) {
        if (darklight) {
            return lighting == 0.0;
        }
        return lighting >= DEFAULT_VISIBILITY_THRESHHOLD;
    }

    public boolean isBumped() {
        return bumped;
    }

    public void setBumped(boolean bumped) {
        this.bumped = bumped;
    }
}