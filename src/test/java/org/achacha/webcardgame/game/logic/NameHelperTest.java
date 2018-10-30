package org.achacha.webcardgame.game.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class NameHelperTest {

    @Test
    void generateName() {
        for (int i=0; i<10; ++i)
            assertFalse(NameHelper.generateName(NameType.Human).isBlank());

//        for (NameType type : NameType.values()) {
//            System.out.println(type.name()+"\n-----");
//            for (int i=0; i<10; ++i)
//                System.out.println(NameHelper.generateName(type));
//            System.out.println("\n\n");
//
//        }
    }
}