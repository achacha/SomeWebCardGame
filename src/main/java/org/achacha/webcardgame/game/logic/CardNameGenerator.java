package org.achacha.webcardgame.game.logic;

import org.apache.commons.lang3.RandomUtils;

public class CardNameGenerator {
    private static final String VOWELS = "aeiou";
    private static final String CONSONANTS = "bcdfghjklmnpqrstvwxyz";

    public enum NameType {
        Grue(4, 4, 0, 100, 0, 100),   // There is no such thing as a Grue
        Human(2, 4, 0, 5, 0, 50),
        Elf(2, 4, 30, 75, 0, 0),
        Goblin(1, 2, 0, 0, 10, 65),
        Orc(1, 2, 40, 0, 50, 90);

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

    /**
     * @return Random generate name
     */
    public static String generateName(NameType type) {
        StringBuilder name = new StringBuilder(20);
        int nameLength = RandomUtils.nextInt(type.minParts, type.maxParts + 1);
        boolean cap = true;
        char lastChar = ' ';
        for (int i = 0; i < nameLength; ++i) {
            if (lastChar != '\'' && RandomUtils.nextInt(0, 100) < type.appostrophePercent && i > 0) {
                lastChar = '\'';
                name.append(lastChar);
                cap = true;
            }
            // Starting consonant
            lastChar = CONSONANTS.charAt(RandomUtils.nextInt(0, CONSONANTS.length()));
            name.append(cap ? Character.toUpperCase(lastChar) : lastChar);
            cap = false;

            // Skip vowel if more than 1 part, and if we replace it with '
            if (RandomUtils.nextInt(0, 100) > type.skipVowelPercent && type.maxParts > 1) {
                lastChar = VOWELS.charAt(RandomUtils.nextInt(0, VOWELS.length()));
                name.append(lastChar);
            } else if (lastChar != '\'' && RandomUtils.nextInt(0, 100) < type.appostrophePercent && i < nameLength - 1) {
                lastChar = '\'';
                name.append(lastChar);
            }

            // Extra vowel
            if (RandomUtils.nextInt(0, 100) < type.extraVowelPercent) {
                lastChar = VOWELS.charAt(RandomUtils.nextInt(0, VOWELS.length()));
                name.append(lastChar);
            }
        }

        if (RandomUtils.nextInt(0, 100) < type.trailingConsonantPercent) {
            if (!isVowel(lastChar))
                name.append('\'');

            name.append(CONSONANTS.charAt(RandomUtils.nextInt(0, CONSONANTS.length())));
        }


        return name.toString();
    }

    private static boolean isVowel(char c) {
        return VOWELS.indexOf(c) != -1;
    }
}
