package at.ac.tuwien.big.we14.lab2.api;


/**
 * stores the score of a game session
 *
 */
public interface Game{

	/**
	 * @return the score of Player1
	 */
	public int getPlayer1Score(int questionNr);
	
	/**
	 * @return the score of Player2
	 */
	public int getPlayer2Score(int questionNr);
	
	/**
	 * @return the number of the actual round
	 */
	public int getRound();
	
	/**
	 * @return number of the actual question
	 */
	public int getQuestionNr();
	
	/**
	 * 
	 * @param questionNr increments the question number
	 */
	public void nextQuestion();

	/**
	 * @param round increments the round Nr and sets the questionNr to 0
	 */
	public void nextRound();

	/**
	 * @param player1Score the player1Score to set
	 */
	public void setPlayer1Win(int questionNumber);

	/**
	 * @param player2Score the player2Score to set
	 */
	public void setPlayer2Win(int questionNumber);
}
