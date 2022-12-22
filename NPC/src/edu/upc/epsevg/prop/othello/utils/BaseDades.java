package edu.upc.epsevg.prop.othello.utils;

import java.util.HashMap;

/**
 * Classe que emmagatzema informació sobre les heurístiques dels taulers representats en forma de Long
 * @author Omar Briqa, Joaquim Hervas
 */
public class BaseDades {
        
    HashMap<Long, Integer> BDHeuristiques;
    
    /**
     * Constructora de la classe.
     */
    public BaseDades() {
        BDHeuristiques  = new HashMap<>();
    }
    
    /**
     * Funció que emmagatzema el parell ordenat (key, value)
     * @param key Valor de la clau.
     * @param value Valor associat a la clau.
     */
    public void afegirH(long key, int value){
        BDHeuristiques.put(key, value);
    }
    
    /**
     * Funció que comprova si hi ha emmagatzemada una clau amb el mateix valor que la de paràmetre.
     * @param key Valor de la clau que es busca.
     * @return Cert en cas que hi hagi emmagatzemada una clau que tingui el valor de 'key'.
     */
    public boolean hiEsH(long key){
        return BDHeuristiques.containsKey(key);
    }
    
    /**
     * Funció que retorna el valor associat a la clau 'key'
     * @param key Clau amb la que busquem el valor.
     * @return El valor associat a la clau 'key'
     */
    public int valorH(long key){
        return BDHeuristiques.get(key);
    }
    
    /**
     * Funció que retorna el nombre d'elements emmagatzemades a la classe.
     * @return  Nombre d'elements emmagatzemades a la classe.
     */
    public int getSizeBDH(){
        return BDHeuristiques.size();
    }
    
}
