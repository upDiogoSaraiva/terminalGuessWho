package org.academiadecodigo.hexallents;

import org.academiadecodigo.hexallents.HelperClasses.CardInfoMessages;

/**
 * Created by GuessWho on 13/03/2018.
 */
public enum CardType {

    RICARDO(CardInfoMessages.RICARDO,
            CardInfoMessages.RICARDO_ASCII),

    CATARINA_CANTO(CardInfoMessages.CATARINA_CANTO,
            CardInfoMessages.CATARINA_CANTO_ASCII),

    TOMAS(CardInfoMessages.TOMAS,
            CardInfoMessages.TOMAS_ASCII),

    JOAO_COSTA(CardInfoMessages.JOAO_COSTA,
            CardInfoMessages.JOAO_COSTA_ASCII),

    DIOGO(CardInfoMessages.DIOGO,
            CardInfoMessages.DIOGO_ASCII),

    CATARINA_MACHADO(CardInfoMessages.CATARINA_MACHADO,
            CardInfoMessages.CATARINA_MACHADO_ASCII),

    RITA(CardInfoMessages.RITA,
            CardInfoMessages.RITA_ASCII),

    JOAO_COELHO(CardInfoMessages.JOAO_COELHO,
            CardInfoMessages.JOAO_COELHO_ASCII),

    FELIPE(CardInfoMessages.FELIPE,
            CardInfoMessages.FELIPE_ASCII),

    JENNIFFER(CardInfoMessages.JENNIFFER,
            CardInfoMessages.JENNIFFER_ASCII),

    RUBEN(CardInfoMessages.RUBEN,
            CardInfoMessages.RUBEN_ASCII),

    HUGO(CardInfoMessages.HUGO,
            CardInfoMessages.HUGO_ASCII),

    MONICA(CardInfoMessages.MONICA,
            CardInfoMessages.MONICA_ASCII),

    JOAO_OLIVEIRA(CardInfoMessages.JOAO_OLIVEIRA,
            CardInfoMessages.JOAO_OLIVEIRA_ASCII),

    ANTONIO(CardInfoMessages.ANTONIO,
            CardInfoMessages.ANTONIO_ASCII),

    GUILHERME(CardInfoMessages.GUILHERME,
            CardInfoMessages.GUILHERME_ASCII),

    SERGIO(CardInfoMessages.SERGIO,
            CardInfoMessages.SERGIO_ASCII),

    BRUNO(CardInfoMessages.BRUNO,
            CardInfoMessages.BRUNO_ASCII),

    BRIGHENTI(CardInfoMessages.BRIGHENTI,
            CardInfoMessages.BRIGHENTI_ASCII),

    CATARINA_CAMPINO(CardInfoMessages.CATARINA_CAMPINO,
            CardInfoMessages.CATARINA_CAMPINO_ASCII);

    private String name;
    private String asci;

    CardType(String name, String asci) {
        this.name = name;
        this.asci = asci;
    }

    public String getName() {
        return name;
    }

    public String getAsci() {
        return asci;
    }
}
