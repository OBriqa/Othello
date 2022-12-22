package edu.upc.epsevg.prop.othello.utils;

import edu.upc.epsevg.prop.othello.GameStatus;

/**
 * Classe que representa un tauler d'Othello.
 * Hereda de la classe GameStatus i incorpora nous atributs i mètodes.
 * @author Omar Briqa, Joaquim Hervas
 */
public class GameStatusNPC extends GameStatus {
    
    private long hashValue = 0;

    /**
     * Constructora per còpia a partir d'un GameStatus.
     * @param gs Objecte GameStatus que representa un tauler d'Othello.
     */
    public GameStatusNPC(GameStatus gs) {
        super(gs);
    }
    
    /**
     * Funció que retorna el nombre total de peces que té en possesió el jugador 1.
     * @return Nombre total de peces que té ara el jugador 1.
     */
    public int getPiecesCountP1(){
        return piecesCountP1;
    }
    
    /**
     * Funció que retorna el nombre total de peces que té en possesió el jugador 2.
     * @return Nombre total de peces que té ara el jugador 2.
     */
    public int getPiecesCountP2(){
        return piecesCountP2;
    }
    
    /**
     * Funció que retorna el nombre total de peces que hi ha al tauler. 
     * @return Nombre total de peces que hi ha al tauler. 
     */
    public int getPiecesCount(){
        return (piecesCountP1 + piecesCountP2);
    }
    
    /**
     * Funció que retorna el bit (format int) que hi ha a la posició (i,j) i que representa el color d'aquella fitxa al tauler.
     * @param i Fila nº i
     * @param j Columna nº j
     * @return 1 en cas que hi hagi una fitxa negra a la posició (i,j), 0 en cas contrari (blanca o buida)
     */
    public int getPosColor(int i, int j){
        return (board_color.get(i + j*8)) ? 1 : 0;
    }
    
    /**
     * Funció que retorna el bit (format bool) que hi ha a la posició (i,j) i que representa l'ocupació d'aquella posició en el tauler.
     * @param i Fila nº i
     * @param j Columna nº j
     * @return Cert en cas que hi hagi una fitxa a la posició (i,j), fals en cas contrari.
     */
    public boolean getPosOccupied(int i, int j){
        return board_occupied.get(i + j*8);
    }
    
    /**
     * Funció que assigna al atribut 'hashValue' el valor de hash Zobrist que representa el tauler.
     * @param hash Valor de hash Zobrist que representa el tauler d'Othello.
     */
    public void setHashValue(long hash){
        this.hashValue = hash;
    }
    
    /**
     * Funció que retorna el valor de hash (si en té) assignat al tauler.
     * @return Valor de hash Zobrist que representa el tauler d'Othello.
     */
    public long getHashValue(){
        return this.hashValue;
    }
    
}
