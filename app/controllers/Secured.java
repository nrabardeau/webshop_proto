package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Security class used to restrict access to the webshop to
 * logged on users
 *
 */
public class Secured extends Security.Authenticator {
    
    @Override
    public String getUsername(Context ctx) {
		return ctx.session().get("username");
    }
    
    @Override
    public Result onUnauthorized(Context ctx) {
		return redirect(routes.Application.getLogin());
    }
    
}
