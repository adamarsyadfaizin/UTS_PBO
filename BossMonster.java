package game;

public class BossMonster extends Enemy {

    private int turnCounter = 0;

    public BossMonster(String name, int hp, int ap, int threat) {
        super(name, hp, ap, threat);
        setStrategy(new FixedStrategy());
    }

    public BossMonster(String name, int hp, int ap, int threat, AttackStrategy strategy) {
        super(name, hp, ap, threat);
        setStrategy(strategy == null ? new FixedStrategy() : strategy);
    }

    @Override
    public void attack(Character target) {
        turnCounter++;
        boolean rage = (getHealth() < (0.5 * 150)) || (turnCounter % 3 == 0);
        int dmg = getAttackPower();
        if (rage) dmg *= 2;

        System.out.println("  [Team B] BossMonster -> " + target.getName()
                + (rage ? " (RAGE STRIKE)" : "") + ": " + dmg + " dmg");

        Battle.applyDamageToTarget(target, dmg, false);
        Battle.addEnemyDamage(dmg);
    }
}
