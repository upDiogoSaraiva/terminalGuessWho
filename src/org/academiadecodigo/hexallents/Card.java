package org.academiadecodigo.hexallents;

/**
 * Created by GuessWho on 13/03/2018.
 */
public enum Card {

    RICARDO("ricardo"),
    CATARINA_CANTO("catarina canto"),
    TOMAS("tomas"),
    JOAO_COSTA("joao costa"),
    DIOGO("diogo"),
    CATARINA_MACHADO("catarina machado"),
    RITA("rita"),
    JOAO_COELHO("joao coelho"),
    FELIPE("felipe"),
    JENNIFFER("jenniffer"),
    RUBEN("ruben"),
    HUGO("hugo"),
    MONICA("monica"),
    JOAO_OLIVEIRA("joao oliveira"),
    ANTONIO("antonio"),
    GUILHERME("guilherme"),
    SERGIO("sergio"),
    BRUNO("bruno"),
    BRIGHENTI("brighenti"),
    CATARINA_CAMPINO("catarina campino");

    private String asci;

    Card(String asci) {
        this.asci = asci;
    }

    public String getAsci() {
        return asci;
    }
}
