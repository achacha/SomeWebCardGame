package org.achacha.webcardgame.game.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdventureNameGeneratorTest {

    @Test
    void generateAdventureNameByEncounterCount() {
//        System.out.println("---0---");
//        for (int i=0; i<5; ++i)
//            System.out.println(AdventureNameGenerator.generateAdventureNameByEncounterCount(0));
//
//        System.out.println("---1---");
//        for (int i=0; i<5; ++i)
//            System.out.println(AdventureNameGenerator.generateAdventureNameByEncounterCount(1));
//
//        System.out.println("---2---");
//        for (int i=0; i<5; ++i)
//            System.out.println(AdventureNameGenerator.generateAdventureNameByEncounterCount(2));
//
//        System.out.println("---3---");
//        for (int i=0; i<5; ++i)
//            System.out.println(AdventureNameGenerator.generateAdventureNameByEncounterCount(3));
//
//        System.out.println("---4---");
//        for (int i=0; i<5; ++i)
//            System.out.println(AdventureNameGenerator.generateAdventureNameByEncounterCount(4));

        assertNotNull(AdventureNameGenerator.generateAdventureNameByEncounterCount(0));
        assertThrows(RuntimeException.class, ()->{
            AdventureNameGenerator.generateAdventureNameByEncounterCount(5);
        });
    }
}