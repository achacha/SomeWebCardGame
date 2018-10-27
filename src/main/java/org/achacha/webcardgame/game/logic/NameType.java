package org.achacha.webcardgame.game.logic;

public enum NameType {
    Human(2,5,0,0),
    Elf(2,3,30,70),
    Goblin(1,2,0,0);

    final int minParts;
    final int maxParts;
    final int appostrophePercent;
    final int extraVowelPercent;

    NameType(int minParts, int maxParts, int appostrophePercent, int extraVowelPercent) {
        this.minParts = minParts;
        this.maxParts = maxParts;
        this.appostrophePercent = appostrophePercent;
        this.extraVowelPercent = extraVowelPercent;
    }
}
