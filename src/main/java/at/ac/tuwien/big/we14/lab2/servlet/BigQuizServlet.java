package at.ac.tuwien.big.we14.lab2.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.*;
import at.ac.tuwien.big.we14.lab2.api.impl.SimpleGame.RoundScore;



public class BigQuizServlet extends HttpServlet{
	
	//constants
	private static final long serialVersionUID = 1L;
	private static final int MAXROUNDS = 5;
	
	//global variables
	private QuizFactory factory;
	private QuestionDataProvider provider;
	private List<Category> categories; 
	
	@Override
	public void init() throws ServletException{
		super.init();
		ServletContext servletContext = getServletContext();
		factory = ServletQuizFactory.init(servletContext);
		provider = factory.createQuestionDataProvider();
		categories = provider.loadCategoryData();
	}
	
	
	/**
	 * Responsible for starting the game and providing Questions
	 * @param request
	 * @param response
	 * @param ServletExeption
	 * @param IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		String action = request.getParameter("action");
		
		if(action == null){
			return;
		}
		if(action.equals("start")){
			//get session
			HttpSession session = request.getSession(true);
			//create Player
			SimplePlayer player = new SimplePlayer();
			//create AI Player
			SimpleGameComputer ai = new SimpleGameComputer();
			//save list of not yet played categories for the session
			ArrayList<Category> freeCategories = new ArrayList<Category>(categories);
			//take random Category from the list
			Category newCategory = freeCategories.get((int) (Math.random()*freeCategories.size()));
			//remove chosen category from free categories list
			freeCategories.remove(newCategory);
			//get random Question from the list
			SimpleQuestion newQuestion = (SimpleQuestion) generateQuestion(newCategory);
			//create new game
			SimpleGame newGame = new SimpleGame();
			//set session attributes
			session.setAttribute("game", newGame);
			session.setAttribute("question", newQuestion);
			session.setAttribute("category", newCategory);
			session.setAttribute("player1", player);
			session.setAttribute("player2", ai);
			session.setAttribute("freeCategories", freeCategories);
			//pass is on to question.jsp
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/question.jsp");
			dispatcher.forward(request, response);
			
		}else if(action.equals("roundcomplete")){
			//get session
			HttpSession session = request.getSession(true);
			//get current Game
			SimpleGame currentGame = (SimpleGame) session.getAttribute("game");
			//get list of free categories
			@SuppressWarnings("unchecked")
			ArrayList<Category> freeCategories = (ArrayList<Category>) session.getAttribute("freeCategories");
			//get random category from the list
			Category newCategory = freeCategories.get((int) (Math.random()*freeCategories.size()));
			//remove chosen category from free categories list
			freeCategories.remove(newCategory);
			//get random Question from the list
			SimpleQuestion newQuestion = (SimpleQuestion) generateQuestion(newCategory);
			//prepare values for new round
			currentGame.nextRound();
			//set changed session attributes
			session.setAttribute("game", currentGame);
			session.setAttribute("question", newQuestion);
			session.setAttribute("category", newCategory);
			session.setAttribute("freeCategories", freeCategories);
			//pass is on to question.jsp
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/question.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	@SuppressWarnings("static-access")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		//retrieve current session
		HttpSession session = request.getSession(true);
		//retrieve Game Object from current session
		SimpleGame currentGame = (SimpleGame) session.getAttribute("game");
		//retrieve Human player from current session
		SimplePlayer currentPlayer = (SimplePlayer) session.getAttribute("player1");
		//retrieve AI player from current session
		SimpleGameComputer currentAi = (SimpleGameComputer) session.getAttribute("player2");
		//get question Nr
		int questionNr = currentGame.getQuestionNr();
			/**
			 * Compute Results of the answered Question
			 */
			//retrieve attributes from request
			Category currentCat = (SimpleCategory) session.getAttribute("category");
			SimpleQuestion answeredQuestion = (SimpleQuestion) session.getAttribute("question");
			//retrieve selected answers by user
			if(request.getAttribute("option")!= null){
				/**
				 * player marked answers
				 */
				List<String> playerAnswers = Arrays.asList(request.getParameterValues("option"));
				//retrieve time used by player to answer the question
				int playerAnswerTime = Integer.parseInt((request.getParameter("timeleftvalue")));
				//check if ALL answers are correct (if not => question not answered)
				List<Choice> correctAnswers = answeredQuestion.getCorrectChoices();
				if(correctAnswers.size() == playerAnswers.size() ){
					for(Choice c : correctAnswers){
						if(!playerAnswers.contains(c.getId())){
							currentGame.setPlayer1Score(questionNr, false);
							currentGame.addRoundTimePlayer1(0);
							break;
						}
					}
					currentGame.setPlayer1Score(questionNr, true);
					currentGame.addRoundTimePlayer1(playerAnswerTime);
				}else{
					currentGame.setPlayer1Score(questionNr, false);
					currentGame.addRoundTimePlayer1(0);
				}
			}else{
				/**
				 * player did not mark anything
				 */
				currentGame.setPlayer1Score(questionNr, false);
				currentGame.addRoundTimePlayer1(0);
			}
			/**
			 * Compute AI results to the current Question
			 */
			boolean computerAnswer = currentAi.getComputerScore();
			if(computerAnswer==true){
				currentGame.setPlayer2Score(questionNr, computerAnswer);
				currentGame.addRoundTimePlayer2(currentAi.getComputerTime());
			}else{
				currentGame.setPlayer2Score(questionNr, computerAnswer);
				currentGame.addRoundTimePlayer2(0);
			}

