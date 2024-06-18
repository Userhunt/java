package net.w3e.base.noise;

import net.w3e.base.random.IRandom;

public class LayeredNoise {

	private final PerlinNoiseSampler[] layers;

	public LayeredNoise(IRandom random, int layers, int size) {
		this.layers = new PerlinNoiseSampler[layers];
		for (int i = 0; i < layers; i++) {
			this.layers[i] = new PerlinNoiseSampler(random);
		}
	}

}
