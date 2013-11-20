/**
 * 
 */
package buckets;

/**
 * @author M.Roter
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Bucket bucket4 = new Bucket(4);
		Bucket bucket7 = new Bucket(7);
		
		bucket4.fill();
		bucket4.pourInto(bucket7);
		bucket4.fill();
		bucket4.pourInto(bucket7);
		bucket7.empty();
		bucket4.pourInto(bucket7);
		bucket4.fill();
		bucket4.pourInto(bucket7);
		int amount = bucket7.getQuantity();
		System.out.println("Bucket7 has: " + amount + " liters of water now.");
	}

}
