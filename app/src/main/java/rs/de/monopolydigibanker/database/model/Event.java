package rs.de.monopolydigibanker.database.model;

import android.util.SparseArray;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.database.model
 * Class:      Event
 */
public class Event {

    public static final String GO_MONEY_EVENT = "go_money_event";
    public static final String DOUBLE_GO_MONEY_EVENT = "double_go_money_event";
    public static final String PAY_RENT_EVENT = "pay_rent_event";
    public static final String SINGLE_TRANSFER_EVENT = "single_transfer_event";
    public static final String MULTIPLE_TRANSFER_EVENT = "multiple_transfer_event";
    public static final String MANAGE_ADD_MONEY_EVENT = "manage_add_money_event";
    public static final String MANAGE_SUBTRACT_MONEY_EVENT = "manage_subtract_money_event";

    private static final SparseArray<String> EVENT_IDENTIFIERS =
            new SparseArray<>();

    static {
        EVENT_IDENTIFIERS.put(0, GO_MONEY_EVENT);
        EVENT_IDENTIFIERS.put(1, DOUBLE_GO_MONEY_EVENT);
        EVENT_IDENTIFIERS.put(2, PAY_RENT_EVENT);
        EVENT_IDENTIFIERS.put(3, SINGLE_TRANSFER_EVENT);
        EVENT_IDENTIFIERS.put(4, MULTIPLE_TRANSFER_EVENT);
        EVENT_IDENTIFIERS.put(5, MANAGE_ADD_MONEY_EVENT);
        EVENT_IDENTIFIERS.put(6, MANAGE_SUBTRACT_MONEY_EVENT);
    }

    public static int i(String eventIdentifier) {
        return EVENT_IDENTIFIERS.keyAt(EVENT_IDENTIFIERS.indexOfValue(eventIdentifier));
    }

    public static String n(int eventId) {
        return EVENT_IDENTIFIERS.valueAt(EVENT_IDENTIFIERS.indexOfKey(eventId));
    }

}
