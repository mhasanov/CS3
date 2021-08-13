import student.TestCase;

/**
 * BufferNode Test will test all the methods in BufferNode to ensure they
 * perform as expected.
 *
 * @author aribali3
 * @author Mehdi Hasanov
 * @version Aug 08, 2021
 */
public class BufferNodeTest
    extends TestCase
{
    private BufferNode b1;

    /**
     * Sets up each of the test cases.
     */
    public void setUp()
    {
        b1 = new BufferNode();

    }


    /**
     * Test to ensure that getCurrBuff works as expected.
     */
    public void testGetCurrBuff()
    {
        System.out.println(b1.getCurrBuff().toString());

    }
}
