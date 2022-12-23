package edu.upc.epsevg.prop.othello.utils;

import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.SearchType;
import edu.upc.epsevg.prop.othello.players.npc.PlayerID;

/**
 * Classe que implement l'algorisme miniMax limitat en temps per a cada tirada.
 * @author Omar Briqa, Joaquim Hervás
 */
public class MiniMaxID {
    
    private final Heuristica H;
    private final BaseDades BD;
    private long NUM_NODES = 0L;
    private final PlayerID playerIDS;
    private static long TIMELIMIT = 0L;
    private static long[][][] taulaClaus;
    private CellType Jugador = CellType.EMPTY;
    private int PROF_ASSOLIDA = 0, PROFUNDITAT_MAXIMA = 0;
    private final int VICTORIA = 100000, DERROTA = -100000;
    private long HEURISTIQUES_REUTILITZADES = 0L, COLISIONS = 0;


    /**
     * Constructora de la classe
     * @param aThis Referencia a la classe que representa al jugador que utilitza aquest algorisme.
     * @param player Jugador favorable.
     * @param timeout Temps límit per a cada tirada.
     */
    public MiniMaxID(PlayerID aThis, CellType player, long timeout) {
        
        playerIDS = aThis;
        TIMELIMIT = timeout;
        H = new Heuristica();
        BD = new BaseDades();
        taulaClaus = generaTaulaAlaetoria();
        
    }

    
    /**
     * Funció que retorna el millor moviment de tots els possibles aplicant l'algorisme miniMax
     * @param gs Tauler que representa la partida d'Othello.
     * @param player Jugador a qui li toca tirar.
     * @return Millor moviment on es pot posar una peça segons l'algorisme miniMax.
     */
    public Move miniMaxID(GameStatusNPC gs, CellType player) {
        
        Jugador = player;
        Move movTirar = null, movTirarAnt = null;
        int cActual, alpha, beta, i = 0, millorAnt = -1;
        
        PROFUNDITAT_MAXIMA = 0;
        
        while(true){
            
            cActual = Integer.MIN_VALUE;
            alpha   = Integer.MIN_VALUE;
            beta    = Integer.MAX_VALUE;
                                    
            if(playerIDS.tempsAcabat()) break;
            else{
                PROFUNDITAT_MAXIMA++;
                
                ArrayList<Point> movPossibles = gs.getMoves(); i = 0;
                if(millorAnt != -1) Collections.swap(movPossibles, 0, millorAnt);
                
                for (Point moviment : movPossibles) {
                    GameStatusNPC nouT = new GameStatusNPC(gs); nouT.movePiece(moviment);
                    Integer fH = MinValor(nouT, 1, alpha, beta);
                    if(fH == null){ 
                        movTirar = movTirarAnt; break; 
                    }
                    else if (fH > cActual) {
                        cActual = fH; millorAnt = i;
                        movTirar = new Move(moviment, NUM_NODES,
                                                      PROF_ASSOLIDA, SearchType.MINIMAX_IDS);
                    } i++;
                }
            }
            movTirarAnt = movTirar;   
        }
        
        return movTirar;
    }
    
    /**
     * Funció que retorna la heurística mínima de tots els estats següents possibles a l'estat indicat en el tauler gs.
     * @param gs Tauler que representa una partida d'Othello.
     * @param prof Profunditat actual en la que está la funció MinValor.
     * @param alpha Paràmetre alfa que s'utiliza en la poda alfa-beta.
     * @param beta Paràmetre alfa que s'utiliza en la poda alfa-beta.
     * @return Heurística mínima de tots els estats següents possibles a l'estat indicat en el Tauler gs.
     */
    private Integer MinValor(GameStatusNPC gs, int prof, int alpha, int beta){
        
        int cActual = Integer.MAX_VALUE;
        
        if(playerIDS.tempsAcabat())
            return null;

        else if (gs.checkGameOver())
            cActual = costFinalPartida(gs);
        
        else if (prof == PROFUNDITAT_MAXIMA || gs.getMoves().isEmpty()) {
            NUM_NODES++;
            long hashTauler = hashValue(gs);
            
            if (BD.hiEsH(hashTauler))
                cActual = BD.valorH(hashTauler);
            else {
                cActual = H.fHeuristica(gs, Jugador);
                BD.afegirH(hashTauler, cActual);
            }
        }
        
        else {
            ArrayList<Point> movPossibles = gs.getMoves();
            for (Point moviment : movPossibles) {
                GameStatusNPC nouT = new GameStatusNPC(gs);
                nouT.movePiece(moviment); PROF_ASSOLIDA = prof + 1;
                Integer fHMAX = MaxValor(nouT, prof + 1, alpha, beta);
                if(fHMAX != null) {
                    cActual = Math.min(cActual, fHMAX);
                    beta = Math.min(cActual, beta); if (alpha >= beta) break; 
                } else return null;
            }
        }

        return cActual;
    }
    
