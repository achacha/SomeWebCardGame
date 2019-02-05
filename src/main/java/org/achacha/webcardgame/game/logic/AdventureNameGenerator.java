package org.achacha.webcardgame.game.logic;

import com.google.common.annotations.VisibleForTesting;
import io.github.achacha.dada.engine.builder.SentenceRendererBuilder;
import io.github.achacha.dada.engine.data.Adjective;
import io.github.achacha.dada.engine.data.Verb;
import io.github.achacha.dada.engine.render.ArticleMode;
import io.github.achacha.dada.engine.render.CapsMode;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdventureNameGenerator {

    // Templates based on how many encounters an adventure has
    private static final List<SentenceRendererBuilder> BUILDERS_0 = new ArrayList<>(10);
    private static final List<SentenceRendererBuilder> BUILDERS_1 = new ArrayList<>(10);
    private static final List<SentenceRendererBuilder> BUILDERS_2 = new ArrayList<>(10);
    private static final List<SentenceRendererBuilder> BUILDERS_3 = new ArrayList<>(10);
    private static final List<SentenceRendererBuilder> BUILDERS_4 = new ArrayList<>(10);


    static {
        buildBuilders0();
        buildBuilders1();
        buildBuilders2();
        buildBuilders3();
        buildBuilders4();
    }

    /**
     * Get text for an adventure
     * @param adventure AdventureDbo
     * @return String text from template based on adventure
     */
    public static String generateAdventureName(AdventureDbo adventure) {
        int encounters = adventure.getEncounters().size();
        return generateAdventureNameByEncounterCount(encounters);
    }

    @NotNull
    @VisibleForTesting
    protected static String generateAdventureNameByEncounterCount(int encounters) {
        switch(encounters) {
            case 0 : return BUILDERS_0.get(RandomUtils.nextInt(0, BUILDERS_0.size())).execute();
            case 1 : return BUILDERS_1.get(RandomUtils.nextInt(0, BUILDERS_1.size())).execute();
            case 2 : return BUILDERS_2.get(RandomUtils.nextInt(0, BUILDERS_2.size())).execute();
            case 3 : return BUILDERS_3.get(RandomUtils.nextInt(0, BUILDERS_3.size())).execute();
            case 4 : return BUILDERS_4.get(RandomUtils.nextInt(0, BUILDERS_4.size())).execute();
            default: throw new RuntimeException("Adventure contains unexpected encounter count: "+encounters);
        }
    }

    /**
     * 0 encounter templates
     */
    public static void buildBuilders0() {
        BUILDERS_0.add(new SentenceRendererBuilder()
                .text("Gather ")
                .adjective(Adjective.Form.positive, ArticleMode.a, CapsMode.none)
                .text(" ")
                .noun()
        );
    }

    /**
     * 1 encounter templates
     */
    public static void buildBuilders1() {
        BUILDERS_1.add(new SentenceRendererBuilder()
                .text("Quick ")
                .verb()
                .text(" of ")
                .adjective(Adjective.Form.positive, ArticleMode.a, CapsMode.none)
                .text(" ")
                .noun()
        );
    }

    /**
     * 2 encounter templates
     */
    public static void buildBuilders2() {
        BUILDERS_2.add(new SentenceRendererBuilder()
                .text("Short ")
                .verb()
                .text(" ")
                .conjunction()
                .text(" ")
                .adjective(Adjective.Form.positive, ArticleMode.a, CapsMode.none)
                .text(" ")
                .noun()
        );
    }

    /**
     * 3 encounter templates
     */
    public static void buildBuilders3() {
        BUILDERS_3.add(new SentenceRendererBuilder()
                .text("Lengthy ")
                .verb()
                .text(" for ")
                .adjective(Adjective.Form.comparative, ArticleMode.a, CapsMode.none)
                .text(" ")
                .noun()
        );
    }

    /**
     * 4 encounter templates
     */
    public static void buildBuilders4() {
        BUILDERS_4.add(new SentenceRendererBuilder()
                .text("Involved and ")
                .verb(Verb.Form.past)
                .text(" ")
                .verb(Verb.Form.present)
                .text(" for ")
                .adjective(Adjective.Form.superlative, ArticleMode.the, CapsMode.none)
                .text(" ")
                .adjective()
                .text(" ")
                .noun()
        );
    }
}
