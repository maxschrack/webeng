package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import models.*;
import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.*;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.mvc.*;
import views.html.*;
import play.cache.Cache;

public class Game extends Controller {

	public static String lang_data = "conf/data.de.json";

	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result index() {
		return ok(index.render());
	}

	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result startNewGame() {
		EntityManager em = JPA.em();
		QuizFactory factory = new PlayQuizFactory(lang_data, em.find(
				Player.class, session().get("user")));
		QuizGame game = factory.createQuizGame();
		// store current game in cache
		Cache.set(session().get("user")+"game", game);
		return redirect(routes.Game.startNewRound());
	}

	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result startNewRound() {
		QuizGame game = (QuizGame) Cache.get(session().get("user")+"game");
		game.startNewRound();
		return redirect(routes.Game.showQuestion());
	}

	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result showQuestion() {
		QuizGame game = (QuizGame) Cache.get(session().get("user")+"game");
		if (game.isRoundOver()) {
			return redirect(routes.Game.roundOver());
		}
		return ok(quiz.render(game));
	}

	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result validateQuestion() {
		//get game from cache
		QuizGame game = (QuizGame) Cache.get(session().get("user")+"game");
		//bind dynamic form
		DynamicForm answerData = Form.form().bindFromRequest();
		//get user from session
		String benutzername = session("user");
		//get time from form
		long answerTime = Long.valueOf(answerData.get("timeleftvalue"));
		//get current user
		User currentPlayer = game.getPlayers().get(0);
		//get current question
		Question question = game.getCurrentRound().getCurrentQuestion(currentPlayer);
		//collect answers from form
		List<Choice> answers = new ArrayList<Choice>();
		for(Choice c: question.getAllChoices()){
			System.out.println("id"+c.getId());
			String chosen = answerData.get(String.valueOf(c.getId()));
			System.out.println("is null:"+chosen);
			if(chosen != null && chosen.equals("on")){
				answers.add(c);
			}
		}
		//submit answers
		game.answerCurrentQuestion(currentPlayer, answers, answerTime);
		return redirect(routes.Game.showQuestion());
	}

	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result roundOver() {
		QuizGame game = (QuizGame) Cache.get(session().get("user")+"game");
		if (game.isGameOver()) {
			return redirect(routes.Game.gameOver());
		}
		return ok(roundover.render(game));
	}

	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result gameOver() {
		QuizGame game = (QuizGame) Cache.get(session().get("user")+"game");
		return ok(quizover.render(game));
	}
}
