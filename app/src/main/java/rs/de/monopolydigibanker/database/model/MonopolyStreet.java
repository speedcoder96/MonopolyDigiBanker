package rs.de.monopolydigibanker.database.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.model
 * Class:      MonopolyStreet
 */
public class MonopolyStreet implements Parcelable {

    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_GROUP_ID = "group_id";
    public static final String ATTRIBUTE_PLOT_VALUE = "plot_value";
    public static final String ATTRIBUTE_MORTGAGE_VALUE = "mortgage_value";
    public static final String ATTRIBUTE_BUILDABLE = "buildable";
    public static final String ATTRIBUTE_HOUSE_COST = "house_cost";
    public static final String ATTRIBUTE_HOTEL_COST = "hotel_cost";

    public static final String TAG_RENT_LIST = "RentList";
    public static final String TAG_RENT = "rent";

    private long id;
    private String name;
    private int groupId;
    private long plotValue;
    private long mortgageValue;
    private boolean buildable;
    private long[] rents;
    private long houseCost;
    private long hotelCost;

    private int houseCount;

    public MonopolyStreet() {
        rents = new long[5];
    }

    protected MonopolyStreet(Parcel in) {
        super();
        id = in.readLong();
        name = in.readString();
        groupId = in.readInt();
        plotValue = in.readLong();
        in.readLongArray(rents);
        houseCost = in.readLong();
        hotelCost = in.readLong();
        mortgageValue = in.readLong();
        buildable = in.readByte() == 1;
        houseCount = in.readInt();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setPlotValue(long plotValue) {
        this.plotValue = plotValue;
    }

    public void setRents(long[] rents) {
        this.rents = rents;
    }

    public void setHouseCost(long houseCost) {
        this.houseCost = houseCost;
    }

    public void setHotelCost(long hotelCost) {
        this.hotelCost = hotelCost;
    }

    public void setMortgageValue(long mortgageValue) {
        this.mortgageValue = mortgageValue;
    }

    public void setBuildable(boolean buildable) {
        this.buildable = buildable;
    }

    public void setHouseCount(int houseCount) {
        this.houseCount = houseCount;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public long getPlotValue() {
        return plotValue;
    }

    public long[] getRents() {
        return rents;
    }

    public long getHouseCost() {
        return houseCost;
    }

    public long getHotelCost() {
        return hotelCost;
    }

    public int getHouseCount() {
        return houseCount;
    }

    public long getMortgageValue() {
        return mortgageValue;
    }

    public boolean isBuildable() {
        return buildable;
    }

    public static final Creator<MonopolyStreet> CREATOR = new Creator<MonopolyStreet>() {
        @Override
        public MonopolyStreet createFromParcel(Parcel in) {
            return new MonopolyStreet(in);
        }

        @Override
        public MonopolyStreet[] newArray(int size) {
            return new MonopolyStreet[size];
        }
    };

    @Override
    public int describeContents() {
        return 7;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(groupId);
        dest.writeLong(plotValue);
        dest.writeLongArray(rents);
        dest.writeLong(houseCost);
        dest.writeLong(hotelCost);
        dest.writeLong(mortgageValue);
        dest.writeByte((byte) ((buildable) ? 1 : 0));
        dest.writeInt(houseCount);
    }

}
