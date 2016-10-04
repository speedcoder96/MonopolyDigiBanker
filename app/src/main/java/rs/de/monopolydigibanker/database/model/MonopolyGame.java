package rs.de.monopolydigibanker.database.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import rs.de.monopolydigibanker.R;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.model
 * Class:      MonopolyGame
 */
public class MonopolyGame implements Parcelable {

    public static final int NO_VARIANT_SELECTED = -1;

    public static final int[] VARIANTS = {
            NO_VARIANT_SELECTED, R.xml.monopoly_banking
    };

    private static final String TAG_MONOPOLY = "Monopoly";
    private static final String TAG_STREET = "Street";

    private String name;
    private ArrayList<MonopolyStreet> streets;

    private MonopolyGame() {
        streets = new ArrayList<>();
    }

    protected MonopolyGame(Parcel in) {
        super();
        name = in.readString();
        in.readTypedList(streets, MonopolyStreet.CREATOR);
    }

    public static MonopolyGame load(Context context, int xmlResourceId) {
        MonopolyGame game = new MonopolyGame();
        game.parse(context, xmlResourceId);
        return game;
    }

    public static boolean isVariantSelected(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return MonopolyGame.VARIANTS[Integer.parseInt(preferences.getString(
                context.getString(R.string.key_preference_game_schema_selection),
                context.getString(R.string.value_preference_game_schema_selection)))] != MonopolyGame.NO_VARIANT_SELECTED;
    }

    private void addStreet(MonopolyStreet street) {
        streets.add(street);
    }

    public String getName() {
        return name;
    }

    public ArrayList<MonopolyStreet> getStreets() {
        return streets;
    }

    private void parse(Context context, int xmlResourceId) {
        try {
            XmlResourceParser xmlResourceParser = context.getResources().getXml(xmlResourceId);
            int eventType = xmlResourceParser.getEventType();
            String currentTag = null;
            MonopolyStreet currentStreet = null;
            long[] currentRentList = null;
            int currentRentListIndex = 0;
            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        currentTag = xmlResourceParser.getName();
                        switch(currentTag) {
                            case MonopolyGame.TAG_MONOPOLY:
                                this.name = xmlResourceParser.getAttributeValue(1);
                                break;
                            case MonopolyGame.TAG_STREET:
                                currentStreet = new MonopolyStreet();
                                extractStreetAttributes(xmlResourceParser, currentStreet);
                                break;
                            case MonopolyStreet.TAG_RENT_LIST:
                                currentRentList = new long[5];
                                currentRentListIndex = 0;
                                break;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(currentTag) {
                            case MonopolyStreet.TAG_RENT:
                                currentRentList[currentRentListIndex++] = Long.parseLong(xmlResourceParser.getText());
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        currentTag = xmlResourceParser.getName();
                        switch(currentTag) {
                            case MonopolyGame.TAG_STREET:
                                this.addStreet(currentStreet);
                                break;
                            case MonopolyStreet.TAG_RENT_LIST:
                                currentStreet.setRents(currentRentList);
                                break;
                        }
                        break;
                }
                eventType = xmlResourceParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractStreetAttributes(XmlPullParser parser, MonopolyStreet street) {
        for(int i = 0; i < parser.getAttributeCount(); i++) {
            switch(parser.getAttributeName(i)) {
                case MonopolyStreet.ATTRIBUTE_ID:
                    street.setId(Long.parseLong(parser.getAttributeValue(i)));
                    break;
                case MonopolyStreet.ATTRIBUTE_NAME:
                    street.setName(parser.getAttributeValue(i));
                    break;
                case MonopolyStreet.ATTRIBUTE_GROUP_ID:
                    street.setGroupId(Integer.parseInt(parser.getAttributeValue(i)));
                    break;
                case MonopolyStreet.ATTRIBUTE_PLOT_VALUE:
                    street.setPlotValue(Long.parseLong(parser.getAttributeValue(i)));
                    break;
                case MonopolyStreet.ATTRIBUTE_HOUSE_COST:
                    street.setHouseCost(Long.parseLong(parser.getAttributeValue(i)));
                    break;
                case MonopolyStreet.ATTRIBUTE_HOTEL_COST:
                    street.setHotelCost(Long.parseLong(parser.getAttributeValue(i)));
                    break;
                case MonopolyStreet.ATTRIBUTE_MORTGAGE_VALUE:
                    street.setMortgageValue(Long.parseLong(parser.getAttributeValue(i)));
                    break;
                case MonopolyStreet.ATTRIBUTE_BUILDABLE:
                    street.setBuildable(Boolean.parseBoolean(parser.getAttributeValue(i)));
                    break;
            }
        }
    }


    public static final Creator<MonopolyGame> CREATOR = new Creator<MonopolyGame>() {
        @Override
        public MonopolyGame createFromParcel(Parcel in) {
            return new MonopolyGame(in);
        }

        @Override
        public MonopolyGame[] newArray(int size) {
            return new MonopolyGame[size];
        }
    };

    @Override
    public int describeContents() {
        return 2;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(streets);
    }

}
