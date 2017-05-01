package org.achacha.webcardgame.game.data;

/**
 * Inventory item type
 */
public enum ItemType {
    Junk(0),
    ElementWhite(1),
    ElementGreen(2),
    ElementBlue(3),
    ElementPurple(4),
    ElementOrange(5),
    ElementPink(6),
    Energy(10),
    ;

    final int typeId;

    ItemType(int typeId) {
        this.typeId = typeId;
    }

    /**
     * Lookup by id
     * @param id int
     * @return ItemType
     */
    public static ItemType of(int id) {
        if (id < 0 || id >= values().length)
            return null;

        return values()[id];
    }
}
