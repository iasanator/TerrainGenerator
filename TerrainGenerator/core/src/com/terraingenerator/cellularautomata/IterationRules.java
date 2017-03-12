package com.terraingenerator.cellularautomata;

import java.util.HashSet;

/**
 * Created by iassona on 3/10/2017.
 */
public class IterationRules {

    private HashSet<Integer> birthConditions;
    private HashSet<Integer> survivalConditions;

    /**
     * Set of rules for the cellular world
     *
     * @param birthConds
     * @param survivalConds
     */
    public IterationRules(int[] birthConds, int[] survivalConds){

        this.setRules(birthConds, survivalConds);

    }

    /**
     * Used to determining whether a cell is alive or not in the next iteration
     *
     * @param isAlive
     * @param neighborsAlive
     * @return
     */
    public boolean determineState(boolean isAlive, int neighborsAlive){

        return isAlive ? this.survivalConditions.contains(neighborsAlive) :
                        this.birthConditions.contains(neighborsAlive);

    }

    /**
     * This sets the rules in the object
     *
     * @param birthConds
     * @param survivalConds
     */
    public void setRules(int[] birthConds, int[] survivalConds){

        this.birthConditions = new HashSet<Integer>();
        this.survivalConditions = new HashSet<Integer>();

        for (int i = 0; i < birthConds.length; i++){
            this.birthConditions.add(birthConds[i]);
        }

        for (int i = 0; i < survivalConds.length; i++){
            this.survivalConditions.add(survivalConds[i]);
        }

    }

}
