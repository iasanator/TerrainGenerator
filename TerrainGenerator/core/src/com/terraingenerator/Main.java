package com.terraingenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.terraingenerator.cellularautomata.CellularTerrainGenerator;


public class Main extends ApplicationAdapter {

	public static final int SIZEX = 4000;
	public static final int SIZEY = 4000;

	SpriteBatch batch;

	CellularTerrainGenerator terraGen;
	int[][] heightmap;


	Pixmap pixmap;
	Texture img;
	
	@Override
	public void create () {
		Gdx.graphics.setWindowedMode(SIZEX, SIZEY);
		batch = new SpriteBatch();

		this.terraGen = new CellularTerrainGenerator();

		int generateID = 1;

		if (generateID == 0){
			this.heightmap = terraGen.generateHillsHeightmap(SIZEX, SIZEY);
			terraGen.outputPNG(this.heightmap, "output");
		} else if (generateID == 1) {
			boolean[][] wallmap = terraGen.generateCave(SIZEX, SIZEY);
			terraGen.outputPNG(wallmap, "output");
			this.heightmap = terraGen.convertToHeightmap(wallmap);
		} else {
			boolean[][] wallmap = terraGen.generateMaze(SIZEX, SIZEY);
			terraGen.outputPNG(wallmap, "output");
			this.heightmap = terraGen.convertToHeightmap(wallmap);
		}

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		this.renderPixels(this.batch, this.heightmap);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	private void renderPixels(Batch batch, int[][] heightmap){

		this.pixmap = new Pixmap(SIZEX, SIZEY, Pixmap.Format.RGB888);

		for (int i = 0; i < SIZEY; i++){
			for (int j = 0; j < SIZEY; j++){
				float shade = (float)((double)heightmap[i][j] / 255.0);
				pixmap.drawPixel(i, j, Color.rgba8888(shade, shade, shade, 0.99f));
			}
		}

		this.img = new Texture(pixmap);

		batch.draw(this.img, 0, 0);

		this.pixmap.dispose();
	}

}
