package org.achacha.webcardgame.game.logic;

public enum NameType {
    Human(2,4,0,5,0,50),
    Elf(2,4,30,75,0,0),
    Goblin(1,2,0,0,10,65),
    Orc(1,2,40,0,50,90);

    final int minParts;
    final int maxParts;
    final int appostrophePercent;
    final int extraVowelPercent;
    final int skipVowelPercent;
    final int trailingConsonantPercent;

    NameType(int minParts, int maxParts, int appostrophePercent, int extraVowelPercent, int skipVowelPercent, int trailingConsonantPercent) {
        this.minParts = minParts;
        this.maxParts = maxParts;
        this.appostrophePercent = appostrophePercent;
        this.extraVowelPercent = extraVowelPercent;
        this.skipVowelPercent = skipVowelPercent;
        this.trailingConsonantPercent = trailingConsonantPercent;
    }
}
