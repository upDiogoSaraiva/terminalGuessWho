package org.academiadecodigo.hexallents;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.hexallents.HelperClasses.Messages;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class Menu {

    private Prompt prompt;

    public Menu() {
        prompt = new Prompt(System.in, System.out);
        showMenu();
    }

    private void showMenu() {

        String[] menuOptions = {Messages.GAME_OPTIONS_START, Messages.GAME_OPTIONS_INSTRUCTIONS, Messages.GAME_OPTIONS_QUIT};
        MenuInputScanner scanner = new MenuInputScanner(menuOptions);
        scanner.setMessage(Messages.GAME_OPTIONS);

        int answerIndex = prompt.getUserInput(scanner);

        if(answerIndex == 1) {
            System.out.println(Messages.GAME_STARTED);
        }

        if(answerIndex == 2) {
            System.out.println(Messages.INSTRUCTIONS);
        }

        if(answerIndex == 3) {
            System.out.println(Messages.GAME_OVER);
            System.exit(0);
        }
    }
}
