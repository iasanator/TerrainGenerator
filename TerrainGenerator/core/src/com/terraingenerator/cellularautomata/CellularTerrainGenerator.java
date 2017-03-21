package com.terraingenerator.cellularautomata;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by iassona on 3/11/2017.
 *
 * This will provide an interface for a user to create random terrain
 *
 */
public class CellularTerrainGenerator {

    private CellularWorld cellularWorld;
    private IterationRules iterationRules;

    /**
     * Creates a new terrain generator object
     */
    public CellularTerrainGenerator(){

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

        boolean[][] wallmap = this.generate(sizeX, sizeY);
        wallmap = filterCave(wallmap);

        return wallmap;

    }

    /**
     * This generates a pseudo-maze
     * @param sizeX
     * @param sizeY
     * @return
     */
    public boolean[][] generateMaze(int sizeX, int sizeY){

        this.iterationRules.setRules(new int[]{3}, new int[]{0,1,2,3,4});

        boolean[][] wallmap = this.generate(sizeX, sizeY);

        return wallmap;
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
     * This will filter out all small caves and leave just the largest, connect cave.
     *
     * @param wallmap
     * @return
     */
    private boolean[][] filterCave(boolean[][] wallmap){

        int sizeX = wallmap.length;
        int sizeY = wallmap[0].length;

        int[][] filtered = new int[sizeX][sizeY];

        int caveIndex = 1;

        for (int i = 0; i < sizeX; i++){
            for (int j = 0; j < sizeY; j++){
                if (wallmap[i][j] == true){
                    filtered[i][j] = -1;
                } else {
                    filtered[i][j] = 0;
                }
            }
        }

        for (int i = 0; i < sizeX; i++){
            for (int j = 0; j < sizeY; j++){

                if (filtered[i][j] == -1){
                    continue;
                }

                if (filtered[i][j] == 0){
                    filtered[i][j] = caveIndex;

                    LinkedList<Point2D> inCave = new LinkedList<Point2D>();
                    inCave.add(new Point2D.Double(i, j));


                    while(inCave.size() > 0){

                        Point2D currentPoint = inCave.getFirst();
                        inCave.removeFirst();

                        for (int xVal : new int[]{-1, 0, 1}){
                            for (int yVal : new int[]{-1, 0, 1}){

                                if (xVal == yVal || xVal == -yVal) {continue;}

                                int xPos = ((int)currentPoint.getX() + xVal) % sizeX;
                                if (xPos < 0){xPos += sizeX;}

                                int yPos = ((int)currentPoint.getY() + yVal) % sizeY;
                                if (yPos < 0){yPos += sizeY;}

                                if (filtered[xPos][yPos] == 0){
                                    filtered[xPos][yPos] = caveIndex;
                                    inCave.add(new Point2D.Double(xPos, yPos));
                                }
                            }
                        }
                    }

                    caveIndex++;

                }
            }
        }

        HashMap<Integer, Integer> caveCount = new HashMap<Integer, Integer>();

        for (int i = 0; i < sizeX; i++){
            for (int j = 0; j < sizeY; j++){

                int cave = filtered[i][j];

                if (caveCount.containsKey(cave)){
                    int count = caveCount.get(cave);
                    caveCount.put(cave, count + 1);
                } else {
                    caveCount.put(cave, 1);
                }
            }
        }

        int mostCave = 0;
        int mostCaveCount = 0;
        for (int key : caveCount.keySet()){
            if (caveCount.get(key) > mostCaveCount && key != -1){
                mostCave = key;
                mostCaveCount = caveCount.get(key);
            }
        }

        boolean[][] output = new boolean[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++){
            for (int j = 0; j < sizeY; j++){

                if (filtered[i][j] == mostCave){
                    output[i][j] = false;
                } else {
                    output[i][j] = true;
                }
            }
        }

        return output;

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

    /**
     * This saves a PNG of a given heightmap in the root directory of this project
     *
     * @param heightmap
     * @param filename
     */
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
