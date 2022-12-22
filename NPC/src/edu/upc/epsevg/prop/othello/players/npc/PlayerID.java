package edu.upc.epsevg.prop.othello.players.npc;

import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.Move;

import edu.upc.epsevg.prop.othello.utils.MiniMaxID;
import edu.upc.epsevg.prop.othello.utils.GameStatusNPC;

/**
 * Classe que representa el jugador que utilitza l'algorisme miniMax limitat en temps.
 * @author Omar Briqa, Joaquim Hervas
 */
public class PlayerID implements IPlayer, IAuto{
    
    private final String nom;
    private final MiniMaxID MM;
    private long TIMELIMIT = 2000;
    private boolean tempsAcabat = false;
    private CellType Jugador = CellType.EMPTY;

    /**
     * Constructora del jugador que utilitza l'algorisme miniMax limitat en temps.
     * @param sec Nombre de segons màxim per cada tirada.
     */
    public PlayerID(int sec) {
        nom = "NPCMiniMaxIDS";
        TIMELIMIT = sec * 1000;
        MM = new MiniMaxID(this, Jugador, TIMELIMIT);
    }
    
    @Override
    public Move move(GameStatus gs) {
        tempsAcabat = false;
        Jugador = gs.getCurrentPlayer();
        GameStatusNPC GS = new GameStatusNPC(gs);
        return MM.miniMaxID(GS, Jugador);
    }

    @Override
    public void timeout() {
        tempsAcabat = true;
    }
    
    /**
     * Funció que retorna si s'ha acabat el temps límit per explorar durant la jugada.
     * @return Cert en cas de que s'hagi agotat el temps per explorar nodes durant la jugada.
     */
    public boolean tempsAcabat(){
        return tempsAcabat;
    }

    @Override
    public String getName() {
        return nom;
    }
    
}
