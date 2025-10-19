package game;

import java.util.Random;

public class Monster extends Enemy {

    private final Random rand = new Random();

    public Monster(String name, int hp, int ap, int threatLevel, AttackStrategy strategy) {
        super(name, hp, ap, threatLevel, strategy);
    }

    @Override
    public void attack(Character target) {
        int base = strategy.computeDamage(this, target);
        double factor = 0.9 + rand.nextDouble() * 0.4; 
        int dmg = (int)Math.round(base * factor);
        System.out.println("  [Team B] " + getName() + " -> " + target.getName() + " (Normal " + base + "): " + dmg + " dmg");
        Battle.applyDamageToTarget(target, dmg, false);
    }
}
