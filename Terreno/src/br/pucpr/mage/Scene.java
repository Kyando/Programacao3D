package br.pucpr.mage;

import java.io.IOException;

public interface Scene {
	void init() throws IOException;
	void update(float secs);
	void draw();
	void deinit();
}
