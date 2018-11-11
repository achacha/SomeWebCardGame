package org.achacha.webcardgame.game.logic;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.BitSet;

public class DamagePerTurn {
    enum BitOffset {
        PLAYER_ATTACKING(0),
        CRITICAL(1),
        ABSORB(2);

        int offset;
        BitOffset(int offset) {
            this.offset = offset;
        }
    }

    private BitSet bitset = new BitSet();
    int damage;

    DamagePerTurn(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    /**
     * Set bit
     * @param bitOffset bit offset
     */
    public void setBit(BitOffset bitOffset) {
        bitset.set(bitOffset.offset, true);
    }

    /**
     * @param bitOffset bit offset
     * @return if bit set
     */
    public boolean isBit(BitOffset bitOffset) {
        return bitset.get(bitOffset.offset);
    }

    public BitSet getBitSet() {
        return bitset;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("bitset", bitset)
                .append("damage", damage)
                .toString();
    }
}
