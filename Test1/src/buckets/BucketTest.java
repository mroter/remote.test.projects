/**
 * 
 */
package buckets;

import static org.junit.Assert.*;

import org.junit.Before; // for @Before
import org.junit.Test;

/**
 * @author M.Roter
 *
 */
public class BucketTest {

	/**
	 * @throws java.lang.Exception
	 */
	
	private Bucket bucket;
	
	@Before 
	public void initialize() { 
		bucket = new Bucket(10);
	}

	/**
	 * Test method for {@link buckets.Bucket#Bucket(int)}.
	 */
	@Test
	public void testBucket() {
		bucket = new Bucket(5);
	}

	/**
	 * Test method for {@link buckets.Bucket#getCapacity()}.
	 */
	@Test
	public void testGetCapacity() {
		assertEquals("Capacity 10l", 10, bucket.getCapacity() );
	}

	/**
	 * Test method for {@link buckets.Bucket#getQuantity()}.
	 */
	@Test
	public void testGetQuantity() {
		assertEquals("Quantity 0l", 0, bucket.getQuantity() );
	}

	/**
	 * Test method for {@link buckets.Bucket#fill(int)}.
	 */
	@Test
	public void testFillInt() {
		bucket.fill(5);
		assertEquals("Quantity 5l", 5, bucket.getQuantity() );
	}

	/**
	 * Test method for {@link buckets.Bucket#fill()}.
	 */
	@Test
	public void testFill() {
		bucket.fill();
		assertEquals("Quantity 10l", 10, bucket.getQuantity() );
	}

	/**
	 * Test method for {@link buckets.Bucket#empty()}.
	 */
	@Test
	public void testEmpty() {
		bucket.empty();
		assertEquals("Quantity 0", 0, bucket.getQuantity() );
	}

	/**
	 * Test method for {@link buckets.Bucket#pourInto(buckets.Bucket)}.
	 */
	@Test
	public void testPourInto() {
		Bucket target = new Bucket(5);
		bucket.fill(7);
		bucket.pourInto(target);
		assertArrayEquals(new int[]{5,2},new int[]{target.getQuantity(),bucket.getQuantity()});
	}

}
