package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Model class that represents an user
 * 
 */
@Entity
public class User extends Model {

	@Id
	@Constraints.Required
	@Formats.NonEmpty
	public String username;

	@Constraints.Required
	public String password;

	@Constraints.Required
	public boolean isAdmin;

	@Constraints.Required
	public boolean isBanned;

	@Constraints.Required
	public String email;
	
	public User(String username, String password, String email) {
		super();
		this.username = username;
		this.password = password;
		this.isAdmin = false;
		this.email = email;
		this.isBanned = false;
	}

	public User(String username, String password, boolean isAdmin, String email) {
		super();
		this.username = username;
		this.password = password;
		this.isAdmin = isAdmin;
		this.email = email;
		this.isBanned = false;
	}


	// Queries to database

	public static Model.Finder<String, User> find = new Model.Finder(
			String.class, User.class);

	/**
	 * Get all users
	 * 
	 * @return
	 */
	public static List<User> getAll(){
		return find.all();
	}

	/**
	 * Authenticate an user with its username and password. If the username does
	 * not exist or if the password if wrong, null is returned.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static User authenticate(String username, String password) {
		return find.where().eq("username", username).eq("password", password)
				.findUnique();
	}

	/**
	 * Get an user according to its username. If the user does not exist, return
	 * null.
	 * 
	 * @param username
	 * @return
	 */
	public static User getUser(String username) {
		return find.ref(username);
	}

	/**
	 * Check if the user exists
	 * 
	 * @param username
	 * @return
	 */
	public static boolean exists(String username) {
		return (find.where().eq("username", username).findUnique() != null);
	}
	
	/**
	 * Set an user banned. Does nothing if the user does not exist.
	 * 
	 * @param username
	 * @param isBanned
	 */
	public static void setBanned(String username, boolean isBanned){
		User user = find.ref(username);
		if (user != null) {
			user.setBanned(isBanned);
			user.update();
		}
	}


	/*
	 * Getters/Setters
	 */

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}

}
