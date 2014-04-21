package at.ac.tuwien.big.we14.lab2.api.impl;

import at.ac.tuwien.big.we14.lab2.api.*;

public class SimpleGameComputer implements GameComputer{

	private final String name = "QuizMaster";
	/**
	 * @return the score of the computer for a question
	 * -> 50:50 chance of answering the question correctly
	 */
	public boolean getComputerScore(){
		int randNumber = (int) (Math.random()*100);
		if(randNumber <50){
			return true;
		}
		return false;
	}
	
	/**
	 * @return the time used by the computer to generate a question answer
	 * -> random number between 1 and 30
	 */
	public int getComputerTime(){
		int randTime = (int) ((Math.random()*30)+1);
		return randTime;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	
}
