import java.nio.ByteOrder;
import java.nio.ByteBuffer;

/**
 * This class creates a BufferNode, which stores information regarding the
 * buffer, its location in the program, and any modification made to it.
 *
 * @author aribali3
 * @author Mehdi Hasanov
 * @version Aug 08, 2021
 */
public class BufferNode {

	// private static final int EMPTY = -1;
	private static final int BUFFER_EMP = -1;
	// private boolean changed;
	private boolean buffModified;
	// private Buffer myBuffer;
	private Buffer currBuff;
	// private long blockID;
	private long initialBuffPos;

	/**
	 * Create a new BufferNode object.
	 */
	public BufferNode() {
		currBuff = new Buffer();
		initialBuffPos = BUFFER_EMP;
		buffModified = false;
	}

	/**
	 * Getter for currBuff.
	 *
	 * @return the buffer of this BufferNode.
	 */
	public Buffer getCurrBuff() {
		// public Buffer getBuffer()
		return currBuff;
	}

	/**
	 * Obtains the location of the starting data block for the buffer.
	 *
	 * @return the Byte Number of the buffer.
	 */
	public long getInitialBuffPos() {
		// public long getBlockID()
		return initialBuffPos;
	}

	/**
	 * Obtains the boolean value of whether the Buffer has been altered.
	 *
	 * @return true, if the buffer has been modified. False, otherwise.
	 */
	public boolean getBuffModified() {
		// public boolean isChanged()
		return buffModified;
	}

	/**
	 * This method sets the location from the file where the data of the node buffer
	 * begins.
	 *
	 * @param initialBlockID the long value that will be set.
	 */
	public void setInitialBuffPos(long initialBlockID) {
		// public void setBlockID(long startReadingPosition)
		this.initialBuffPos = initialBlockID;
	}

	/**
	 * Setter for the buffModified.
	 *
	 * @param buffModified whether the node has been altered.
	 */
	public void setBuffModified(boolean buffModified) {
		// public void setChanged(boolean changed)
		this.buffModified = buffModified;
	}

	/**
	 * Sets the current Buffer of this Node.
	 *
	 * @param currBuff the current Buffer.
	 */
	public void setcurrBuff(Buffer currBuff) {
		// public void setMyBuffer(Buffer myBuffer)
		this.currBuff = currBuff;
	}

	/**
	 * Helper class which will store the information in an array. It also provides
	 * getters and setters for those attributes.
	 *
	 * @author aribali3
	 * @author Mehdi Hasanov
	 * @version Aug 08, 2021
	 */
	protected class Buffer {
		// private byte[] data;
		private byte[] myBytes;

		/**
		 * Constructor for the Buffer Object.
		 */
		public Buffer() {
			myBytes = new byte[BufferPool.BUFFER_SIZE];
		}

		/**
		 * Obtains the record composed of byte key and value.
		 *
		 * @param index the index where the data begins.
		 * @return the newerBytes Array.
		 */
		public byte[] getDataRecords(int index) {
			// public byte[] getRecord(int pos)
			int value = 4;
			byte[] tempByte = new byte[value];
			int initialVal = 0;
			System.arraycopy(myBytes, index, tempByte, initialVal, value);
			return tempByte;
		}

		/**
		 * Obtains the value comprised of bytes at the (index) and (index + 1) position.
		 *
		 * @param index The index in the array where the value will be obtained.
		 * @return value from the array.
		 */
		public short getShort(int index) {
			// public short getShort(int pos)
			int allocateSize = 2;
			ByteBuffer myByteBuff = ByteBuffer.allocate(allocateSize);
			myByteBuff.order(ByteOrder.BIG_ENDIAN);
			myByteBuff.put(myBytes[index]);
			myByteBuff.put(myBytes[index + 1]);
			short result = myByteBuff.getShort(0);
			if (result < 0) {
				System.out.println("The value=  " + result + "was less than 0");
			}
			return result;
		}

		/**
		 * Getter for the myBytes array.
		 *
		 * @return elements in the array.
		 */
		public byte[] getmyBytes() {
			// public byte[] getData()
			return myBytes;
		}

		/**
		 * Setter for the myBytes Array.
		 *
		 * @param myBytes the data the will be set.
		 */
		public void setMyBytes(byte[] myBytes) {
			// public void setMyBytes(byte[] data)
			this.myBytes = myBytes;
		}

		/**
		 * This method will set the bytes in myBytes beginning at the position
		 * specified.
		 *
		 * @param index this is the index where bytes will be overwritten.
		 * @param items the bytes that will be written to the previous bytes.
		 */
		public void setNewerBytes(int index, byte[] items) {
			// public void setRecord(int pos, byte[] newData)
			for (int j = 0; j < items.length; j++) {
				myBytes[j + index] = items[j];
			}
		}
	}
}
