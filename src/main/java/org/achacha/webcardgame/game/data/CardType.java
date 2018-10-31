package org.achacha.webcardgame.game.data;

import org.achacha.webcardgame.game.logic.NameHelper;

public enum CardType {
    Grue(NameHelper.NameType.Grue),
    Human(NameHelper.NameType.Human),
    Elf(NameHelper.NameType.Elf),
    Goblin(NameHelper.NameType.Goblin);

    final NameHelper.NameType nameType;

    CardType(NameHelper.NameType nameType) {
        this.nameType = nameType;
    }

    public NameHelper.NameType getNameType() {
        return nameType;
    }
}
