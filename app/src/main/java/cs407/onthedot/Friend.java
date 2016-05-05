package cs407.onthedot;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by connerhuff on 4/9/16.
 */
public class Friend implements Parcelable {

    private String name;//name of the person to be displayed
    private boolean attending;//used to signify if the person is currently invited
    private String id; //user id provided by facebook. Used in getting the persons profile picture

    public Friend(String name, boolean attending, String id){
        this.name = name;
        this.attending = attending;
        this.id = id;
    }

    public Friend(Parcel in) {
        this.name = in.readString();
        this.attending = (in.readInt() == 1);
        this.id = in.readString();
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

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Friend) && (this.id.equals(((Friend) obj).getId()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(attending ? 1 : 0);
        dest.writeString(id);
    }

    public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {

        public Friend createFromParcel(Parcel in){
            return new Friend(in);
        }
        public Friend[] newArray(int size){
            return new Friend[size];
        }
    };

    public static ArrayList<Friend> getFriendsListFromString(String friendsList) {

        ArrayList<Friend> friends = new ArrayList<>();

        String friendStrings[] = friendsList.split(";");

        for (String friendString : friendStrings) {
            String friendValues[] = friendString.split(",");

            Friend friend =
                    new Friend(friendValues[0], "1".equals(friendValues[1]), friendValues[2]);

            friends.add(friend);
        }

        return friends;
    }

    public static String getFriendsStringFromArray(ArrayList<Friend> friendsList) {

        StringBuilder friendsString = new StringBuilder();

        for (Friend friend : friendsList) {
            friendsString.append(friend.getName());
            friendsString.append(",");
            friendsString.append(friend.attending ? "1" : "0");
            friendsString.append(",");
            friendsString.append(friend.getId());
            friendsString.append(";");
        }

        return friendsString.toString();
    }
}