				//increment question number
				currentGame.nextQuestion();
				
				if(questionNr <2){
					/**
					 * round not over 
					 */
					//generate new question from same category
					SimpleQuestion newQuestion = (SimpleQuestion) generateQuestion(currentCat);
					//set changed game session attribute
					session.setAttribute("question", newQuestion);
					session.setAttribute("game", currentGame);
					//forward response
					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/question.jsp");
					dispatcher.forward(request, response);
				}else{
					/**
					 * round over -> Compute round winner or tie
					 */
					String winnerMessage = ""; //needed only for roundcomplete.jsp
					RoundScore roundResult = computeRoundWinner(currentGame);
					if(roundResult.equals(RoundScore.PLAYER1)){
						currentGame.setRoundScore(RoundScore.PLAYER1);
						winnerMessage = currentPlayer.getName()+" gewinnt Runde "+(currentGame.getRound()+1)+"!";
					}else if(roundResult.equals(RoundScore.PLAYER2)){
						currentGame.setRoundScore(RoundScore.PLAYER2);
						winnerMessage = currentAi.getName()+" gewinnt Runde "+(currentGame.getRound()+1)+"!";
					}else if(roundResult.equals(RoundScore.TIE)){
						currentGame.setRoundScore(RoundScore.TIE);
						winnerMessage = "Runde "+(currentGame.getRound()+1)+" geht UNETSCHIEDEN aus!";
					}
					
					if(currentGame.getRound()<(this.MAXROUNDS-1)){
						/**
						 * game not over yet
						 */
						//set changed session attributes
						session.setAttribute("game", currentGame);
						request.setAttribute("winnerMessage", winnerMessage);
						//increment round Nr and prepare new Round
						RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/roundcomplete.jsp");
						dispatcher.forward(request, response);
					}else{
						/**
						 * Game over
						 */
						//compute round-wins for both players
						int player1Rounds = 0;
						int player2Rounds = 0;
						for(int i = 0; i<MAXROUNDS;i++){
							if(currentGame.getRoundScore(i)==RoundScore.PLAYER1){
								player1Rounds++;
							}else if(currentGame.getRoundScore(i)==RoundScore.PLAYER2){
								player2Rounds++;
							}
						}
						//computer a winner or a TIE
						String gameOverMessage = "";
						if(player1Rounds>player2Rounds){
							gameOverMessage = currentPlayer.getName()+" gewinnt!";
						}else if(player2Rounds> player1Rounds){
							gameOverMessage = currentAi.getName()+" gewinnt";
						}else{
							gameOverMessage = "Das Spiel endet Unetschieden!";
						}
						request.setAttribute("gameOverMessage", gameOverMessage);
						request.setAttribute("player1Rounds", player1Rounds);
						request.setAttribute("player2Rounds", player2Rounds);
						RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/finish.jsp");
						dispatcher.forward(request, response);
					}
				}
	}
	
	/**
	 * @param category Category of the question to be returned
	 * @return a random question from the parsed category
	 */
	public Question generateQuestion(Category category){
		List<Question> questionList = category.getQuestions();
		//get random Question from the list
		SimpleQuestion question = (SimpleQuestion) questionList.get((int) (Math.random()*questionList.size()));
		return question;
	}
	
	/**
	 * @return winner of the current round of the parsed game
	 */
	public RoundScore computeRoundWinner(SimpleGame currentGame){
		int player1Wins = 0;
		int player2Wins = 0;
		for(int i=0; i<3;i++){
			if(currentGame.isQuestionCorrectPlayer1(i)){
				player1Wins++;
			}
			if(currentGame.isQuestionCorrectPlayer2(i)){
				player2Wins++;
			}
		}
		if(player1Wins > player2Wins){//Player 1 wins the round (human)
			return RoundScore.PLAYER1;
		}else if(player2Wins > player1Wins){//Player 2 Wins the round (AI)
			return RoundScore.PLAYER2;
		}else{ //same amount of correct questions => compare time
			if(currentGame.getRoundTimePlayer1() > currentGame.getRoundTimePlayer2()){
				//player 1 wins
				return RoundScore.PLAYER1;
			}else if(currentGame.getRoundTimePlayer2() > currentGame.getRoundTimePlayer1()){
				//player 2 wins
				return RoundScore.PLAYER1;
			}else{
				//everything in equal => its a TIE!
				return RoundScore.TIE;
			}
		}
	}
	
}
