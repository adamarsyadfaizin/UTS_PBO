package game;

public class Enemy extends Character {

    private int threatLevel;

    public Enemy(String name, int hp, int ap, int threat) {
        super(name, hp, ap, 1, new FixedStrategy()); 
        this.threatLevel = threat;
    }

    public Enemy(String name, int hp, int ap, int threat, AttackStrategy strategy) {
        super(name, hp, ap, 1, strategy);
        this.threatLevel = threat;
    }

    public int getThreatLevel() {
        return threatLevel;
    }

    @Override
    public void attack(Character target) {
        int dmg = strategy.computeDamage(this, target);
        System.out.println("  [Team B] Enemy -> " + target.getName() + " : " + dmg + " dmg");
        Battle.applyDamageToTarget(target, dmg, false);
        Battle.addEnemyDamage(dmg);
    }
}
