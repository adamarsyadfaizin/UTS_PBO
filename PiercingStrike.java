package game;

public class PiercingStrike implements Skill {
    private final double multiplier;

    public PiercingStrike(double multiplier) {
        if (multiplier <= 0)
            throw new IllegalArgumentException("Multiplier must be positive");
        this.multiplier = multiplier;
    }

    @Override
    public String name() {
        return "PiercingStrike(x" + multiplier + ")";
    }

    @Override
    public void apply(Character self, Character target) {
        int baseDamage = (int) (self.getAttackPower() * multiplier);
        System.out.println(self.getName() + " menggunakan PiercingStrike! (" + baseDamage + " damage, ignore 25% shield)");
        // bypass 25% shield
        Battle.applyDamageToTarget(target, baseDamage, false); 
    }
}
