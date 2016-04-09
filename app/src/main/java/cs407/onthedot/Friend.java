package cs407.onthedot;

/**
 * Created by connerhuff on 4/9/16.
 */
public class Friend {

    private String name;//name of the person to be displayed
    private boolean attending;//used to signify if the person is currently invited
    private String id; //user id provided by facebook. Used in getting the persons profile picture

    public Friend(String name, boolean attending, String id){
        this.name = name;
        this.attending = attending;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAttending() {
        return attending;
    }

    public void setAttending(boolean attending) {
        this.attending = attending;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
