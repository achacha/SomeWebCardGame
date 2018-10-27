package org.achacha.webcardgame.game.logic;

import org.junit.Assert;
import org.junit.Test;

public class NameHelperTest {

    @Test
    public void generateName() {
        for (int i=0; i<10; ++i)
            Assert.assertFalse(NameHelper.generateName(NameType.Human).isBlank());

//        for (int i=0; i<10; ++i)
//            System.out.println(NameHelper.generateName(NameType.Human));
//
//        for (int i=0; i<10; ++i)
//            System.out.println(NameHelper.generateName(NameType.Elf));
//
//        for (int i=0; i<10; ++i)
//            System.out.println(NameHelper.generateName(NameType.Goblin));
    }
}