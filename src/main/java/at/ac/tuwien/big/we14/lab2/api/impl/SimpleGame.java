package at.ac.tuwien.big.we14.lab2.api.impl;

public class SimpleGame{

	private int questionNr; //current Question (between 0 and 2)
	private int round; //current Round (between 0 and 4)
	private boolean[] player1Score ={false,false,false};
	private boolean[] player2Score ={false,false,false};
	private RoundScore[] roundScores ={RoundScore.OPEN,RoundScore.OPEN,RoundScore.OPEN,RoundScore.OPEN,RoundScore.OPEN};
	private int roundTimePlayer1;//time taken to answer all questions in a round
	private int roundTimePlayer2;
	private int player1Rounds;
	private int player2Rounds;

	

	public enum RoundScore{
		PLAYER1,
		PLAYER2,
		TIE,
		OPEN;
	}
	
	public SimpleGame(){
		this.round = 0;
		this.questionNr = 0;
		this.roundTimePlayer1 = 0;
		this.roundTimePlayer2 = 0;
		this.player1Rounds = 0;
		this.player2Rounds = 0;
	}
	/**
	 * @param questionNr the questionNr to set
	 */
	public void nextQuestion() {
		this.questionNr++;
	}

	/**
	 *  prepares values for the next round
	 */
	public void nextRound(){
		this.round++;
		this.questionNr = 0;
		this.roundTimePlayer1 = 0;
		this.roundTimePlayer2 = 0;
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
	/**
	 * @return the roundTimePlayer1
	 */
	public int getRoundTimePlayer1() {
		return roundTimePlayer1;
	}
	/**
	 * @param roundTimePlayer1 the roundTimePlayer1 to set
	 */
	public void addRoundTimePlayer1(int questionTime) {
		this.roundTimePlayer1 += questionTime;
	}
	/**
	 * @return the roundTimePlayer2
	 */
	public int getRoundTimePlayer2() {
		return roundTimePlayer2;
	}
	/**
	 * @param roundTimePlayer2 the roundTimePlayer2 to set
	 */
	public void addRoundTimePlayer2(int questionTime) {
		this.roundTimePlayer2 += questionTime;
	}
	/**
	 * @return the roundScores
	 */
	public RoundScore getRoundScore(int roundNr) {
		return roundScores[roundNr];
	}
	
	/**
	 * sets the Round score
	 */
	public void setRoundScore(RoundScore roundResult){
		if(roundScores.equals(RoundScore.PLAYER1)){
			player1Rounds++;
		}else if(roundScores.equals(RoundScore.PLAYER2)){
			player2Rounds++;
		}
		roundScores[round]=roundResult;
	}
	
	public int getPlayer1Rounds() {
		return player1Rounds;
	}
	public int getPlayer2Rounds() {
		return player2Rounds;
	}

}
