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

<<<<<<< HEAD:src/org/academiadecodigo/hexallents/menu/Menu.java
        String[] menuOptions = {Messages.GAME_OPTIONS_START, Messages.GAME_OPTIONS_QUIT};
=======
        String[] menuOptions =
                { Messages.GAME_OPTIONS_START,
                Messages.GAME_OPTIONS_INSTRUCTIONS,
                Messages.GAME_OPTIONS_QUIT };

>>>>>>> 306895357b701f575fd19e587c874f81522736dc:src/org/academiadecodigo/hexallents/Menu.java
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
