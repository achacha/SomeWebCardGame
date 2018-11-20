package org.achacha.webcardgame.game.data;

import org.achacha.webcardgame.game.logic.NameHelper;
import org.apache.commons.lang3.RandomUtils;

public enum CardType {
    Grue(NameHelper.NameType.Grue),
    Human(NameHelper.NameType.Human),
    Elf(NameHelper.NameType.Elf),
    Goblin(NameHelper.NameType.Goblin);

    final NameHelper.NameType nameType;

    CardType(NameHelper.NameType nameType) {
        this.nameType = nameType;
    }

    /**
     * Return a random type
     * @return CardType
     */
    public static CardType random() {
        return values()[RandomUtils.nextInt(1, values().length)];
    }

    public NameHelper.NameType getNameType() {
        return nameType;
    }
}
