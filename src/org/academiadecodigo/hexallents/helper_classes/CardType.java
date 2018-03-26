package org.academiadecodigo.hexallents.helper_classes;

import static org.academiadecodigo.hexallents.helper_classes.CardInfo.*;

/**
 * Created by GuessWho on 13/03/2018.
 */
public enum CardType {

    RICARDO(RICARDO_NAME,
            RICARDO_ASCII),

    CATARINA_CANTO(CATARINA_CANTO_NAME,
            CATARINA_CANTO_ASCII),

    TOMAS(TOMAS_NAME,
            TOMAS_ASCII),

    JOAO_COSTA(JOAO_COSTA_NAME,
            JOAO_COSTA_ASCII),

    DIOGO(DIOGO_NAME,
            DIOGO_ASCII),

    CATARINA_MACHADO(CATARINA_MACHADO_NAME,
            CATARINA_MACHADO_ASCII),

    RITA(RITA_NAME,
            RITA_ASCII),

    JOAO_COELHO(JOAO_COELHO_NAME,
            JOAO_COELHO_ASCII),

    FELIPE(FELIPE_NAME,
            FELIPE_ASCII),

    JENNIFFER(JENNIFFER_NAME,
            JENNIFFER_ASCII),

    RUBEN(RUBEN_NAME,
            RUBEN_ASCII),

    HUGO(HUGO_NAME,
            HUGO_ASCII),

    MONICA(MONICA_NAME,
            MONICA_ASCII),

    JOAO_OLIVEIRA(JOAO_OLIVEIRA_NAME,
            JOAO_OLIVEIRA_ASCII),

    ANTONIO(ANTONIO_NAME,
            ANTONIO_ASCII),

    GUILHERME(GUILHERME_NAME,
            GUILHERME_ASCII),

    SERGIO(SERGIO_NAME,
            SERGIO_ASCII),

    BRUNO(BRUNO_NAME,
            BRUNO_ASCII),

    BRIGHENTI(BRIGHENTI_NAME,
            BRIGHENTI_ASCII),

    CATARINA_CAMPINO(CATARINA_CAMPINO_NAME,
            CATARINA_CAMPINO_ASCII);

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