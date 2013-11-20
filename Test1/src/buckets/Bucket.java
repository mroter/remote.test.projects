package buckets;

/**
 * @author M.Roter
 *
 */
public class Bucket {
	private int capacity;
	private int quantity;
	
	public Bucket(int capacity){
		this.capacity = capacity;
		this.quantity =0;
	}
	
	/**
	 * Get the capacity of the bucket
	 * @return capacity
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Get the actual quantity of water in the bucket
	 * @return quantity 
	 */
	public int getQuantity() {
		return quantity;
	}
	
	/**
	 * Fill the bucket with the specified quantity 
	 * @param quantity
	 */
	public void fill(int quantity) {
		int amount = this.quantity + quantity;
		if (amount > this.capacity) {
			this.quantity = this.capacity;
		} else {
			this.quantity = amount;
		}
	}
	
	/**
	 * Fills the bucket.
	 */
	public void fill() {
		this.quantity = this.capacity;
	}
	
	/**
	 * Empties the bucket.
	 */
	public void empty() {
		this.quantity = 0;
	}
	
	/**
	 * Pours the content of this bucket into another bucket.
	 */
	public void pourInto(Bucket target){
		int reserve = target.capacity - target.quantity;
		if (reserve < this.quantity) {
			target.fill();
			this.quantity -= reserve;			
		} else {
			target.fill(this.quantity);
			this.empty();
		}
	}
}
