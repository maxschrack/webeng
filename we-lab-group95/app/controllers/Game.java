package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import models.*;
import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.*;
import play.data.Form;
import play.db.jpa.JPA;
import play.mvc.*;
import views.html.*;
import play.cache.Cache;

public class Game extends Controller{
	
	public static String lang_data="conf/data.de.json";
	
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render());
    }
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
    public static Result startNewGame() {
		EntityManager em =JPA.em();
		QuizFactory factory = new PlayQuizFactory(lang_data, em.find(Player.class, session().get("user")));
		QuizGame game = factory.createQuizGame();
		//store current game in cache
		Cache.set("game",game);
		return redirect(routes.Game.startNewRound());
	}
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
    public static Result startNewRound() {
		QuizGame game = (QuizGame)Cache.get("game");
		game.startNewRound();
		return redirect(routes.Game.showQuestion());
    }
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result showQuestion(){ 	
		QuizGame game = (QuizGame)Cache.get("game");
		if(game.isRoundOver()){
			return redirect(routes.Game.roundOver());
		}
		List<Choice> choices = (List<Choice>) game.getCurrentRound().getCurrentQuestion(game.getPlayers().get(0)).getAllChoices();
		return ok(quiz.render(game, choices));
	}
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result validateQuestion(){ 	
		QuizGame game = (QuizGame)Cache.get("game");
		// Form verarbeiten
		// Sieger ermitteln
		
		return redirect(routes.Game.showQuestion());
	}
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result roundOver(){
		QuizGame game = (QuizGame)Cache.get("game");
		if(game.isGameOver()){
			return redirect(routes.Game.gameOver());
		}
		return redirect(routes.Game.startNewRound());
	}
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
	public static Result gameOver(){
		return ok(quizover.render());
	}
}