    /**
     * Funció que retorna la heurística máxima de tots els estats següents possibles a l'estat indicat en el tauler gs.
     * @param gs Tauler que representa una partida d'Othello.
     * @param prof Profunditat actual en la que está la funció MaxValor.
     * @param alpha Paràmetre alfa que s'utiliza en la poda alfa-beta.
     * @param beta Paràmetre alfa que s'utiliza en la poda alfa-beta.
     * @return Heurística màxima de tots els estats següents possibles a l'estat indicat en el tauler gs.
     */
    private Integer MaxValor(GameStatusNPC gs, int prof, int alpha, int beta){
        
        int cActual = Integer.MIN_VALUE;
        
        if(playerIDS.tempsAcabat())
            return null;
        
        else if (gs.checkGameOver())
            cActual = costFinalPartida(gs);
              
        else if (prof == PROFUNDITAT_MAXIMA || gs.getMoves().isEmpty()) {
            NUM_NODES++;
            long hashTauler = hashValue(gs);
            
            if (BD.hiEsH(hashTauler))
                cActual = BD.valorH(hashTauler);
            else {
                cActual = H.fHeuristica(gs, Jugador);
                BD.afegirH(hashTauler, cActual);
            }
        } 
        else {
            ArrayList<Point> movPossibles = gs.getMoves();
            for (Point moviment : movPossibles) {
                GameStatusNPC nouT = new GameStatusNPC(gs);
                nouT.movePiece(moviment); PROF_ASSOLIDA = prof + 1;
                Integer fHMIN = MinValor(nouT, prof + 1, alpha, beta);
                if(fHMIN != null){
                    cActual = Math.max(cActual, fHMIN); 
                    alpha = Math.max(cActual, alpha); if (alpha >= beta) break;
                } else return null;
            }
        }

        return cActual;
        
    }

    /**
     * Funció que retorna un valor gran en funció si ets guanyador o perdedor de la partida.
     * @param gs Tauler que representa una partida d'Othello.
     * @return Valor que representa la victoria o derrota del jugador.
     */
    private int costFinalPartida(GameStatusNPC gs) {
        return ((gs.GetWinner() == Jugador) ? VICTORIA : DERROTA);
    }
    
    /**
     * Funció que retorna una taula de 2x8x8 de nombres aleatoris de tipus Long.
     * @return Taula de 2x8x8 de nombres aleatoris de tipus Long.
     */
    private long[][][] generaTaulaAlaetoria() {

        Random R = new Random();
        long[][][] taula = new long[2][8][8];
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    taula[k][i][j] = R.nextLong();
                }
            }
        }
        
        return taula;
    }

    /**
     * Funció que retorna del valor de hash Zobrist que representa el tauler gs.
     * @param gs Tauler que representa una partida d'Othello.
     * @return Valor de hash Zobrist que representa el tauler gs.
     */
    private long hashValue(GameStatusNPC gs) {
        
        long h = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(gs.getPosOccupied(i, j)){
                    h = h ^ (taulaClaus[gs.getPosColor(i, j)][i][j]);
                }
            }
        } gs.setHashValue(h);
        
        return h;
    }
    
}
