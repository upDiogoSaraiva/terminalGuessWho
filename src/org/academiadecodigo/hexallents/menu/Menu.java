package org.academiadecodigo.hexallents.menu;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.hexallents.HelperClasses.Messages;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class Menu {

    private Prompt prompt;
    private InstructionMenu instructionMenu;

    public Menu() {
        prompt = new Prompt(System.in, System.out);
        showMenu();
    }

    public void showMenu() {

        String[] menuOptions = {Messages.GAME_OPTIONS_START, Messages.GAME_OPTIONS_QUIT};
        MenuInputScanner scanner = new MenuInputScanner(menuOptions);
        scanner.setMessage(Messages.GAME_OPTIONS);

        int answerIndex = prompt.getUserInput(scanner);

        if(answerIndex == 1) {
            System.out.println(Messages.INSTRUCTIONS);
            System.out.println(Messages.GAME_STARTED);
        }

        if(answerIndex == 3) {
            System.out.println(Messages.GAME_OVER);
            System.exit(0);
        }
    }

    public void setInstructionMenu(InstructionMenu instructionMenu) {
        this.instructionMenu = instructionMenu;
    }
}
