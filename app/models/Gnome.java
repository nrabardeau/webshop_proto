package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Gnome entity class
 *
 */
@Entity
public class Gnome extends Model{

	/**
	 * Identifier automatically generated when a new gnome type
	 * is created
	 */
	@Id
	public Long id;

	@Constraints.Required
	public String type;

	
	public String descritpion;

	public int price;

	@Constraints.Required
	public int quantity;

	public Gnome(String type, String desc, int price, int quantity) {
		super();
		this.type = type;
		this.descritpion = desc;
		this.price = price;
		this.quantity = quantity;
	}

	// Queries
	/**
	 * Finder tool used to query the database
	 */
	public static Model.Finder<Long, Gnome> find = new Model.Finder(
			Long.class, Gnome.class);
	
	/**
	 * Create and persist a new gnome
	 * @param type
	 * @param desc
	 * @param price
	 * @param quantity
	 * @return
	 */
	public static Gnome create(String type, String desc, int price, int quantity){
		Gnome g = new Gnome(type, desc, price, quantity);
		g.save();
		return g;
	}

	/**
	 * Retrieve a gnome from its id
	 * @param id
	 * @return
	 */
	public static Gnome findById(Long id) {
		return find.ref(id);
	}

	/**
	 * Remove [quantity] gnomes of type [id]
	 * @param id
	 * @param quantity
	 * @return true if the buy action has been successful
	 * false instead
	 */
	public static boolean buy(Long id, int quantity) {
		Gnome g = findById(id);
		boolean ret = false;
		if (g != null){
			if (g.quantity < quantity){
			}else{
				g.quantity -= quantity;
				g.save();
				ret = true;
			}
		}
		return ret;
	}
	
	public static void decreaseQuantity(Long id, int q) throws RuntimeException {
		Gnome g = find.ref(id);
		if ((g.getQuantity() - q) >= 0) {
			g.setQuantity(g.getQuantity() - q);
			g.update();
		} else {
			throw new RuntimeException(
					"Tried to buy more gnome than available quantity");
		}
	}

	/**
	 * Retrieve all the gnomes types
	 * @return list of all the gnomes types
	 */
	public static List<Gnome> getAll(){
		return find.all();
	}
	
	/**
	 * Administrative tool used to add [q] gnomes to the stock
	 * of gnome type [id]
	 * @param id
	 * @param q
	 */
	// Only used by administrator
	public static void increaseQuantity(Long id, int q) {
		Gnome g = find.ref(id);
		g.setQuantity(g.getQuantity() + q);
		g.update();
	}

	/**
	 * Administrative tool used to remove a gnome type
	 * and all the related stock
	 * @param id
	 */
	public static void removeGnomeType(Long id){
		Gnome g = find.ref(id);
		g.delete();
	}

	/*
	 * Getters/Setters
	 */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescritpion() {
		return descritpion;
	}

	public void setDescritpion(String descritpion) {
		this.descritpion = descritpion;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((descritpion == null) ? 0 : descritpion.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + price;
		result = prime * result + quantity;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gnome other = (Gnome) obj;
		if (descritpion == null) {
			if (other.descritpion != null)
				return false;
		} else if (!descritpion.equals(other.descritpion))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (price != other.price)
			return false;
		if (quantity != other.quantity)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	
}
