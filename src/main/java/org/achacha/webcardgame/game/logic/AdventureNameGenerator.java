package org.achacha.webcardgame.game.logic;

import io.github.achacha.dada.engine.builder.SentenceRendererBuilder;
import io.github.achacha.dada.engine.data.Noun;
import io.github.achacha.dada.engine.render.ArticleMode;
import io.github.achacha.dada.engine.render.CapsMode;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class AdventureNameGenerator {

    // TODO: Templates may need to have a better script engine
    private static final List<SentenceRendererBuilder> BUILDERS = new ArrayList<>(10);

    static {
        BUILDERS.add(new SentenceRendererBuilder()
                .text("Stroll around the block looking for")
                .noun(Noun.Form.singular, ArticleMode.a, CapsMode.none)
        );

        // TODO: More
    }

    public static String generateAdventureName(AdventureDbo adventure) {
        int encounters = adventure.getEncounters().size();
        switch(encounters) {
            // TODO: Something better than this
            case 0 : return "Stroll around the block " + RandomUtils.nextInt();
            case 1 : return "Quick resource gathering trip " + RandomUtils.nextInt();
            case 2 : return "Quest for things and stuff " + RandomUtils.nextInt();
            case 3 : return "Difficult quest with lots to do " + RandomUtils.nextInt();
            default: return "Epic Adventure of Enormous Proportions " + RandomUtils.nextInt();
        }
    }
}
