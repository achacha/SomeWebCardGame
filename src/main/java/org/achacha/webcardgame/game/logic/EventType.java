package org.achacha.webcardgame.game.logic;

public enum EventType {
    Start,

    CardStart,
    CardHealth,
    CardAttack,
    CardAttackCrit,
    CardAttackAbsorb,
    CardAttackCritAbsorb,
    CardDeath,

    PlayerWin,
    PlayerDraw,
    PlayerLose,

    StickerHeal,
    StickerDamage
}
