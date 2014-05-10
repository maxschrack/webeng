package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

public class Secured extends Security.Authenticator {

    public String getUsername(Context ctx) {
        return ctx.session().get("user");
    }

    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.authentication());
    }
}