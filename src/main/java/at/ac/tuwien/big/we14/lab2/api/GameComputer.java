package at.ac.tuwien.big.we14.lab2.api;

public interface GameComputer {

	/**
	 * @return the score of the computer for a question
	 */
	public boolean getComputerScore();
	
	/**
	 * 
	 * @return the time used by the computer to generate a question answer
	 */
	public int getComputerTime();
	
}
