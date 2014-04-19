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
			SimpleQuestion testQuestion = (SimpleQuestion) questionList.get(1);
			//set is as session attribute
			session.setAttribute("testQuestion", testQuestion);
			session.setAttribute("testCategory", testCategory);
			//pass is on to question.jsp
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/question.jsp");
			dispatcher.forward(request, response);
		}
	}
	
}
