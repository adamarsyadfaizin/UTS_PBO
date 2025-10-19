package game;

public class FixedStrategy implements AttackStrategy {
    @Override
    public int computeDamage(Character self, Character target) {
        return self.getAttackPower(); 
    }
}
