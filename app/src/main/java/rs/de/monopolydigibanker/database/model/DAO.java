package rs.de.monopolydigibanker.database.model;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.database.model
 * Class:      DAO
 */
public abstract class DAO {

    public static final long NOT_REGISTERED = -1;

    protected long id;

    public DAO(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
