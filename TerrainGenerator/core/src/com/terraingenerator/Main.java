package com.terraingenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.terraingenerator.cellularautomata.*;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		TerrainGenerator terraGen = new TerrainGenerator();

		terraGen.generateCave(128, 128);
		terraGen.printWorld();

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();



		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
