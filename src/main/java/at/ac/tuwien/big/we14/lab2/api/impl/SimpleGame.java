package at.ac.tuwien.big.we14.lab2.api.impl;

import at.ac.tuwien.big.we14.lab2.api.Game;

public class SimpleGame{

	private int questionNr; //current Question (between 0 and 2)
	private int round; //current Round (between 0 and 4)
	private boolean[] player1Score ={false,false,false};
	private boolean[] player2Score ={false,false,false};
	private RoundScore[] roundScores ={RoundScore.OFFEN,RoundScore.OFFEN,RoundScore.OFFEN,RoundScore.OFFEN,RoundScore.OFFEN};
	

	public enum RoundScore{
		SPIELER1,
		SPIELER2,
		UNENTSCHIEDEN,
		OFFEN;
	}
	
	public SimpleGame(){
		this.round = 0;
		this.questionNr = 0;
	}
	/**
	 * @param questionNr the questionNr to set
	 */
	public void nextQuestion() {
		this.questionNr++;
	}

	/**
	 * @param round the round to set
	 */
	public void nextRound() {
		this.round++;
		this.questionNr=0;
	}

	/**
	 * @param player1Score the player1Score to set
	 */
	public void setPlayer1Score(int questionNumber, boolean result) {
		player1Score[questionNumber] = result;
	}

	/**
	 * @param player2Score the player2Score to set
	 */
	public void setPlayer2Score(int questionNumber, boolean result) {
		player2Score[questionNumber] = result;
	}

	/**
	 * @return the questionNr
	 */
	public int getQuestionNr() {
		return questionNr;
	}

	/**
	 * @return the round
	 */
	public int getRound() {
		return round;
	}

	/**
	 * @return the player1Score for the parsed questionNr
	 */
	public boolean isQuestionCorrectPlayer1(int questionNumber) {
		return player1Score[questionNumber];
	}

	/**
	 * @return the player2Score for the parsed questionNr
	 */
	public boolean isQuestionCorrectPlayer2(int questionNumber) {
		return player2Score[questionNumber];
	}

}
