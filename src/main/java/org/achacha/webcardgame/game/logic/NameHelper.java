package org.achacha.webcardgame.game.logic;

import org.apache.commons.lang3.RandomUtils;

public class NameHelper {
    private static final String VOWELS = "aeiou";
    private static final String CONSONANTS = "bcdfghjklmnpqrstvwxyz";

    /**
     * @return Random generate name
     */
    public static String generateName(NameType type) {
        StringBuilder name = new StringBuilder(20);
        int nameLength = RandomUtils.nextInt(type.minParts, type.maxParts+1);
        boolean cap = true;
        for (int i=0; i<nameLength; ++i) {
            if (RandomUtils.nextInt(0,100) < type.appostrophePercent && i > 0) {
                name.append("'");
                cap = true;
            }
            char consonant = CONSONANTS.charAt(RandomUtils.nextInt(0, CONSONANTS.length()));
            name.append(cap ? Character.toUpperCase(consonant) : consonant);
            name.append(VOWELS.charAt(RandomUtils.nextInt(0, VOWELS.length())));
            if (RandomUtils.nextInt(0,100) < type.extraVowelPercent)
                name.append(VOWELS.charAt(RandomUtils.nextInt(0, VOWELS.length())));
            cap = false;
        }
        return name.toString();
    }
}
