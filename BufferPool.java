import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.nio.ByteOrder;
import java.io.File;
import java.io.IOException;

/**
 * This class includes buffer pools which will store the data (byte) from the
 * binary file.
 *
 * @author aribali3
 * @author Mehdi Hasanov
 * @version Aug 08, 2021
 */
public class BufferPool
{
    private RandomAccessFile       randomAccFile;
    private LinkedList<BufferNode> buffInPoolList;
    private int                    maxBuff;
    /**
     * Buffer can hold this many bytes.
     */
    public static final int        SIZE_BUFF = 4096;
    private int                    writes    = 0;
    private int                    reads     = 0;
    private int                    misses    = 0;
    private int                    hits      = 0;

    /**
     * Constructor for the BufferPool class.
     *
     * @param noBuff
     *            number of buffers that can be stored in the pool.
     * @param binFile
     *            the file that buffer pool interacts with.
     * @throws FileNotFoundException
     *             if the file does not exist.
     */
    public BufferPool(int noBuff, File binFile)
        throws FileNotFoundException
    {
        buffInPoolList = new LinkedList<BufferNode>();
        maxBuff = noBuff;
        randomAccFile = new RandomAccessFile(binFile, "rw");
    }


    /**
     * Getter for the disk writes.
     *
     * @return the number of times the data was written to the disk.
     */
    public int getWrites()
    {
        return writes;
    }


    /**
     * Getter for the disk reads.
     *
     * @return the number of times the disk was read.
     */
    public int getReads()
    {
        return this.reads;
    }


    /**
     * Getter for the cache misses.
     *
     * @return the number of cache misses.
     */
    public int getMisses()
    {
        return this.misses;
    }


    /**
     * Getter for the cache hits.
     *
     * @return the number of cache hits.
     */
    public int getHits()
    {
        return this.hits;
    }


    /**
     * Obtains the key value of the record at the specified index.
     *
     * @param noRecord
     *            record number to get the key.
     * @return value of the key.
     */
    public short getKey(long noRecord)
    {
        BufferNode currBuffNode = elemInBuffer(noRecord);
        if (currBuffNode == null)
        {
            currBuffNode = readDataIntoBuff(noRecord);
        }
        return getKey(currBuffNode, noRecord);
    }


    /**
     * Obtains the data of the bytes that are given.
     *
     * @param num1
     *            One of the data.
     * @param num2
     *            The other data.
     * @return the value that was created from the two bytes.
     */
    public short createShortVal(byte num1, byte num2)
    {
        int allocateNum = 2;
        ByteBuffer myBuffer = ByteBuffer.allocate(allocateNum);
        myBuffer.order(ByteOrder.BIG_ENDIAN);
        myBuffer.put(num1);
        myBuffer.put(num2);
        short result = myBuffer.getShort(0);
        return result;
    }


    /**
     * Outputs contents to the specified file.
     */
    public void flush()
    {
        for (BufferNode tempBuffNode : buffInPoolList)
        {
            if (tempBuffNode.getBuffModified())
            {
                outputDataToFile(tempBuffNode);
                boolean val = false;
                tempBuffNode.setBuffModified(val);
            }
        }
    }


    /**
     * Helper method which reads data into the buffer, and overwrites the data
     * if the buffer is full.
     *
     * @param noRecord
     *            the record whose blocks will be read.
     * @return the BufferNode of which the data was read.
     */
    private BufferNode readDataIntoBuff(long noRecord)
    {
        BufferNode currentNode;
        if (buffInPoolList.size() >= maxBuff)
        {
            currentNode = buffInPoolList.getLast();
            if (currentNode.getBuffModified())
            {
                outputDataToFile(currentNode);
            }

            buffInPoolList.removeLast();
        }
        currentNode = new BufferNode();

        int sizeBuff = 4096;
        long initialPos = ((noRecord * 4) / sizeBuff);
        initialPos = initialPos * sizeBuff;

        currentNode.setInitialBuffPos(initialPos);

        try
        {
            randomAccFile.seek(initialPos);
            randomAccFile.read(currentNode.getCurrBuff().getmyBytes());
            reads++;
        }
        catch (IOException except)
        {
            System.out.println("Could not read file.");
        }
        buffInPoolList.addFirst(currentNode);
        buffInPoolList.indexOf(currentNode);
        return currentNode;
    }


