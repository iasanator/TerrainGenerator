package com.terraingenerator.cellularautomata;

import java.util.ArrayDeque;

/**
 * Created by iassona on 3/10/2017.
 *
 * This is the object that will represent a world for cells to live and grow on.
 *
 */
public class CellularWorld {

    private int sizeX, sizeY;
    private boolean[][] world;
    private IterationRules iterationRules;

    /**
     * Creates a new world with a specified starting state
     *
     * @param beginningState
     * @param iterationRules
     */
    public CellularWorld(boolean[][] beginningState, IterationRules iterationRules){

        this.sizeX = beginningState.length;
        this.sizeY = beginningState[0].length;

        this.world = beginningState;

        this.iterationRules = iterationRules;

    }

    /**
     * Creates a new world with a given size and random starting state
     *
     * @param sizeX
     * @param sizeY
     * @param densityAlive
     * @param iterationRules
     */
    public CellularWorld(int sizeX, int sizeY, float densityAlive, IterationRules iterationRules){

        this.generateRandomWorld(sizeX, sizeY, densityAlive);

        this.iterationRules = iterationRules;

    }

    /**
     * This will populate the world with a random distribution of cells
     * @param densityAlive
     */
    public void generateRandomWorld(int sizeX, int sizeY, float densityAlive){

        boolean[][] newWorld = new boolean[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++){
            for (int j = 0; j < sizeY; j++){
                if (Math.random() < densityAlive) {
                    newWorld[i][j] = true;
                } else {
                    newWorld[i][j] = false;
                }
            }
        }

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.world = newWorld;

    }

    /**
     * Iterates until stability has been achieved.
     * Returns true if successful; false if unsuccessful.
     *
     * @return
     */
    public boolean iterateUntilStable(){

        ArrayDeque<boolean[][]> previousStates = new ArrayDeque<boolean[][]>();

        int iterations = 0;

        while (iterations < 1000) {

            this.iterate();

            for (boolean[][] previousState : previousStates){
                if (this.isEqual(previousState)){
                    return true;
                }
            }

            if (previousStates.size() > 10){
                previousStates.removeFirst();
            }

            previousStates.addLast(this.world);

            iterations++;
        }

        return false;
    }

    /**
     * Iterates the world by one time step
     */
    public void iterate(){

        boolean[][] newWorld = new boolean[sizeX][sizeY];

        for (int i = 0; i < this.sizeX; i++){
            for (int j = 0; j < this.sizeY; j++){

                int neighbors = 0;

                for (int xVal : new int[]{-1, 0, 1}){
                    for (int yVal : new int[]{-1, 0, 1}){

                        int xPos = (i + xVal) % this.sizeX;
                        if (xPos < 0){xPos += this.sizeX;}

                        int yPos = (j + yVal) % this.sizeY;
                        if (yPos < 0){yPos += this.sizeY;}

                        if (this.world  [xPos][yPos]){
                            neighbors++;
                        }
                    }
                }

                if (this.world[i][j]) {neighbors--;}

                newWorld[i][j] = this.iterationRules.determineState(this.world[i][j], neighbors);

            }
        }

        this.world = newWorld;

    }

    /**
     * Checks if this world's state is equal to another given world's state
     *
     * @param state
     * @return
     */
    private boolean isEqual(boolean[][] state){

        if (this.sizeX != state.length || this.sizeY != state[0].length) {
            return false;
        }

        for (int i = 0; i < this.sizeX; i++){
            for (int j = 0; j < this.sizeY; j++){
                if (this.world[i][j] != state[i][j]){
                    return false;
                }
            }
        }

        return true;

    }

    /**
     * Prints the world to the console in a user-friendly manner
     */
    public void printWorld(){

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < this.sizeX; i++) {

            output.append('[');

            for (int j = 0; j < this.sizeY; j++) {
                if (this.world[i][j]) {
                    output.append('@');
                } else {
                    output.append('.');
                }
            }

            output.append("]\n");

        }

        output.append('\n');

        System.out.print(output.toString());
    }

    public int getPopulation(){

        int population = 0;

        for (int i = 0; i < this.sizeX; i++){
            for (int j = 0; j < this.sizeY; j++){
                if (this.world[i][j]){
                    population++;
                }
            }
        }

        return population;

    }

    public boolean[][] getWorld(){
        return this.world;
    }

}
