package game;

public class HealSkill implements Skill {
    private final int healAmount;

    public HealSkill(int healAmount) {
        if (healAmount <= 0)
            throw new IllegalArgumentException("Heal amount must be positive");
        this.healAmount = healAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }

    @Override
    public String name() {
        return "HealSkill(+" + healAmount + ")";
    }

    @Override
    public void apply(Character self, Character target) {
        int before = self.getHealth();
        int maxHp = Battle.getMaxHp(self);
        int after = Math.min(maxHp, before + healAmount);
        self.setHealth(after);
        System.out.println(self.getName() + " menggunakan HealSkill dan memulihkan " + (after - before) + " HP (sekarang: " + after + ")");
    }
}
