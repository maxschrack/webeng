package at.ac.tuwien.big.we14.lab2.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.*;



public class BigQuizServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
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
			//create session
			HttpSession session = request.getSession(true);
			//take first Category from the list
			Category testCategory = categories.get((int) (Math.random()*categories.size()));
			//get the List of Questions from that category
			List<Question> questionList = testCategory.getQuestions();
			//get the first Question from the list
			SimpleQuestion testQuestion = (SimpleQuestion) questionList.get((int) (Math.random()*questionList.size()));
			//create new game
			SimpleGame newGame = new SimpleGame();
			//set is as session attribute
			session.setAttribute("game", newGame);
			session.setAttribute("question", testQuestion);
			session.setAttribute("category", testCategory);
			//pass is on to question.jsp
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/question.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		//retrieve current session
		HttpSession session = request.getSession(true);
		//retrieve Game Object from current session
		SimpleGame currentGame = (SimpleGame) session.getAttribute("game");
		
		if(currentGame.getQuestionNr()<2){
			//retrieve attributes from request
			Category currentCat = (SimpleCategory) session.getAttribute("category");
			SimpleQuestion answeredQuestion = (SimpleQuestion) session.getAttribute("question");
			//retrieve selected answers by user
			String[] answers = request.getParameterValues("option");
			/** Round not finished
			 * TODO:
			 * 0) increment questionNr!!!
			 * 1) check if ALL answers are correct (if not => question not answered)
			 * 2) save player score in SimpleGame
			 * 3) CREATE COMPUTER class
			 * 4) generate computer answers
			 * 5) store computer score in SimpleGame
			 * 6) generate new question from same category and set new question attribute
			 * 7) forward request
			 */
			
			
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/question.jsp");
			dispatcher.forward(request, response);
		}else if(currentGame.getRound()<4){
			/** Round over
			 * TODO:
			 * 1) compute round winner
			 * 2) start new round (increment roundNr in "game" session attr.)
			 * 3) rest like in "doGet action=start" 
			 */
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/roundcomplete.html");
			dispatcher.forward(request, response);
		}else{
			/**
			 * Game over
			 * TODO:
			 * 1) computer winner or tie
			 */
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/finish.html");
			dispatcher.forward(request, response);
		}
	}
}
