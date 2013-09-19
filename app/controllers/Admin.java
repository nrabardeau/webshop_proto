package controllers;

import controllers.Application.Login;
import models.Gnome;
import models.User;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Class containing tools used only by administrators
 * Can only be accessed by administrators
 *
 */
@Security.Authenticated(SecuredAdmin.class)
public class Admin extends Controller {
	
	/**
	 * Class defining a form used to create a new gnome type
	 *
	 */
	public static class CreateGnome{
		@Required
		public String type;
		
		@Required
		public String description;
		
		@Required
		public int price;
		
		@Required
		public int quantity;
		
	}
	
	/**
	 * Generate the admin view
	 * @return admin view
	 */
	public static Result adminpage(){
		return ok(views.html.admin.render(Gnome.getAll(), User.getAll()));
	}
	
	/**
	 * Unban a user
	 * @param username
	 * @return admin view, with user unbanned
	 */
	public static Result unban(String username){
		User.setBanned(username, false);
		return redirect(routes.Admin.adminpage());
	}
	
	/**
	 * Ban a user
	 * @param username
	 * @return admin view, with user unbanned
	 */
	public static Result ban(String username){
		User.setBanned(username, true);
		return redirect(routes.Admin.adminpage());
	}
	
	/**
	 * Add gnomes of an existing type to the stock
	 * @param id
	 * @param quantity
	 * @return admin view, with [quantity] gnomes added to stock of [id] gnome
	 */
	public static Result addGnomesToStock(Long id, int quantity){
		Gnome.increaseQuantity(id, quantity);
		return redirect(routes.Admin.adminpage());
	}
	
	/**
	 * Remove a gnome type and all the related stock
	 * @param id
	 * @return admin view, with the gnome type [id] removed
	 */
	public static Result removeGnomeType(Long id){
		Gnome.removeGnomeType(id);
		return redirect(routes.Admin.adminpage());
	}
	
	/**
	 * Generate the form used to create a new gnome type
	 * @return form used to create a new gnome type
	 */
	public static Result getCreateGnomeType(){
		return ok(views.html.creategnome.render(form(CreateGnome.class)));
	}

	/**
	 * Create a new gnome type
	 * @return admin view, with the new gnome type added
	 */
	public static Result createGnomeType(){
		Form<CreateGnome> filledForm = form(CreateGnome.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.creategnome.render(filledForm));
		} else {
			Gnome g = Gnome.create(filledForm.get().type, filledForm.get().description,
					filledForm.get().price, filledForm.get().quantity);
			return redirect(routes.Admin.adminpage());
		}
	}
	
}
