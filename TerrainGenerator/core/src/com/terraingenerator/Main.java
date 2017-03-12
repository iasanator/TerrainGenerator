package com.terraingenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.terraingenerator.cellularautomata.TerrainGenerator;


public class Main extends ApplicationAdapter {

	public static final int SIZEX = 512;
	public static final int SIZEY = 512;

	SpriteBatch batch;

	TerrainGenerator terraGen;
	int[][] heightmap;


	Pixmap pixmap;
	Texture img;
	
	@Override
	public void create () {
		Gdx.graphics.setWindowedMode(SIZEX, SIZEY);
		batch = new SpriteBatch();

		this.terraGen = new TerrainGenerator();

		boolean generateHills = false;

		if (generateHills){
			this.heightmap = terraGen.generateHillsHeightmap(SIZEX, SIZEY);
			terraGen.outputPNG(this.heightmap, "output");
		} else {
			boolean[][] wallmap = terraGen.generateCave(SIZEX, SIZEY);
			terraGen.outputPNG(wallmap, "output");
			this.heightmap = terraGen.convertToHeightmap(wallmap);
		}
		terraGen.outputPNG(heightmap, "output");


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

		this.pixmap = new Pixmap(512, 512, Pixmap.Format.RGB888);

		for (int i = 0; i < 512; i++){
			for (int j = 0; j < 512; j++){
				float shade = (float)((double)heightmap[i][j] / 255.0);
				pixmap.drawPixel(i, j, Color.rgba8888(shade, shade, shade, 0.99f));
			}
		}

		this.img = new Texture(pixmap);

		batch.draw(this.img, 0, 0);
	}

}
