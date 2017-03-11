package com.terraingenerator.cellularautomata;

/**
 * Created by iassona on 3/11/2017.
 *
 * This will provide an interface for a user to create random terrain
 *
 */
public class TerrainGenerator {

    private CellularWorld cellularWorld;
    private IterationRules iterationRules;

    /**
     * Creates a new terrain generator object
     */
    public TerrainGenerator(){

        this.iterationRules = new IterationRules(new int[]{}, new int[]{});
        this.cellularWorld = new CellularWorld(8,8,0.5f, this.iterationRules);
    }

    /**
     * Generates a boolean array of the given size where 'true' is a wall and 'false' is a floor
     *
     * @param sizeX
     * @param sizeY
     * @return
     */
    public boolean[][] generateCave(int sizeX, int sizeY){

        this.iterationRules.setRules(new int[]{0,1,2,3,4}, new int[]{0,1,2,3});
        this.cellularWorld.generateRandomWorld(sizeX, sizeY, 0.5f);
        cellularWorld.iterateUntilStable();

        if (cellularWorld.getPopulation() > ((sizeX * sizeY) / 2)){
            cellularWorld.iterate();
        }

        return cellularWorld.getWorld();

    }

    public void printWorld(){
        this.cellularWorld.printWorld();
    }

}
