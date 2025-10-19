package game;

public class Regen implements StatusEffect {
    private int perTurn;
    private int duration;

    public Regen(int perTurn, int duration) {
        this.perTurn = perTurn;
        this.duration = duration;
    }

    @Override
    public void onTurnStart(Character self) {
        self.setHealth(self.getHealth() + perTurn);
        System.out.println("  " + self.getName() + " regenerates +" + perTurn + " HP (" + duration + " turns left)");
    }

    @Override
    public void onTurnEnd(Character self) {
        duration--;
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }
}
