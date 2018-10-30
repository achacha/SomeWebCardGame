package org.achacha.webcardgame.game.logic;

public enum CardType {
    Grue(NameType.Grue),
    Human(NameType.Human),
    Elf(NameType.Elf),
    Goblin(NameType.Goblin);

    final NameType nameType;

    CardType(NameType nameType) {
        this.nameType = nameType;
    }

    public NameType getNameType() {
        return nameType;
    }
}
