package controllers;

import javax.persistence.EntityManager;

import models.Player;
import play.*;
import play.data.Form;
import play.db.jpa.JPA;
import play.mvc.*;
import views.html.index;
import views.html.authentication;
import views.html.registration;
import controllers.Game;

public class Application extends Controller {
	
	
	@play.db.jpa.Transactional
    public static Result index() {
        return redirect(routes.Application.authentication());
    }

	@play.db.jpa.Transactional
    public static Result authentication() {
        return ok(authentication.render(Form.form(Player.class)));
    }
	
	@play.db.jpa.Transactional
    public static Result registration() {
        return ok(registration.render(Form.form(Player.class)));
    }
	
	@play.db.jpa.Transactional
    public static Result createPlayer() {
		Form<Player> form = Form.form(Player.class).bindFromRequest(); 
		if (form.hasErrors()) { 
			return badRequest(registration.render(form)); 
		} else { 
			Player player = form.get(); 
			JPA.em().persist(player);
			return redirect(routes.Application.authentication());
		}
    }
	
	
	@play.db.jpa.Transactional
    public static Result validatePlayer() {
		Form<Player> form = Form.form(Player.class).bindFromRequest(); 
		String benutzername = form.data().get("benutzername");
		String passwort = form.data().get("passwort");
		Player player = getPlayerByUsername(benutzername);
		if(player == null){
			// TODO Show Invalid Username
			return badRequest(authentication.render(form));
		}else{
			if(player.getPasswort().equals(passwort)){
				session().clear();
		        session("user", form.data().get("benutzername"));
				return redirect(routes.Game.index());
			}else{
				// TODO Show Invalid Password
				return badRequest(authentication.render(form));
			}
		}
    }
	
	@play.db.jpa.Transactional
	private static Player getPlayerByUsername(String benutzername) {
		EntityManager em =JPA.em();
		return em.find(Player.class, benutzername);
	}
	
	@play.db.jpa.Transactional
	public static Result logout() {
	    session().clear();
	    flash("success", "Sie haben sich abgemeldet.");
	    return redirect(routes.Application.authentication());
	}
}
