package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

public class Game extends Controller{
	
	
	@play.db.jpa.Transactional
	@Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render());
    }
}
