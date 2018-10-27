package org.achacha.webcardgame.game.logic;

public enum EnemyType {
    Human(NameType.Human),
    Elf(NameType.Elf),
    Goblin(NameType.Goblin);

    final NameType nameType;

    EnemyType(NameType nameType) {
        this.nameType = nameType;
    }

    public NameType getNameType() {
        return nameType;
    }
}
