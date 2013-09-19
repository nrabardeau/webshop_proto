package controllers;

import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

/**
 * Security class used to restrict access to administrative tools to
 * allowed users (administrators)
 *
 */
public class SecuredAdmin extends Security.Authenticator {
	
    @Override
    public String getUsername(Context ctx) {
		return ctx.session().get("admin");
    }
    
    @Override
    public Result onUnauthorized(Context ctx) {
    	if(ctx.session().get("username") != null){
    		return redirect(routes.Shop.getShop());
    	}else{
		return redirect(routes.Application.getLogin());
    	}
    }

}
