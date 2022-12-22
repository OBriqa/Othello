package edu.upc.epsevg.prop.othello.utils;

import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.SearchType;

/**
 * Classe que implement l'algorisme miniMax amb profunditat limitada.
 * @author Omar Briqa, Joaquim Hervas
 */
public class MiniMax {

    private final Heuristica H;
    private final BaseDades BD;
    private static long[][][] taulaClaus;
    private CellType Jugador = CellType.EMPTY;
    private final int VICTORIA = 100000, DERROTA = -100000;
    private long NUM_NODES = 0L, HEURISTIQUES_REUTILITZADES = 0L;
    private int PROFUNDITAT_MAXIMA = 0, PROF_ASSOLIDA = 0, COLISIONS = 0;

    /**
     * Constructora de la classe.
     * @param prof Profunditat límit fins a on l'algorisme pot cercar.
     */
    public MiniMax(int prof) {
        H = new Heuristica();
        BD = new BaseDades();
        PROFUNDITAT_MAXIMA = prof;
        taulaClaus = generaTaulaAlaetoria();
    }

    /**
     * Funció que retorna el millor moviment de tots els possibles aplicant l'algorisme miniMax
     * @param gs Tauler que representa la partida d'Othello.
     * @param player Jugador a qui li toca tirar.
     * @return Millor moviment on es pot posar una peça segons l'algorisme miniMax.
     */
    public Move miniMax(GameStatusNPC gs, CellType player) {

        Move movTirar;
        Jugador = player;

        int cActual = Integer.MIN_VALUE;
        int alpha   = Integer.MIN_VALUE;
        int beta    = Integer.MAX_VALUE;

        if (!gs.getMoves().isEmpty())   movTirar = new Move(gs.getMoves().get(0), 0L, 0, SearchType.MINIMAX);
        else                            movTirar = null;
        

        ArrayList<Point> movPossibles = gs.getMoves();
        for (Point moviment : movPossibles) {
            GameStatusNPC nouT = new GameStatusNPC(gs);
            nouT.movePiece(moviment);
            int fH = MinValor(nouT, 1, alpha, beta);
            if (fH > cActual) {
                cActual = fH;
                movTirar = new Move(moviment, NUM_NODES,
                                              PROF_ASSOLIDA, SearchType.MINIMAX);
            }
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
    private int MinValor(GameStatusNPC gs, int prof, int alpha, int beta) {

        int cActual = Integer.MAX_VALUE;

        if (gs.checkGameOver())
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
                int fHMAX = MaxValor(nouT, prof + 1, alpha, beta);
                cActual = Math.min(cActual, fHMAX);
                beta = Math.min(cActual, beta); if (alpha >= beta) break;
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
    private int MaxValor(GameStatusNPC gs, int prof, int alpha, int beta) {

        int cActual = Integer.MIN_VALUE;

        if (gs.checkGameOver())
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
                int fHMIN = MinValor(nouT, prof + 1, alpha, beta);
                cActual = Math.max(cActual, fHMIN); 
                alpha = Math.max(cActual, alpha); if (alpha >= beta) break;
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
        }
        
        return h;
    }

}
