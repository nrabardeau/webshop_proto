package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Model class (but not persistent) that represents an order (pair of Gnome and
 * number of ordered items of this Gnome)
 * 
 */
public class Order {
	private Gnome gnome;
	private int n;
	
	public Order(Gnome g, int n){
		this.gnome = g;
		this.n = n;
	}
	
	/**
	 * Transform a Json string (contained in an user session) to a List of
	 * orders
	 * 
	 * @param json
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static List<Order> fromSession(String json)
			throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(json);
		List<Order> list = new ArrayList<>();
		for (JsonNode arrayElem : rootNode) {
			Long gnomeId = arrayElem.get("id").asLong();
			int n = arrayElem.get("n").asInt();
			Gnome gnome = Gnome.findById(gnomeId);
			Order order = new Order(gnome, n);
			list.add(order);
		}
		return list;
	}

	/**
	 * Transform a List of orders to a stringified Json object (to be stored in
	 * an user session)
	 * 
	 * @param list
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String toSession(List<Order> list)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode rootNode = mapper.createArrayNode();
		for(Order order : list){
			ObjectNode orderNode = mapper.createObjectNode();
			orderNode.put("id",order.getGnome().getId());
			orderNode.put("n",order.getN());
			rootNode.add(orderNode);
		}
		return mapper.writeValueAsString(rootNode);
	}

	public Gnome getGnome() {
		return gnome;
	}
	public void setGnome(Gnome gnome) {
		this.gnome = gnome;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	} 

}
