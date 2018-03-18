package org.academiadecodigo.hexallents.helpers;

import org.academiadecodigo.hexallents.CardType;

/**
 * Created by codecadet on 18/03/2018.
 */
public class Random {

    public static int generateRandomInt(int max) {
        max = (int) Math.floor(max);
        return (int) Math.floor(Math.random() * (max));
    }

    public static int generateRandomCard() {
        return Random.generateRandomInt(CardType.values().length);
    }

}
