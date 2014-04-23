package at.ac.tuwien.big.we14.lab2.api.impl;

import at.ac.tuwien.big.we14.lab2.api.Player;

public class SimplePlayer implements Player {

	private String name;
	
	public SimplePlayer(){
		this.name = "Spieler 1";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
