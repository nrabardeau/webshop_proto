package controllers;

import models.User;
import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Main controller that handles Login, Signup and Logout
 * 
 */
public class Application extends Controller {

	public static class Login {
		@Required
		public String username;

		@Required
		@MinLength(6)
		public String password;
	}

	public static class Signup {
		@Required
		public String username;

		@Required
		@Email
		public String email;

		@Required
		@MinLength(6)
		public String password;
	}

	/**
	 * Get Login html page
	 * 
	 * @return
	 */
	public static Result getLogin() {
		return ok(views.html.login.render(form(Login.class)));
	}

	/**
	 * Handles Post of a login form
	 * 
	 * @return
	 */
	public static Result login() {
		Form<Login> filledForm = form(Login.class).bindFromRequest();

		if (filledForm.hasErrors()) {
			return badRequest(views.html.login.render(filledForm));
		} else {
			User user = User.authenticate(filledForm.get().username,
					filledForm.get().password);
			if (user != null) {
				if (user.isBanned()) {
					filledForm
							.reject("username",
									"You have been banned. Please contact an administrator to know why.");
					return badRequest(views.html.login.render(filledForm));
				} else {
					session("username", user.username);
					if (user.isAdmin()) {
						session("admin","true");
						return redirect(routes.Admin.adminpage());
					} else {
						return redirect(routes.Shop.getShop());
					}
				}
			} else {
				filledForm.reject("username",
						"Username and/or password are not correct");
				filledForm.reject("password",
						"Username and/or password are not correct");
				return badRequest(views.html.login.render(filledForm));
			}
		}
	}

	/**
	 * Get the Signup page
	 * 
	 * @return
	 */
	public static Result getSignup() {
		return ok(views.html.signup.render(form(Signup.class)));
	}

	/**
	 * Handle the Posted signup form
	 * 
	 * @return
	 */
	public static Result signup() {
		Form<Signup> filledForm = form(Signup.class).bindFromRequest();

		if (filledForm.hasErrors()) {
			return badRequest(views.html.signup.render(filledForm));
		} else {
			// create user
			if (User.exists(filledForm.get().username)) {
				filledForm.reject("username",
						"This username is already taken, sorry.");
				return badRequest(views.html.signup.render(filledForm));
			} else {
				User user = new User(filledForm.get().username,
						filledForm.get().password, false,
						filledForm.get().email);
				user.save();
				session("username", user.username);
				return redirect(routes.Shop.getShop());
			}
		}
	}

	/**
	 * Logout user and redirect him to index
	 * 
	 * @return
	 */
	public static Result logout() {
		session().clear();
		return redirect(routes.Application.index());
	}

	/**
	 * Get index
	 * 
	 * @return
	 */
	public static Result index() {
		String admin = session("admin");
		if(admin == null){
			return redirect(routes.Shop.getShop());
		}else{
			return redirect(routes.Admin.adminpage());
		}
		
	}

}
