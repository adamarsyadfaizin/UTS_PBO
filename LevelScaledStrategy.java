package game;

public class LevelScaledStrategy implements AttackStrategy {
    public final int scale; 
    
    public LevelScaledStrategy(int scale) {
        this.scale = scale;
    }

    @Override
    public int computeDamage(Character self, Character target) {
        return self.getAttackPower() + (self.getLevel() * scale);
    }
}