    /**
     * Obtains the key from the buffer.
     *
     * @param keyNode
     *            node with the key.
     * @param noRecord
     *            The number of record where the key is located.
     * @return the value of the key.
     */
    public short getKey(BufferNode keyNode, long noRecord)
    {
        long pos = noRecord * 4 - keyNode.getInitialBuffPos();
        short result = keyNode.getCurrBuff().getShort((int)pos);
        return result;
    }


    /**
     * Helper method which outputs the data out to the file.
     *
     * @param bufferNode
     *            node where the Buffer is which will be output.
     */
    private void outputDataToFile(BufferNode bufferNode)
    {
        try
        {
            randomAccFile.seek(bufferNode.getInitialBuffPos());
            randomAccFile.write(bufferNode.getCurrBuff().getmyBytes());
            writes = writes + 1;
        }
        catch (IOException except)
        {
            System.out.println("Couldn't write");
            except.printStackTrace();
        }
    }


    /**
     * Obtains the array which contains the record that is specified.
     *
     * @param elemNumber
     *            the number of the data which will be obtained.
     * @return the data containing the record.
     */
    public byte[] get(long elemNumber)
    {
        BufferNode currentBuffNode = elemInBuffer(elemNumber);
        if (currentBuffNode == null)
        {
            currentBuffNode = readDataIntoBuff(elemNumber);
        }
        return currentBuffNode.getCurrBuff().getDataRecords(
            (int)(elemNumber * 4 - currentBuffNode.getInitialBuffPos()));
    }


    /**
     * Helper method that obtains the node of the record.
     *
     * @param noRecord
     *            the number of the record to search.
     * @return bufferNode object where the record is.
     */
    private BufferNode elemInBuffer(long noRecord)
    {
        for (BufferNode tempBuffNode : buffInPoolList)
        {
            if (tempBuffNode.getInitialBuffPos() <= noRecord * 4
                && tempBuffNode.getInitialBuffPos() + SIZE_BUFF > noRecord * 4)
            {
                hits = hits + 1;
                return tempBuffNode;
            }
        }
        misses = misses + 1;
        return null;
    }


    /**
     * Sets the record given to the new byte data.
     *
     * @param elemNumber
     *            the number that will be overwritten.
     * @param dBytes
     *            the data the that record will be set to.
     */
    public void set(long elemNumber, byte[] dBytes)
    {
        BufferNode currentBuffNode = elemInBuffer(elemNumber);
        if (currentBuffNode == null)
        {
            currentBuffNode = readDataIntoBuff(elemNumber);
        }
        currentBuffNode.getCurrBuff().setNewerBytes(
            (int)(elemNumber * 4 - currentBuffNode.getInitialBuffPos()),
            dBytes);
        boolean bValue = true;
        currentBuffNode.setBuffModified(bValue);
    }


    /**
     * Outputs the records to the console as described in the instructions.
     *
     * @throws IOException
     *             if there's a problem with opening the file.
     */
    public void printOutRecords()
        throws IOException
    {
        int numb = 0;
        int val1 = 4095;
        int increment = 1024;
        int numOfDataItems = 8;
        for (long i = 0; i * 4 < randomAccFile.length() - val1; i += increment)
        {
            byte[] byteRecords = get(i);
            numb++;
            System.out.print(
                createShortVal(byteRecords[0], byteRecords[1]) + "\t"
                    + createShortVal(byteRecords[2], byteRecords[3]) + "\t");
            if (numb % numOfDataItems == 0)
            {
                System.out.print("\n");
            }
        }

    }
}
