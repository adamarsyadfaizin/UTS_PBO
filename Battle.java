package game;

import java.util.*;

public class Battle {
    private final List<Character> teamA;
    private final List<Character> teamB;

    private static final Map<Character, Integer> maxHp = new HashMap<>();
    private final Map<Character, List<StatusEffect>> effects = new HashMap<>();

    private final List<Map.Entry<Player, StatusEffect>> playerEffects = new ArrayList<>();
    private final List<Map.Entry<Enemy, StatusEffect>> enemyEffects = new ArrayList<>();

    private static int playerTotalDamage = 0;
    private static int enemyTotalDamage = 0;
    private static int bossRageCount = 0;
    private static int shieldAbsorbedTotal = 0;
    private static int regenTotal = 0;

    public Battle(List<Character> teamA, List<Character> teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
        for (Character c : teamA) registerMaxHp(c);
        for (Character c : teamB) registerMaxHp(c);
    }

    public void registerMaxHp(Character c) {
        if (c != null)
            maxHp.putIfAbsent(c, c.getHealth());
    }

    public static int getMaxHp(Character c) {
        return maxHp.getOrDefault(c, c.getHealth());
    }

    public StatusEffect createShield(int flat, int turns) {
        return new ShieldInner(flat, turns);
    }

    public StatusEffect createRegen(int perTurn, int turns) {
        return new RegenInner(perTurn, turns);
    }

    public void addEffectToCharacter(Character target, StatusEffect effect) {
        if (target == null || effect == null) return;
        if (target instanceof Player p) {
            playerEffects.add(new AbstractMap.SimpleEntry<>(p, effect));
            addEffect(p, effect);
            System.out.println("Efek " + effect.getClass().getSimpleName() + " ditambahkan ke " + p.getName());
        } else if (target instanceof Enemy e) {
            enemyEffects.add(new AbstractMap.SimpleEntry<>(e, effect));
            addEffect(e, effect);
            System.out.println("Efek " + effect.getClass().getSimpleName() + " ditambahkan ke " + e.getName());
        }
    }

    public void addEffect(Character c, StatusEffect s) {
        effects.computeIfAbsent(c, k -> new ArrayList<>()).add(s);
    }

    private List<StatusEffect> getEffects(Character c) {
        return effects.getOrDefault(c, Collections.emptyList());
    }

    public static void addBossRage() {
        bossRageCount++;
        System.out.println("[Boss Rage Triggered] Rage count: " + bossRageCount);
    }

    public static void addPlayerDamage(int damage) {
        if (damage > 0) playerTotalDamage += damage;
    }

    public static void addEnemyDamage(int damage) {
        if (damage > 0) enemyTotalDamage += damage;
    }

    public static void applyDamageToTarget(Character target, int incoming, boolean bypassShield) {
        if (incoming <= 0) return;
        Battle current = BattleHolder.INSTANCE;
        if (current == null) {
            target.takeDamage(incoming);
            return;
        }
        int shield = current.totalShieldValue(target, bypassShield);
        int adjusted = Math.max(0, incoming - shield);
        int absorbed = Math.max(0, incoming - adjusted);
        shieldAbsorbedTotal += absorbed;
        target.takeDamage(adjusted);
    }

    private int totalShieldValue(Character c, boolean bypassShield) {
        if (bypassShield) return 0;
        int sum = 0;
        for (StatusEffect s : getEffects(c)) {
            if (s instanceof ShieldInner) sum += ((ShieldInner) s).getFlat();
        }
        return sum;
    }

    private static class BattleHolder {
        static Battle INSTANCE = null;
    }

    private void bindAsCurrent() {
        BattleHolder.INSTANCE = this;
    }

    private int healCharacter(Character c, int amount) {
        if (amount <= 0) return c.getHealth();
        int before = c.getHealth();
        int mx = getMaxHp(c);
        int after = Math.min(mx, before + amount);
        try {
            java.lang.reflect.Method m = Character.class.getDeclaredMethod("setHealth", int.class);
            m.setAccessible(true);
            m.invoke(c, after);
        } catch (Exception ignored) {}
        int healed = after - before;
        regenTotal += healed;
        return after;
    }

    private static class ShieldInner implements StatusEffect {
        private final int flat;
        private int remaining;

        ShieldInner(int flat, int turns) {
            this.flat = Math.max(0, flat);
            this.remaining = Math.max(0, turns);
        }

        public int getFlat() {
            return flat;
        }

        @Override
        public void onTurnStart(Character self) {}

        @Override
        public void onTurnEnd(Character self) {
            if (remaining > 0) remaining--;
        }

        @Override
        public boolean isExpired() {
            return remaining <= 0;
        }

        public String describe() {
            return "Shield(-" + flat + " dmg, " + remaining + " turns)";
        }
    }

