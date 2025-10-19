package game;

public class Shield implements StatusEffect {
    private int flatReduce;
    private int duration;

    public Shield(int flatReduce, int duration) {
        this.flatReduce = flatReduce;
        this.duration = duration;
    }

    public int getFlatReduce() {
        return flatReduce;
    }

    @Override
    public void onTurnStart(Character self) { }

    @Override
    public void onTurnEnd(Character self) {
        duration--;
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }
}
