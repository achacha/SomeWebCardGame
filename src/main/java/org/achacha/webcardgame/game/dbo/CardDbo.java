package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDbo;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.logic.EncounterEventLog;
import org.achacha.webcardgame.game.logic.NameHelper;
import org.achacha.webcardgame.sticker.CardSticker;
import org.achacha.webcardgame.sticker.CardStickerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Table(schema="public", name="card")
public class CardDbo extends BaseDbo {
    transient private static final Logger LOGGER = LogManager.getLogger(CardDbo.class);

    /** Card id */
    protected long id;

    /** Card name */
    protected String name;

    /** Enemy type */
    protected CardType type;

    /**
     * Level
     */
    protected int level;

    /**
     * Experience into the level
     * when xp > 100,000 then level is increased
     */
    protected int xp;

    /**
     * Percent health [0,100]
     * Not saved to database, health is reset to 100 before each adventure
     */
    protected int health = 100;

    /**
     * strength - combat damage, change to absorb damage
     */
    protected int strength = 10;

    /**
     * agility - critical chance, damage avoidance
     */
    protected int agility = 10;

    /**
     * Amount of base damage per turn
     */
    protected int damage = 10;

    /** Player that owns this inventory */
    protected long playerId;

    /**
     * Encounter that owns this inventory
     * If 0 then not assigned to encounter yet
     */
    protected long encounterId;

    /** Card stickers */
    protected List<CardSticker> stickers;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.playerId = rs.getLong("player__id");
        this.encounterId = rs.getLong("encounter__id");

        this.name = rs.getString("name");
        this.type = CardType.valueOf(rs.getString("type"));

        this.level = rs.getInt("level");
        this.xp = rs.getInt("xp");
        this.strength = rs.getInt("strength");
        this.agility = rs.getInt("agility");

        String stickerString = rs.getString("stickers");
        String[] stickerArray = stickerString.split(",");
        this.stickers = new ArrayList<>(stickerArray.length);
        for (String name : stickerArray) {
            CardSticker sticker = CardStickerFactory.getSticker(name);
            if (sticker != null)
                this.stickers.add(sticker);
            else
                LOGGER.warn("Unknown sticker on cardId={} stickerName={}", id, name);
        }
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public List<CardSticker> getStickers() {
        return stickers;
    }

    public void setStickers(List<CardSticker> stickers) {
        this.stickers = stickers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(long encounterId) {
        this.encounterId = encounterId;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Generate a new name based on CardType.NameType
     */
    public void generateName() {
        this.name = NameHelper.generateName(this.type.getNameType());
    }

    /**
     * @return player health as a pecent, always [0,100]
     */
    public int getHealth() {
        return health;
    }

    /**
     * @param health player health percent, must be [0,100]
     */
    public void setHealth(int health) {
        this.health = health;
        if (this.health < 0) {
            LOGGER.error("Health fell below 0, setting to 0");
            this.health = 0;
        }
        if (this.health > 100) {
            LOGGER.error("Health over 100, setting to 100");
            this.health = 100;
        }
    }

    /**
     * Decrease health
     * Cannot decrease below 0
     *
     * @param healthDelta health percent lost
     * @return health after decrease
     */
    public int decHealth(int healthDelta) {
        this.health -= healthDelta;
        return this.health;
    }

    /**
     * Increase health
     * Cannot increase above 100
     *
     * @param healthDelta health percent gained
     * @return health after increase
     */
    public int incHealth(int healthDelta) {
        this.health += healthDelta;
        if (this.health > 200)
            this.health = 200;
        return this.health;
    }

    /**
     * @return true if alive
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Apply stickers before encounter
     * @param eventLog EncounterEventLog
     * @param targetCard CardDbo target
     */
    public void processStickersBeforeEncounter(EncounterEventLog eventLog, CardDbo targetCard) {
        if (stickers != null)
            stickers.forEach(sticker-> sticker.beforeEncounter(eventLog, this, targetCard));
    }

    /**
     * Apply stickers after encounter
     * @param eventLog EncounterEventLog
     * @param targetCard CardDbo target
     */
    public void processStickersAfterEncounter(EncounterEventLog eventLog, CardDbo targetCard) {
        if (stickers != null)
            stickers.forEach(sticker-> sticker.afterEncounter(eventLog, this, targetCard));
    }

    /**
     * Apply stickers before turn
     * @param eventLog EncounterEventLog
     * @param targetCard CardDbo target
     */
    public void processStickersBeforeTurn(EncounterEventLog eventLog, CardDbo targetCard) {
        if (stickers != null)
            stickers.forEach(sticker-> sticker.beforeTurn(eventLog, this, targetCard));
    }

    /**
     * Apply stickers after turn
     * @param eventLog EncounterEventLog
     * @param targetCard CardDbo target
     */
    public void processStickersAfterTurn(EncounterEventLog eventLog, CardDbo targetCard) {
        if (stickers != null)
            stickers.forEach(sticker-> sticker.afterTurn(eventLog, this, targetCard));
    }

}
