package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Gnome;
import models.Order;
import models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.cache.Cache;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Class providing methods enabling a user to interact with the webshop
 * Can only be access to logged in users who are not banned 
 *
 */
@Security.Authenticated(Secured.class)
public class Shop extends Controller {
	private static final Logger logger = LoggerFactory.getLogger(Shop.class);
	private static final String err = "An error with your session. Sorry. Try to logout and relogin.";

	/**
	 * Class defining a form used for payment
	 *
	 */
	public static class Payment {
		@Required
		public String firstname;

		@Required
		public String lastname;

		@Required
		public String address;

		@Required
		public String city;

		@Required
		public Long postalcode;

		@Required
		public Long cartnumber;

		@Required
		public int secretcode;
	}

	/**
	 * Generate shop view
	 * @return shop view
	 */
	//@SuppressWarnings("unchecked")
	public static Result getShop() {
		try {
			// retrieve session
			String ordersJson = session("cart");
//			List<Gnome> allGnomes;
//			List<Gnome> cachedGnomes = (List<Gnome>) Cache.get("gnomeslist"); 
//			if(cachedGnomes == null){
//				System.out.println("cache null");
//				allGnomes = Gnome.getAll();
//				Cache.set("gnomeslist", allGnomes);
//			}else{
//				allGnomes = cachedGnomes;
//			}
			List<Gnome> allGnomes = Gnome.getAll();
			if (ordersJson != null) {
				List<Order> orders = Order.fromSession(ordersJson);
				return ok(views.html.shoplist.render(orders,
						session("username"), allGnomes));
			} else {
				List<Order> empty = new ArrayList<>();
				return ok(views.html.shoplist.render(empty,
						session("username"), allGnomes));
			}
		} catch (IOException e) {
			logger.error("wrong cookie");
			session().clear();
			return redirect(routes.Application.index());
		}
	}

	/**
	 * Empty the user shopping cart
	 * @return shop view, with an empty shopping cart
	 */
	public static Result emptyCart() {
		session().remove("cart");
		return redirect(routes.Shop.getShop());
	}
	
	/**
	 * Add [quantity] gnomes of type [id] to the
	 * user shopping cart
	 * @param id
	 * @param quantity
	 * @return shop view, with selected gnomes added to shopping cart
	 */
	public static Result addGnome(Long id, int quantity){
		String ordersJson = session("cart");
		Gnome g = Gnome.findById(id);
		if (ordersJson != null) { 
			List<Order> orders;
			try {
				orders = Order.fromSession(ordersJson);
				boolean found = false;
				for(Order o : orders){
					if (o.getGnome().equals(g)){
						found = true;
						if (o.getN() + quantity <= g.getQuantity()){
							o.setN(o.getN()+quantity);
						}
					}
				}
				if(found == false){
					if(quantity <= g.getQuantity()){
						orders.add(new Order(g,quantity));
					}
				}
				session("cart",Order.toSession(orders));
				return redirect(routes.Shop.getShop());
			} catch (IOException e) {
				logger.error("wrong cookie");
				session().clear();
				return redirect(routes.Application.index());
			}
			
		}else{
			try {
				List<Order> newOrders = new ArrayList<Order>();
				if(quantity <= g.getQuantity()){
					newOrders.add(new Order(g,quantity));
				}
				session("cart",Order.toSession(newOrders));
				return redirect(routes.Shop.getShop());
			} catch (IOException e) {
				logger.error("wrong cookie");
				session().clear();
				return redirect(routes.Application.index());
			}
			
		}
	}

	/**
	 * Generate the payment form
	 * @return payment form or shop view 
	 * if the user shopping cart is empty
	 */
	public static Result getBuy() {
		try {
			// Get shopping cart
			String ordersJson = session("cart");
			if (ordersJson != null) {
				List<Order> orders = Order.fromSession(ordersJson);
				return ok(views.html.shoppayment.render(form(Payment.class),
						orders, session("username")));
			} else {
				return redirect(routes.Shop.getShop());
			}

		} catch (IOException e) {
			logger.error("wrong cookie");
			session().clear();
			return redirect(routes.Application.index());
		}
	}
	
	/**
	 * Enable a user to buy the gnomes of his shopping cart.
	 * Check the actual stock of gnomes and if it is sufficient,
	 * remove gnomes from database and accept user payment
	 * @return a view informing the user of the success
	 *  of his payment if he isn't banned and if there is enough gnomes
	 */
	public static Result buy() {
		Form<Payment> filledForm = form(Payment.class).bindFromRequest();

		if (filledForm.hasErrors()) {
			try {
				// Get shopping cart
				String ordersJson = session("cart");
				if (ordersJson != null) {
					List<Order> orders = Order.fromSession(ordersJson);
					return ok(views.html.shoppayment.render(filledForm,
							orders, session("username")));
				} else {
					return redirect(routes.Shop.getShop());
				}
			} catch (IOException e) {
				logger.error("wrong cookie");
				session().clear();
				return redirect(routes.Application.index());
			}
		} else {
			// verifying if not banned user
			User user = User.getUser(session("username"));
			if (user.isBanned()) {
				session().clear();
				return badRequest(views.html.banned.render());
			} else {
				// get shopping cart and remove gnomes
				try {
					// Get shopping cart
					String ordersJson = session("cart");
					if (ordersJson != null) {
						List<Order> orders = Order.fromSession(ordersJson);
						for (Order order : orders) {
							Gnome g = order.getGnome();
							Gnome.decreaseQuantity(g.getId(), order.getN());
						}
						session().remove("cart");
						return redirect(routes.Shop.paymentSuccess());
					} else {
						return redirect(routes.Shop.getShop());
					}

				} catch (IOException | RuntimeException e) {
					logger.error("wrong cookie");
					session().clear();
					return redirect(routes.Application.index());
				}
			}
		}
	}

	/**
	 * Generate a view informing the user of the success of his payment
	 * @return view informing the user of the success of his payment
	 */
	public static Result paymentSuccess() {
		User user = User.getUser(session("username"));
		return ok(views.html.shoppaymentsuccess.render(user));
	}

}
