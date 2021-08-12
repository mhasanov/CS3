import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A node in a linked list that references a buffer, it's position in the file,
 * and whether it has been changed or not.
 *  @author Joshua Rush (jdrush89)
 *  @author Benjamin Roble (broble)
 *  @version Nov 2, 2011
 */
public class BufferNode
{
    //The buffer this node is associated with.
    private Buffer myBuffer;
    //A flag to keep track of whether or not the buffer has changed.
    private boolean changed;
    //The starting position of the buffer in the file (the byte #)
    private long blockID;
    //A constant to represent the starting position of an empty buffer.
    private static final int EMPTY = -1;

    /**
     * Initialize the BufferNode, creating a new Buffer and setting the blockID
     * and changed flag.
     */
    public BufferNode() {
        myBuffer = new Buffer();
        blockID = EMPTY;
        changed = false;
    }
    // ----------------------------------------------------------
    /**
     * Return the buffer associated with this BufferNode.
     * @return the buffer
     */
    public Buffer getBuffer()
    {
        return myBuffer;
    }
    // ----------------------------------------------------------
    /**
     * Set the buffer associated with this BufferNode.
     * @param myBuffer the buffer to set
     */
    public void setMyBuffer(Buffer myBuffer)
    {
        this.myBuffer = myBuffer;
    }
    // ----------------------------------------------------------
    /**
     * Return whether or not the buffer has been changed since it read in data.
     * @return the whether the buffer has been changed
     */
    public boolean isChanged()
    {
        return changed;
    }
    // ----------------------------------------------------------
    /**
     * Set whether the buffer in this node has been changed.
     * @param changed whether the buffer has been changed
     */
    public void setChanged(boolean changed)
    {
        this.changed = changed;
    }
    // ----------------------------------------------------------
    /**
     * Return the byte number where the data block starts for the buffer
     * associated with this node.
     * @return the blockID
     */
    public long getBlockID()
    {
        return blockID;
    }
    // ----------------------------------------------------------
    /**
     * Set the byte position from the file where the data in this node's buffer starts.
     * @param startReadingPosition the blockID to set
     */
    public void setBlockID(long startReadingPosition)
    {
        this.blockID = startReadingPosition;
    }

    protected class Buffer{
    	private byte[] data;
        /**
         * Create a new Buffer, initializing the data array.
         */
        public Buffer()
        {
            data = new byte[BufferPool.BUFFER_SIZE];
        }
        // ----------------------------------------------------------
        /**
         * Return the byte array storing data.
         * @return the byte array
         */
        public byte[] getData()
        {
            return data;
        }

        // ----------------------------------------------------------
        /**
         * Set the data byte array.
         * @param data the data to set
         */
        public void setData(byte[] data)
        {
            this.data = data;
        }
        /**
         * Set the bytes in the data array to be the bytes in the newData array,
         * starting at the indicated position.  pos + newData.length should not be
         * greater than data.length.
         * @param pos the position to start overwriting bytes at
         * @param newData the byte array containing the bytes to be copied
         */
        public void setRecord(int pos, byte[] newData)
        {
            for(int i = 0; i < newData.length; i++)
            {
                data[i + pos] = newData[i];
            }
        }
        /**
         * Return a record made up of a 2 byte key and 2 byte value.
         * @param pos the byte position where the record starts
         * @return the record
         */
        public byte[] getRecord(int pos)
        {
            byte[] record = new byte[4];
            System.arraycopy(data, pos, record, 0, 4);
            return record;
        }

        /**
         * Returns a short made up of the bytes in the array at position pos and
         * pos + 1.
         * @param pos the position in the data array to get the short from
         * @return a short from the data array
         */
        public short getShort(int pos)
        {
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.put(data[pos]);
            bb.put(data[pos+1]);
            short shortVal = bb.getShort(0);
            if (shortVal < 0)
                System.out.println("Short was negative: " + shortVal);
            return shortVal;
        }
    }

}
