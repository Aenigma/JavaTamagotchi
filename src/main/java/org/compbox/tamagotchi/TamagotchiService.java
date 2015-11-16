/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compbox.tamagotchi;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Kevin Raoofi
 */
public class TamagotchiService {

    private final Tamagotchi tamagotchi;

    private final ExecutorService es;
    private final ScheduledExecutorService ses;

    public TamagotchiService(Tamagotchi tamagotchi) {
        this.es = Executors.newSingleThreadExecutor();
        this.ses = Executors.newScheduledThreadPool(1);
        this.tamagotchi = tamagotchi;
    }

    public void init() {
        this.es.submit(() -> {
            final Scanner sc = new Scanner(System.in);

            while (true) {
                String in = sc.nextLine()
                        .toLowerCase()
                        .trim();

                switch (in) {
                    case "exit":
                        es.shutdown();
                        ses.shutdown();
                        return;
                    case "feed":
                        es.submit(() -> tamagotchi.feed());
                        break;
                    case "status":
                        System.out.println(tamagotchi);
                        break;
                    case "clean":
                        es.submit(() -> tamagotchi.clean());
                        break;
                    case "sleep":
                        es.submit(() -> tamagotchi.putToSleep());
                        break;
                    case "tick":
                        es.submit(() -> tamagotchi.tick());
                        break;
                    default:
                        System.out.println(
                                "Supported commands: feed, sleep, status,"
                                + "clean, exit");
                }
            }
        });

        this.ses.scheduleAtFixedRate(() -> {
            tamagotchi.tick();
            System.out.println("TICK: " + tamagotchi);
            if (!tamagotchi.isAlive()) {
                ses.shutdown();
            }
        }, 1, 1, TimeUnit.MINUTES);

    }

    public static void main(String... args) {
        final Scanner sc = new Scanner(System.in);
        System.out.println("Enter name: ");
        String name = sc.nextLine();
        Tamagotchi t = new Tamagotchi(name);
        TamagotchiService ts = new TamagotchiService(t);
        ts.init();

    }

}