    private class RegenInner implements StatusEffect {
        private final int perTurn;
        private int remaining;

        RegenInner(int perTurn, int turns) {
            this.perTurn = Math.max(0, perTurn);
            this.remaining = Math.max(0, turns);
        }

        @Override
        public void onTurnStart(Character self) {}

        @Override
        public void onTurnEnd(Character self) {
            if (remaining <= 0) return;
            healCharacter(self, perTurn);
            remaining--;
        }

        @Override
        public boolean isExpired() {
            return remaining <= 0;
        }

        public String describe() {
            return "Regen(+" + perTurn + " HP, " + remaining + " turns)";
        }
    }

    private boolean isTeamAlive(List<Character> team) {
        for (Character c : team) if (c.isAlive()) return true;
        return false;
    }

    private Character firstAlive(List<Character> team) {
        for (Character c : team) if (c.isAlive()) return c;
        return null;
    }

    public void run() {
        bindAsCurrent();

        System.out.println("=== SETUP ===");
        System.out.println("Team A:");
        for (Character c : teamA) {
            if (c instanceof Player p) {
                System.out.print(" - Player(name=" + p.getName() + ", HP=" + p.getHealth() + "/" + getMaxHp(p)
                        + ", AP=" + p.getAttackPower() + ", Lv=" + p.getLevel() + ", Strategy=LevelScaled(+" +
                        (((LevelScaledStrategy) p.getStrategy()).scale) + "/level))");
                if (!p.getSkills().isEmpty()) {
                    System.out.print("\n   Skills: [");
                    List<String> sn = new ArrayList<>();
                    for (Skill s : p.getSkills()) {
                        if (s instanceof HealSkill) sn.add("HealSkill(+" + ((HealSkill) s).getHealAmount() + ")");
                        else sn.add(s.name());
                    }
                    System.out.print(String.join(", ", sn));
                    System.out.print("]");
                }
                System.out.println();
            } else {
                System.out.println(" - " + c.getClass().getSimpleName() + "(name=" + c.getName() + ", HP=" + c.getHealth() + ")");
            }
        }

        System.out.println("Team B:");
        for (Character c : teamB) {
            if (c instanceof Enemy e) {
                System.out.println(" - " + e.getClass().getSimpleName() + "(name=" + e.getName() + ", HP=" + e.getHealth() + "/" + getMaxHp(e)
                        + ", AP=" + e.getAttackPower() + ", Threat=" + e.getThreatLevel() + ")");
            }
        }

        System.out.println();
        System.out.println("Damage rules:");
        System.out.println(" - Player base damage = AP + Lv*" + (((LevelScaledStrategy) ((Player) firstAlive(teamA)).getStrategy()).scale));
        System.out.println(" - Boss Rage Strike: 2x damage jika HP <50% ATAU setiap turn ke-3");
        System.out.println();

        int turn = 1;
        while (isTeamAlive(teamA) && isTeamAlive(teamB)) {
            System.out.println("=== TURN " + turn + " ===");

            for (Character a : teamA) {
                if (!a.isAlive()) continue;
                Character target = firstAlive(teamB);
                if (target == null) break;
                a.attack(target);
                if (!target.isAlive())
                    System.out.println("    " + target.getName() + " HP: 0 -> (defeated)");
                else
                    System.out.println("    " + target.getName() + " HP: " + target.getHealth());
            }

            for (Character b : teamB) {
                if (!b.isAlive()) continue;
                Character target = firstAlive(teamA);
                if (target == null) break;
                b.attack(target);
                if (!target.isAlive())
                    System.out.println("    " + target.getName() + " HP: 0 -> (defeated)");
                else
                    System.out.println("    " + target.getName() + " HP: " + target.getHealth());
            }

            for (var entry : playerEffects) {
                Player p = entry.getKey();
                StatusEffect eff = entry.getValue();
                if (eff instanceof RegenInner r && p.isAlive()) {
                    int before = p.getHealth();
                    eff.onTurnEnd(p);
                    int after = p.getHealth();
                    if (after > before) {
                        System.out.println("[End Effects] " + p.getName() + " Regen +" + (after - before) + " HP => " + after);
                    }
                }
            }

            System.out.println();
            turn++;
            if (turn > 200) break;
        }

        System.out.println("=== RESULT ===");
        if (isTeamAlive(teamA))
            System.out.println("Team A menang!");
        else
            System.out.println("Team B menang!");

        System.out.println();
        System.out.println("=== STATS ===");
        System.out.println("Total Damage oleh Player: " + playerTotalDamage);
        System.out.println("Total Damage oleh Enemy: " + enemyTotalDamage);
        System.out.println("Total Damage Diserap Shield: " + shieldAbsorbedTotal);
        System.out.println("Total HP dari Regen: " + regenTotal);
    }
}
