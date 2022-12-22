package edu.upc.epsevg.prop.othello.players.npc;

import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.Move;

import edu.upc.epsevg.prop.othello.utils.MiniMax;
import edu.upc.epsevg.prop.othello.utils.GameStatusNPC;

/**
 * Classe que representa el jugador que utilitza l'algorisme miniMax limitat en profunditat.
 * @author Omar Briqa, Joaquim Hervas
 */
public class PlayerMiniMax implements IPlayer, IAuto {

    private final MiniMax MM;
    private final String nom;
    private int PROFUNDITAT_MAXIMA = 8;
    private CellType Jugador = CellType.EMPTY;
    
    /**
     * Constructora del jugador que utilitza l'algorisme miniMax limitat en profunditat.
     * @param prof Profunditat màxima de l'arbre d'exploració.
     */
    public PlayerMiniMax(int prof) {
        nom = "NPCMiniMax";
        PROFUNDITAT_MAXIMA = prof;
        MM = new MiniMax(PROFUNDITAT_MAXIMA);
    }
    
    @Override
    public Move move(GameStatus gs) {
        Jugador = gs.getCurrentPlayer();
        GameStatusNPC GS = new GameStatusNPC(gs);
        return MM.miniMax(GS, Jugador);
    }

    @Override
    public void timeout() { }

    @Override
    public String getName() {
        return nom;
    }
    
}
