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

public class BigQuizServlet extends HttpServlet {

	// constants
	private static final long serialVersionUID = 1L;
	private static final int MAXROUNDS = 5;

	// global variables
	private QuizFactory factory;
	private QuestionDataProvider provider;
	private List<Category> categories;

	@Override
	public void init() throws ServletException {
		super.init();
		ServletContext servletContext = getServletContext();
		factory = ServletQuizFactory.init(servletContext);
		provider = factory.createQuestionDataProvider();
		categories = provider.loadCategoryData();
	}

	/**
	 * Responsible for starting the game and providing Questions
	 * 
	 * @param request
	 * @param response
	 * @param ServletExeption
	 * @param IOException
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getParameter("action") == null) {
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/start.jsp");
			dispatcher.forward(request, response);
		}
		
		String action = request.getParameter("action");
		
		if(action == null){
			return ;
		}
		
		// get session
		HttpSession session = request.getSession(true);
		
		if (action.equals("start")) {
			// create Player
			SimplePlayer player = new SimplePlayer();
			// create AI Player
			SimpleGameComputer ai = new SimpleGameComputer();
			// save list of not yet played categories for the session
			ArrayList<Category> freeCategories = new ArrayList<Category>(categories);
			session.setAttribute("freeCategories", freeCategories);
			// save list of not yet played questions of the category for the session
			genereateCategory(session);
			// get random Question from the list
			generateQuestion(session);
			// create new game
			SimpleGame newGame = new SimpleGame();
			// set session attributes
			session.setAttribute("game", newGame);
			session.setAttribute("player1", player);
			session.setAttribute("player2", ai);
		} else if (action.equals("roundcomplete")) {
			// get current Game
			SimpleGame currentGame = (SimpleGame) session.getAttribute("game");
			// get list of free categories
			genereateCategory(session);
			// get random Question from the list
			generateQuestion(session);
			// prepare values for new round
			currentGame.nextRound();
			// set changed session attributes
			session.setAttribute("game", currentGame);
		}
		
		// pass is on to question.jsp
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/question.jsp");
		dispatcher.forward(request, response);
	}

	@SuppressWarnings("static-access")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// retrieve current session
		HttpSession session = request.getSession(true);
		// retrieve Game Object from current session
		SimpleGame currentGame = (SimpleGame) session.getAttribute("game");
		// retrieve Human player from current session
		SimplePlayer currentPlayer = (SimplePlayer) session.getAttribute("player1");
		// retrieve AI player from current session
		SimpleGameComputer currentAi = (SimpleGameComputer) session.getAttribute("player2");
		// get question Nr
		int questionNr = currentGame.getQuestionNr();
		/**
		 * Compute Results of the answered Question
		 */
		// retrieve attributes from request
		SimpleQuestion answeredQuestion = (SimpleQuestion) session.getAttribute("question");
		// retrieve selected answers by user
		if (request.getParameterValues("option") != null) {
			/**
			 * player marked answers
			 */
			List<String> playerAnswers = Arrays.asList(request.getParameterValues("option"));
			
			System.out.println("player answer: ");
			for(String s : playerAnswers)
				System.out.print(s + " ");
			System.out.println();
			
			// retrieve time used by player to answer the question
			int playerAnswerTime = Integer.parseInt((request.getParameter("timeleftvalue")));
			// check if ALL answers are correct (if not => question not
			// answered)
			List<Choice> correctAnswers = answeredQuestion.getCorrectChoices();
			
			System.out.println("correct answer: ");
			for(Choice c : correctAnswers)
				System.out.print(c.getId() + " ");
			System.out.println();
			
			boolean correct = true;
			if (correctAnswers.size() == playerAnswers.size()) 
			{
				for (Choice c : correctAnswers) 
				{
					System.out.println(playerAnswers.contains(String.valueOf(c.getId())) + ": player anwers do not contain the correct answer");
					if (!playerAnswers.contains(String.valueOf(c.getId()))) 
					{
						currentGame.setPlayer1Score(questionNr, false);
						currentGame.addRoundTimePlayer1(0);
						correct = false;
						break;
					}
				}
				
				if(correct == true)
				{
					currentGame.setPlayer1Score(questionNr, true);
					currentGame.addRoundTimePlayer1(playerAnswerTime);
				}
			} 
			else 
			{
				currentGame.setPlayer1Score(questionNr, false);
				currentGame.addRoundTimePlayer1(0);
			}
		} else {
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
		if (computerAnswer == true) {
			currentGame.setPlayer2Score(questionNr, computerAnswer);
			currentGame.addRoundTimePlayer2(currentAi.getComputerTime());
		} else {
			currentGame.setPlayer2Score(questionNr, computerAnswer);
			currentGame.addRoundTimePlayer2(0);
		}

		// increment question number
		currentGame.nextQuestion();

		if (questionNr < 2) {
			/**
			 * round not over
			 */
			// generate new question from same category
			generateQuestion(session);
			// set changed game session attribute
			session.setAttribute("game", currentGame);
			// forward response
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/question.jsp");
			dispatcher.forward(request, response);
		} else {
			/**
			 * round over -> Compute round winner or tie
			 */
			String winnerMessage = ""; // needed only for roundcomplete.jsp
			RoundScore roundResult = computeRoundWinner(currentGame);
			if (roundResult.equals(RoundScore.PLAYER1)) {
				currentGame.setRoundScore(RoundScore.PLAYER1);
				winnerMessage = currentPlayer.getName() + " gewinnt Runde "
						+ (currentGame.getRound() + 1) + "!";
			} else if (roundResult.equals(RoundScore.PLAYER2)) {
				currentGame.setRoundScore(RoundScore.PLAYER2);
				winnerMessage = currentAi.getName() + " gewinnt Runde "
						+ (currentGame.getRound() + 1) + "!";
			} else if (roundResult.equals(RoundScore.TIE)) {
				currentGame.setRoundScore(RoundScore.TIE);
				winnerMessage = "Runde " + (currentGame.getRound() + 1)
						+ " geht UNETSCHIEDEN aus!";
			}

			if (currentGame.getRound() < (this.MAXROUNDS - 1)) {
				/**
				 * game not over yet
				 */
				// set changed session attributes
				session.setAttribute("game", currentGame);
				request.setAttribute("winnerMessage", winnerMessage);
				// increment round Nr and prepare new Round
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/roundcomplete.jsp");
				dispatcher.forward(request, response);
			} else {
				/**
				 * Game over
				 *
				// compute round-wins for both players
				int player1Rounds = 0;
				int player2Rounds = 0;
				for (int i = 0; i < MAXROUNDS; i++) {
					if (currentGame.getRoundScore(i) == RoundScore.PLAYER1) {
						player1Rounds++;
					} else if (currentGame.getRoundScore(i) == RoundScore.PLAYER2) {
						player2Rounds++;
					}
				}
				*/
				
				// computer a winner or a TIE
				String gameOverMessage = "";
				if (currentGame.getPlayer1Rounds() > currentGame.getPlayer2Rounds()) {
					gameOverMessage = currentPlayer.getName() + " gewinnt!";
				} else if (currentGame.getPlayer2Rounds() > currentGame.getPlayer1Rounds()) {
					gameOverMessage = currentAi.getName() + " gewinnt";
				} else {
					gameOverMessage = "Das Spiel endet Unetschieden!";
				}
				request.setAttribute("gameOverMessage", gameOverMessage);
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/finish.jsp");
				dispatcher.forward(request, response);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void genereateCategory(HttpSession session){
		// Get list of not yet played categories
		ArrayList<Category> freeCategories = (ArrayList<Category>) session.getAttribute("freeCategories");
		// take random Category from the list
		Category newCategory = freeCategories.get((int) (Math.random() * freeCategories.size()));
		// remove chosen category from free categories list
		freeCategories.remove(newCategory);
		// Set the freeCatgeories attribute in the session
		session.setAttribute("freeCategories", freeCategories);
		session.setAttribute("category", newCategory);
		session.setAttribute("freeQuestions", newCategory.getQuestions());
	}
	
	/**
	 * 
	 * @param session : current Session
	 * @return a random question that wasnt displayed yet
	 */
	public void generateQuestion(HttpSession session) {
		@SuppressWarnings("unchecked")
		ArrayList<Question> freeQuestions = new ArrayList<Question>((List<Question>) session.getAttribute("freeQuestions"));
		//List<Question> freeQuestions = (List<Question>) session.getAttribute("freeQuestions");
		// get random Question from the list
		SimpleQuestion newQuestion = (SimpleQuestion) freeQuestions.get((int) (Math.random() * freeQuestions.size()));
		// remove chosen question from free question list
		freeQuestions.remove(newQuestion);
		// Set the freeQuestions attribute in the session
		session.setAttribute("freeQuestions", freeQuestions);
		session.setAttribute("question", newQuestion);
	}

	/**
	 * @return winner of the current round of the parsed game
	 */
	public RoundScore computeRoundWinner(SimpleGame currentGame) {
		int player1Wins = 0;
		int player2Wins = 0;
		for (int i = 0; i < 3; i++) {
			if (currentGame.isQuestionCorrectPlayer1(i)) {
				player1Wins++;
			}
			if (currentGame.isQuestionCorrectPlayer2(i)) {
				player2Wins++;
			}
		}
		if (player1Wins > player2Wins) {// Player 1 wins the round (human)
			return RoundScore.PLAYER1;
		} else if (player2Wins > player1Wins) {// Player 2 Wins the round (AI)
			return RoundScore.PLAYER2;
		} else { // same amount of correct questions => compare time
			if (currentGame.getRoundTimePlayer1() > currentGame
					.getRoundTimePlayer2()) {
				// player 1 wins
				return RoundScore.PLAYER1;
			} else if (currentGame.getRoundTimePlayer2() > currentGame
					.getRoundTimePlayer1()) {
				// player 2 wins
				return RoundScore.PLAYER1;
			} else {
				// everything in equal => its a TIE!
				return RoundScore.TIE;
			}
		}
	}
}