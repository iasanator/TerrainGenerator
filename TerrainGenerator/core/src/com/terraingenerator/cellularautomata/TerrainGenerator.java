package com.terraingenerator.cellularautomata;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

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

        return this.generate(sizeX, sizeY);

    }

    /**
     * Generates a boolean array of blobs where 'true' is in a blob and 'false' is not
     * @param sizeX
     * @param sizeY
     * @return
     */
    public boolean[][] generateHills(int sizeX, int sizeY){

        this.iterationRules.setRules(new int[]{0,1,2,3,4}, new int[]{0,1,2,3,4});

        return this.generate(sizeX, sizeY);

    }

    /**
     * Generates a world based on the conditions set
     *
     * @param sizeX
     * @param sizeY
     * @return
     */
    private boolean[][] generate(int sizeX, int sizeY){

        this.cellularWorld.generateRandomWorld(sizeX, sizeY, 0.5f);
        cellularWorld.iterateUntilStable();

        if (cellularWorld.getPopulation() > ((sizeX * sizeY) / 2)){
            cellularWorld.iterate();
        }

        return cellularWorld.getWorld();
    }

    /**
     * Generates a heightmap with smooth hills
     *
     * @param sizeX
     * @param sizeY
     * @return
     */
    public int[][] generateHillsHeightmap(int sizeX, int sizeY){

        boolean[][] hillMap = this.generateHills(sizeX, sizeY);
        int[][] heightmap = this.convertToHeightmap(hillMap);

        for (int i = 0; i < 50; i++){
            heightmap = this.blur(heightmap);
        }

        return heightmap;

    }


    /**
     * Performs one iteration of blurring on the heightmap
     *
     * @param heightmap
     * @return
     */
    private int[][] blur(int[][] heightmap){

        int sizeX = heightmap.length;
        int sizeY = heightmap[0].length;

        int[][] output = new int[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++){
            for (int j = 0; j < sizeY; j++){

                int sum = 0;

                for (int xVal : new int[]{-1, 0, 1}){
                    for (int yVal : new int[]{-1, 0, 1}){

                        int xPos = (i + xVal) % sizeX;
                        if (xPos < 0){xPos += sizeX;}

                        int yPos = (j + yVal) % sizeY;
                        if (yPos < 0){yPos += sizeY;}

                        sum += heightmap[xPos][yPos];

                    }
                }

                output[i][j] = sum / 9;
            }
        }

        return output;

    }

    /**
     * This converts a boolean array to a heightmap with values 0 or 255
     *
     * @param world
     * @return
     */
    public int[][] convertToHeightmap(boolean[][] world){

        int sizeX = world.length;
        int sizeY = world[0].length;

        int[][] output = new int[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if (world[i][j]){
                    output[i][j] = 255;
                } else {
                    output[i][j] = 0;
                }
            }
        }

        return output;

    }

    /**
     * This prints the world to the console
     */
    public void printWorld(){
        this.cellularWorld.printWorld();
    }

    /**
     * This will output a PNG file that represents the heightmap given
     *
     * @param heightmap
     * @param filename
     */
    public void outputPNG(int[][] heightmap, String filename){

        writeToPNG(heightmap, filename);

    }

    /**
     * This will output a PNG based on the wallmap given
     *
     * @param wallmap
     * @param filename
     */
    public void outputPNG(boolean[][] wallmap, String filename){

        writeToPNG(this.convertToHeightmap(wallmap), filename);

    }

    private void writeToPNG(int[][] heightmap, String filename){

        int sizeX = heightmap.length;
        int sizeY = heightmap[0].length;

        try {
            BufferedImage img = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    int height = heightmap[i][j];
                    img.setRGB(i, j, new Color(height, height, height).getRGB());
                }
            }

            File outputFile = new File(filename + ".png");
            ImageIO.write(img, "png", outputFile);


        } catch (java.io.IOException e) {}
    }

}
