/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compbox.tamagotchi;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Kevin Raoofi
 */
public class Tamagotchi {

    private static final int STARVE_THRESHOLD = 200;

    private static final double FOOD_LOSS_RATIO = 1;
    private static final double FOOD_GAIN_RATIO = 0.5;

    private static final int FULL_THRESHOLD = 800;

    private static final int NORMAL_HEALTH = 1000;
    private static final int NORMAL_FAT = 1000;

    private static final int POOP_POISON_RATIO = 10;

    private static final int SLEEPY_LOW_THRESHOLD = 200;
    private static final int SLEEPY_HIGH_THRESHOLD = 800;

    private double food;
    private double sanitation;
    private int psyche;
    private int health;
    private int age;

    private boolean alive;
    private boolean asleep;

    private final List<String> causesOfDeath = new LinkedList<>();

    private final int fatness;
    private final int toughness;

    private final Random r = new Random();
    private final String name;

    public Tamagotchi(String name) {
        this.name = name;

        this.toughness = r.nextInt(1000);
        this.fatness = r.nextInt(1000);

        this.food = 1000;
        this.psyche = 1000;
        this.alive = true;
        this.sanitation = 1000;

        this.age = 0;
        this.asleep = false;

        this.health = maxHealth();
    }

    public void tick() {
        if (!this.alive) {
            return;
        }

        // handle food
        if (this.food < STARVE_THRESHOLD) {
            this.health -= (STARVE_THRESHOLD - this.food) * FOOD_LOSS_RATIO;
        } else if (this.food > FULL_THRESHOLD) {
            this.health += (this.food - FULL_THRESHOLD) * FOOD_GAIN_RATIO;
        }

        if (this.health < 0) {
            causesOfDeath.add(this.name + " starved to death");
        }

        this.food -= 1 + ((this.fatness - NORMAL_FAT / 2)
                / (double) (NORMAL_FAT));

        // handle sanitation
        this.sanitation -= this.food / 1000;

        if (this.sanitation < 0) {
            this.health += this.sanitation * POOP_POISON_RATIO;
            if (this.health <= 0) {
                causesOfDeath.add(this.name + " died in its own filth");
            }
        }

        // handle psyche
        if (this.asleep) {
            this.psyche = (this.psyche + 80) % 1000;
        } else {
            this.psyche -= 16;
        }

        if (this.psyche < SLEEPY_LOW_THRESHOLD && !this.asleep
                || this.psyche > SLEEPY_HIGH_THRESHOLD && this.asleep) {
            this.asleep = r.nextBoolean();
        }

        if (this.psyche >= 1000) {
            this.asleep = false;
        }

        if (this.psyche < 0) {
            this.health += this.psyche;
            if (this.health <= 0) {
                causesOfDeath.add(this.name + " died of exhaustion");
            }
        }

        // handle age
        this.age++;

        // handle health
        if (this.health <= 0) {
            this.health = 0;
            this.alive = false;
        }
        if (this.health > maxHealth()) {
            this.health = maxHealth();
        }
    }

    public boolean isAlive() {
        return alive;
    }

    private void addHealth(int health) {
        this.health = (this.health + health) % maxHealth();
    }

    public final int maxHealth() {
        return NORMAL_HEALTH + this.toughness - (NORMAL_HEALTH / 2);
    }

    public void putToSleep() {
        this.asleep = true;
    }

    public void feed() {
        this.food = 1000;
    }

    public void clean() {
        this.sanitation = 1000;
    }

    @Override
    public String toString() {
        return "Tamagotchi{" + "food=" + food + ", sanitation=" + sanitation
                + ", psyche=" + psyche + ", health=" + health + ", age=" + age
                + ", alive=" + alive + ", asleep=" + asleep + ", causesOfDeath="
                + causesOfDeath + ", fatness=" + fatness + ", toughness="
                + toughness + ", name=" + name + '}';
    }

    public static void main(String... args) {
        Tamagotchi tamagotchi = new Tamagotchi("Steve");

        for (int i = 0; i < 1000; i++) {
            tamagotchi.tick();
            tamagotchi.feed();
            System.out.println(tamagotchi);
        }
    }

}
