package org.achacha.webcardgame.game.data;

import org.achacha.webcardgame.game.logic.CardNameGenerator;
import org.apache.commons.lang3.RandomUtils;

public enum CardType {
    Grue(CardNameGenerator.NameType.Grue),
    Human(CardNameGenerator.NameType.Human),
    Elf(CardNameGenerator.NameType.Elf),
    Goblin(CardNameGenerator.NameType.Goblin);

    final CardNameGenerator.NameType nameType;

    CardType(CardNameGenerator.NameType nameType) {
        this.nameType = nameType;
    }

    /**
     * Return a random type
     * @return CardType
     */
    public static CardType random() {
        return values()[RandomUtils.nextInt(1, values().length)];
    }

    public CardNameGenerator.NameType getNameType() {
        return nameType;
    }
}
