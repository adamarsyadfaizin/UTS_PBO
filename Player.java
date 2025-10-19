package game;

import java.util.*;

public class Player extends Character {

    private final List<Skill> skills = new ArrayList<>();

    public Player(String name, int hp, int ap, int level, AttackStrategy strategy) {
        super(name, hp, ap, level, strategy == null ? new LevelScaledStrategy(1) : strategy);
    }

    public List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    public void addSkill(Skill s) {
        if (s != null) skills.add(s);
    }

    @Override
    public void attack(Character target) {
        int base = (strategy != null)
            ? strategy.computeDamage(this, target)
            : getAttackPower();

        double pierceMultiplier = 1.0;
        boolean hasPiercing = false;

        for (Skill skill : skills) {
            if (skill.name().toLowerCase().startsWith("piercingstrike")) {
                hasPiercing = true;
                int open = skill.name().indexOf('(');
                int close = skill.name().indexOf(')');
                if (open >= 0 && close > open) {
                    String inside = skill.name().substring(open + 1, close);
                    if (inside.startsWith("x")) {
                        try {
                            pierceMultiplier = Double.parseDouble(inside.substring(1));
                        } catch (Exception e) {
                            pierceMultiplier = 1.0;
                        }
                    }
                }
            }
        }

        int raw = (int) Math.round(base * pierceMultiplier);
        boolean isPiercing = hasPiercing && pierceMultiplier > 1.0;

        if (isPiercing)
            System.out.println("  [Team A] Player -> " + target.getName() + " (PiercingStrike): " + raw + " dmg");
        else
            System.out.println("  [Team A] Player -> " + target.getName() + " (Normal " + base + "): " + raw + " dmg");

        Battle.applyDamageToTarget(target, raw, isPiercing);
        Battle.addPlayerDamage(raw);
    }
}
