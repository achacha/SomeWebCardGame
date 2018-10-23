package org.achacha.webcardgame.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.CardStickerDbo;
import org.achacha.webcardgame.game.dbo.CardStickerDboFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class CardStickerDboTest extends BaseInitializedTest {
    private CardStickerDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(CardStickerDbo.class);

    @Test
    public void testLoading() {
        Collection<CardStickerDbo> stickers = factory.getByCardId(TestDataConstants.JUNIT_PLAYER_CARDID);

        Assert.assertNotNull(stickers);
        Assert.assertEquals(3, stickers.size());
    }
}
