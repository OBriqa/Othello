package edu.upc.epsevg.prop.othello.utils;

import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;
import edu.upc.epsevg.prop.othello.CellType;

/**
 * Classe que té varies funcions heurístiques pel joc Othello implementades.
 * @author Omar Briqa, Joaquim Hervas
 */
public class Heuristica {
    
    private CellType Jugador  = CellType.EMPTY;
    private CellType Contrari = CellType.EMPTY;
    
    private final static int A[] = {1, 2, 3, 4, 5, 6, 7}, D[] = {6, 5, 4, 3, 2, 1, 0};
    
    private final static int CORNERCAPTURED = 1000, POTENTIALCORNER = 500, UNLIKELYCORNER = -1000;
    private final static Point CORNERS[] = new Point[] {new Point(0, 0), new Point(7, 0), new Point(7, 7), new Point(0, 7)};
    
    private final static int[][] taulaPuntuacions = {   { 4, -3,  2,  2,  2,  2, -3,  4},
                                                        {-3, -4, -1, -1, -1, -1, -4, -3},
                                                        { 2, -1,  1,  0,  0,  1, -1,  2},
                                                        { 2, -1,  0,  1,  1,  0, -1,  2},
                                                        { 2, -1,  0,  1,  1,  0, -1,  2},
                                                        { 2, -1,  1,  0,  0,  1, -1,  2},
                                                        {-3, -4, -1, -1, -1, -1, -4, -3},
                                                        { 4, -3,  2,  2,  2,  2, -3,  4} };

    /**
     * Constructora de la classe.
     */
    public Heuristica() {}
    
    /**
     * Funció que retorna un valor heurístic donat un tauler d'Othello i el jugador favorable.
     * @param gs Tauler que representa una partida d'Othello.
     * @param player Jugador favorable.
     * @return Valor heurístic donat el tauler d'Othello i el jugador favorable
     */
    public int fHeuristica(GameStatusNPC gs, CellType player){
        Jugador = player;
        Contrari = CellType.opposite(Jugador);        
        return fHStaticWeigh(gs);
    }
    
    /**
     * Funció heurística que utilitza una taula de pesos estàtics.
     * @param gs Tauler que representa una partida d'Othello.
     * @return Valor heurístic.
     */
    private int fHStaticWeigh(GameStatusNPC gs){
        
        int h = 0;
        
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(gs.getPos(i, j) == Jugador)          h += taulaPuntuacions[i][j];
                else if (gs.getPos(i, j) == Contrari)   h -= taulaPuntuacions[i][j];
            }
        }
        
        return h;
    }
    
    /**
     * Funció heurística que utilitza el nombre de peces que té en possesió cada jugador. 
     * @param gs Tauler que representa una partida d'Othello.
     * @return Valor heurístic.
     */
    private int fHCoins(GameStatusNPC gs){
        
        int h = 0;
        int coinsP1 = gs.getPiecesCountP1();
        int coinsP2 = gs.getPiecesCountP2();
        
        if(Jugador == CellType.PLAYER1)
            h = (coinsP1 - coinsP2);
        
        else if(Jugador == CellType.PLAYER2)
            h = (coinsP2 - coinsP1);
                
        return h;
    }
    
    /**
     * Funció heurística que utilitza el nombre de possibles tirades que té cada jugador.
     * @param gs Tauler que representa una partida d'Othello.
     * @return Valor heurístic.
     */
    private int fHMoves(GameStatusNPC gs){
        
        int h = 0, g = 0;
                
        ArrayList<Point> mov = gs.getMoves(); int S = mov.size();
        for(int i = 0; i < S; i++){
            GameStatusNPC nouT = new GameStatusNPC(gs);
            nouT.movePiece(mov.get(i)); g += nouT.getMoves().size();
        }
        
        h = (10 * (S - g/S));
        
        return h;
    }
    
    /**
     * Funció heurística que retorna un nombre aleatori entre un rang.
     * @return Valor heurístic.
     */
    private int fHRandom(){
        int MIN = -10, MAX = 10;
        Random R = new Random();
        return R.nextInt((MAX - MIN) + 1) + MIN;
    }
    
    /**
     * Funció heurística que prioritza la obtenció que peces a les cantonades.
     * @param gs Tauler que representa una partida d'Othello.
     * @return Valor heurístic. 
     */
    private int fHCorner(GameStatusNPC gs){
        
        int h = 0;
        
        CellType Corners[] = new CellType[4];
            Corners[0] = gs.getPos(0, 0);
            Corners[1] = gs.getPos(7, 0);
            Corners[2] = gs.getPos(0, 7);
            Corners[3] = gs.getPos(7, 7);
        
        for(CellType Corner : Corners){
            if(Corner == Jugador)           h += CORNERCAPTURED;
            else if (Corner == Contrari)    h += UNLIKELYCORNER;
        }
                
        ArrayList<Point> moves = gs.getMoves();
        for(Point corner : CORNERS)
            h = (moves.contains(corner)) ? h + POTENTIALCORNER : h + 0;
        
            
        h += (unlikelyCorner(Corners[0], gs, A, 0, A, 0) +
              unlikelyCorner(Corners[1], gs, D, 0, A, 7) + 
              unlikelyCorner(Corners[2], gs, D, 7, D, 7) + 
              unlikelyCorner(Corners[3], gs, A, 7, D, 0));
               
        return h;
    }
    
    /**
     * Funció que comprova si les cantonades buides son ja posicions inabastables
     * @param corner Entitat que representa una cantonada.
     * @param gs Tauler que representa la partida d'Othello.
     * @param COL Vector de columnes.
     * @param fil Fila on estem comprovant.
     * @param FIL Vector de files.
     * @param col Columna on estem comprovant.
     * @return Valor heurístic
     */
    private int unlikelyCorner(CellType corner, GameStatusNPC gs, int COL[], int fil, int FIL[], int col){
        
        if(corner == CellType.EMPTY){
            
            int fN = 0, fC = 0;
            for(int columna : COL){
                fN = (gs.getPos(columna, fil) == Jugador)  ? fN + 1 : fN + 0;
                fC = (gs.getPos(columna, fil) == Contrari) ? fC + 1 : fC + 0;
            }
            
            int cN = 0, cC = 0;
            for(int fila : FIL){
                cN = (gs.getPos(col, fila) == Jugador)  ? cN + 1 : cN + 0;
                cC = (gs.getPos(col, fila) == Contrari) ? cC + 1 : cC + 0;
            }
            
            int dN = 0, dC = 0;
            for(int k = 0; k < 7; k++){
                dN = (gs.getPos(COL[k], FIL[k]) == Jugador)  ? dN + 1 : dN + 0;
                dC = (gs.getPos(COL[k], FIL[k]) == Contrari) ? dC + 1 : dC + 0;
            }
            
            /** ---------------------------------------------------------------------------- **/
            
            if( ((fN == 7) || (fC == 7)) && 
                ((cN == 7) || (cC == 7)) && 
                ((dN == 7) || (dC == 7)) ) return UNLIKELYCORNER;
            
        }
        
        return 0;
    }
    
}
