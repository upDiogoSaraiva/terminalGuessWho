package org.academiadecodigo.hexallents.helper_classes;

/**
 * Created by codecadet on 18/03/2018.
 */
public class Random {

    private static int generateRandomInt(int max) {
        max = (int) Math.floor(max);
        return (int) Math.floor(Math.random() * (max));
    }

    public static int generateRandomCard() {
        return Random.generateRandomInt(CardType.values().length);
    }

}
