package game;

public class ShieldEffect implements StatusEffect {
    private final int flatReduce; // e.g., 10
    private int remainingTurns;

    public ShieldEffect(int flatReduce, int turns) {
        this.flatReduce = Math.max(0, flatReduce);
        this.remainingTurns = Math.max(0, turns);
    }

    public int getFlatReduce() {
        return flatReduce;
    }

    @Override
    public void onTurnStart(Character self) {
        // no immediate action; reduction applied when damage is computed
    }

    @Override
    public void onTurnEnd(Character self) {
        if (remainingTurns > 0) remainingTurns--;
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }

    @Override
    public String describe() {
        return "Shield(-" + flatReduce + " dmg, " + remainingTurns + " turns)";
    }
}
