package controllers;

import javax.persistence.EntityManager;

import models.Player;
import at.ac.tuwien.big.we14.lab2.api.Question;
import at.ac.tuwien.big.we14.lab2.api.QuizFactory;
import at.ac.tuwien.big.we14.lab2.api.QuizGame;
import at.ac.tuwien.big.we14.lab2.api.Round;
import at.ac.tuwien.big.we14.lab2.api.impl.PlayQuizFactory;
import play.data.Form;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

public class Game extends Controller{
	
	//public static final EntityManager em =JPA.em();
	
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render());
    }
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
    public static Result startNewGame() {
		EntityManager em =JPA.em();
		QuizFactory factory = new PlayQuizFactory("conf/data.de.json", em.find(Player.class, session().get("user")));
		QuizGame game = factory.createQuizGame();
		return redirect(routes.Game.startNewRound());
    }
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
    public static Result startNewRound() {
		//game.startNewRound();
		return redirect(routes.Game.showQuestion());
    }
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result showQuestion(){
		/*
		Form<Game> form = Form.form(Game.class).bindFromRequest(); 
		if(game.isRoundOver()){
			return redirect(routes.Game.roundOver());
		}
		Question question = game.getCurrentRound().getCurrentQuestion(user);
		ArrayList<Choices> choices = question.getAllChoices();	
		*/	
		return ok(quiz.render(Form.form(Game.class)));
	}
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result roundOver(){
		/*
		if(game.isGameOver()){
			return redirect(routes.Game.gameOver());
		}
		*/
		return redirect(routes.Game.startNewRound());
	}
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result gameOver(){
		return ok(quizover.render());
	}
}
