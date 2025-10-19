package game;

import java.util.*;

public class GameTest {
    public static void main(String[] args) {
        Player player = new Player("HeroVipkas", 120, 25, 5, new LevelScaledStrategy(2));
        player.addSkill(new HealSkill(15));
        player.addSkill(new Skill() {
            @Override public String name() { return "PiercingStrike(x1.2)"; }
            @Override public void apply(Character self, Character target) { }
        });

        BossMonster boss = new BossMonster("Drake", 150, 28, 5, new LevelScaledStrategy(1));
        Monster goblin = new Monster("Goblin", 80, 12, 2, new LevelScaledStrategy(1));

        List<Character> teamA = new ArrayList<>();
        teamA.add(player);

        List<Character> teamB = new ArrayList<>();
        teamB.add(boss);
        teamB.add(goblin);

        Battle battle = new Battle(teamA, teamB);
        battle.run();
    }
}
