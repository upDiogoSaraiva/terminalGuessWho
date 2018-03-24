package org.academiadecodigo.hexallents.menu;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.hexallents.HelperClasses.Messages;

/**
 * Created by codecadet on 24/03/2018.
 */
public class InstructionMenu {

    private Prompt prompt;

    public InstructionMenu() {
        prompt = new Prompt(System.in, System.out);
        showInst();
    }

    public void showInst() {

        String[] menuOptions = {Messages.GAME_OPTIONS_START, Messages.GAME_OPTIONS_QUIT};
        MenuInputScanner scanner = new MenuInputScanner(menuOptions);
        scanner.setMessage(Messages.GAME_OPTIONS);

        int answerIndex = prompt.getUserInput(scanner);

        if (answerIndex == 1) {
            return;
        }

        if (answerIndex == 2) {
            System.out.println(Messages.GAME_OVER);
            System.exit(0);
            // é melhor fazer return, mais nito
        }
    }

}
