package org.academiadecodigo.hexallents;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class Menu {

    private Prompt prompt;
    private ServerWorker serverWorker;

    public Menu() {
        prompt = new Prompt(System.in, System.out);
        showMenu();
    }

    private void showMenu() {

        String[] menuOptions = {"Start", "Instructions"};
        MenuInputScanner scanner = new MenuInputScanner(menuOptions);
        scanner.setMessage("Menu Options");

        int answerIndex = prompt.getUserInput(scanner);

        if(answerIndex == 1) {
            System.out.println("Game Started");
        }

        if(answerIndex == 2) {
            System.out.println("Instructions");
        }
    }
}
