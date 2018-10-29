package org.achacha.webcardgame.game.logic;

import org.junit.Assert;
import org.junit.Test;

public class NameHelperTest {

    @Test
    public void generateName() {
        for (int i=0; i<10; ++i)
            Assert.assertFalse(NameHelper.generateName(NameType.Human).isBlank());

//        for (NameType type : NameType.values()) {
//            System.out.println(type.name()+"\n-----");
//            for (int i=0; i<10; ++i)
//                System.out.println(NameHelper.generateName(type));
//            System.out.println("\n\n");
//
//        }
    }
}