package game;

import java.util.*;

public abstract class Character {
    private final String name;
    private int health;
    private int attackPower;
    private int level; 
    protected AttackStrategy strategy;
    private final List<StatusEffect> effects = new ArrayList<>();
    public Character(String name, int health, int attackPower, int level, AttackStrategy strategy) {
        if (health < 0 || attackPower < 0)
            throw new IllegalArgumentException("Health dan Attack Power tidak boleh negatif!");
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.level = level;
        this.strategy = strategy;
    }
    public abstract void attack(Character target);
    public final boolean isAlive() {
        return health > 0;
    }
    public final void performTurn(Character target) {
        if (!isAlive()) return;

        for (StatusEffect e : effects)
            e.onTurnStart(this);

        attack(target);

        for (StatusEffect e : effects)
            e.onTurnEnd(this);

        effects.removeIf(StatusEffect::isExpired);
    }
    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getAttackPower() { return attackPower; }
    public int getLevel() { return level; } 
    public AttackStrategy getStrategy() { return strategy; }
    public void setHealth(int health) {
        this.health = Math.max(0, health);
    }
    public void takeDamage(int amount) {
        if (amount <= 0) return;
        this.health = Math.max(0, this.health - amount);
    }
    public void heal(int amount, int maxHp) {
        if (amount <= 0) return;
        this.health = Math.min(maxHp, this.health + amount);
    }
    public void addEffect(StatusEffect e) {
        if (e != null) effects.add(e);
    }
    public List<StatusEffect> getEffects() {
        return effects;
    }
    public void setStrategy(AttackStrategy strategy) {
        this.strategy = strategy;
    }
    public int onIncomingDamage(int damage, boolean piercing) {
        if (!piercing) {
            for (StatusEffect e : effects) {
                if (e instanceof Shield s) {
                    damage = Math.max(0, damage - s.getFlatReduce());
                    System.out.println("  " + name + " shield absorbs " + s.getFlatReduce() + " damage!");
                }
            }
        }
        return damage;
    }
}
