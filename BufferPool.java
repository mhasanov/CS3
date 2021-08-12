import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * 
 */
public class BufferPool
{
    // TODO use custom LinkedList
    private LinkedList<BufferNode> bufferLL;
    
    
    
    
    
    
    private int numberOfBuffers;
    private RandomAccessFile file;
    private int hits = 0;
    private int misses = 0;
    private int reads = 0;
    private int writes = 0;


    /**
     * 
     */
    public int getMisses() {
        return misses;
    }
    /**
     * 
     */
    public int getHits() {
        return hits;
    }
    /**
     * 
     */
    public int getReads() {
        return reads;
    }
    /**
     * 
     */
    public int getWrites() {
        return writes;
    }
    
    
    /**
     * 
     */
    public static final int bSize = 4096;
    /**
     * 
     */
    public BufferPool(int numberOfBuffers, File file)
    {
        this.numberOfBuffers = numberOfBuffers;
        this.file = new RandomAccessFile(file, "rw");
        bufferLL = new LinkedList<BufferNode>();
    }

    

    
    
    /**
     * bufferContains = has
     */
    private BufferNode has(long recordNumb) {
        
        for(BufferNode node : bufferLL) {
            if(node.getBlockID() <= recordNumb*4) {
                if(node.getBlockID() + bSize > recordNumb*4) {
                    hits++;
                    return node;
                }
            }
        }
        misses++;
        return null;
    }

    
    
    
    
    /**
     * 
     */
    public short getKey(BufferNode node, long recordNumber)
    {
        long pos = recordNumber * 4 - node.getBlockID();
        return node.getBuffer().getShort((int)pos);
    }
    
    
    
    
    /**
     * requestKey = getKey
     */
    public short getKey(long recordNumb) {
        BufferNode node = has(recordNumb);
        if(node == null) {
            node = bufferRead(recordNumb);
        }
        return getKey(node, recordNumb);
    }
    
    
    /**
     * 
     */
    private BufferNode bufferRead(long recordNumber) {
        BufferNode node;

        if(bufferLL.size() >= numberOfBuffers) {
            node= bufferLL.getLast();
            if(node.isChanged())
                writeToFile(node);

            bufferLL.removeLast();
        }
        node = new BufferNode();


        long startReadingPosition = recordNumber * 4;

        node.setBlockID(startReadingPosition);
        file.seek(startReadingPosition);
        file.read(node.getBuffer().getData());
        reads++;
        bufferLL.addFirst(node);
        bufferLL.indexOf(node);
        return node;
    }
    
    

    
    
    /**
     * setRecord = set
     */
    public void set(long recordNumber, byte[] data)
    {
        BufferNode node = has(recordNumber);
        if (node == null) {
            node = bufferRead(recordNumber);
        }
        node.getBuffer().set((int)(recordNumber * 4 - node.getBlockID()), data);
        node.setChanged(true);
    }
    
    
    
    /**
     * 
     */
    private void writeToFile(BufferNode node) {
        file.seek(node.getBlockID());
        file.write(node.getBuffer().getData());
        writes++;
    }
    
    
    
    /**
     * getRecord = get
     */
    public byte[] get(long recordNumber)
    {
        BufferNode node = has(recordNumber);
        if (node == null) {
            node = bufferRead(recordNumber);
        }
        return node.getBuffer().get((int)(recordNumber * 4 - node.getBlockID()));
    }
    
    
    /**
     * 
     */
    public void flush()
    {
        for(BufferNode bNode : bufferList) {
            if(bNode.isChanged())
            {
                writeToFile(bNode);
                bNode.setChanged(false);
            }
        }
    }
    /**
     * 
     */
    public void print()
    {
        int j = 0;
        for(long i=0; i*4<file.length() - 4095; i+=1024)
        {
            byte[] record = get(i);
            j++;
            System.out.print(makeShort(record[0],record[1])
                + "\t" + makeShort(record[2], record[3]) + "\t");
            if(j%8 == 0) {
                System.out.print("\n");
            }
        }
    }
    
    
    
    
    
    
    
    /**
     * 
     */
    public short makeShort(byte a, byte b)
    {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(a);
        buffer.put(b);
        short shortVal = buffer.getShort(0);
        return shortVal;
    }
    
    
    

}
