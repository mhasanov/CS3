import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;

/**
 * HeapSort will take an input file and the number of buffers, and it will sort
 * the file (in ascending order), using a modified version of Heapsort, and it
 * will display statistical information on the given output file.
 *
 * @author aribali3
 * @author Mehdi Hasanov
 * @version Aug 08, 2021
 */
public class HeapSort
{

    /**
     * Entry point of the program. This method will take exactly three arguments
     * from the command line, and process the input file with the input buffers.
     *
     * @param args
     *            the arguments for the program.
     * @throws IOException
     *             if there was an issue with processing the files.
     * @throws IllegalArgumentException
     *             if the number of arguments were not 3.
     */
    public static void main(String[] args)
        throws IOException
    {
        if (args.length != 3)
        {
            throw new IllegalArgumentException(
                "Exactly three arguments needed");
        }
        HeapSort heap = new HeapSort();
        heap.processInfo(args[0], args[1], args[2]);
    }


    /**
     * Outputs the info from the program to the given output file.
     *
     * @param resultFile
     *            The name of the output file.
     * @param finalTime
     *            Amount of time for the sorting.
     * @param fileName
     *            Statistics File.
     * @param bufferPool
     *            This objects statistics are printed.
     * @throws IOException
     *             when there is a problem with the file I/O.
     */
    public static void displayInfo(
        File resultFile,
        long finalTime,
        String fileName,
        BufferPool bufferPool)
        throws IOException
    {
        FileWriter inputStream = new FileWriter(resultFile, true);
        BufferedWriter writer = new BufferedWriter(inputStream);
        writer.append("STATS\n");
        writer.append("File Name: " + fileName + "\n");
        writer.append("Cache Hits: " + bufferPool.getHits() + "\n");
        writer.append("Cache Misses: " + bufferPool.getMisses() + "\n");
        writer.append("Disk Reads: " + bufferPool.getReads() + "\n");
        writer.append("Disk Writes: " + bufferPool.getWrites() + "\n");
        writer.append("Time to sort: " + finalTime + "\n");
        writer.close();
    }


    /**
     * This method will sort the binary file using the heapsort algorithm. Also,
     * it will store information about the sorting process.
     *
     * @param fileName
     *            Binary File that will be sorted.
     * @param noOfBuffers
     *            Buffers provided in the command line.
     * @param outputFile
     *            Statistics will be written to this file.
     * @throws IOException
     *             When there is an issue with file I/O.
     */
    public static
        void
        processInfo(String fileName, String noOfBuffers, String outputFile)
            throws IOException
    {
        File resultFile = new File(outputFile);
        File fileHeap = new File(fileName);
        BufferPool bufferPool =
            new BufferPool(Integer.parseInt(noOfBuffers), fileHeap);
        ExternalMaximumHeap sorterHeap =
            new ExternalMaximumHeap(bufferPool, fileHeap.length() / 4);
        long initialTime = System.currentTimeMillis();
        sorterHeap.heapSortElements();
        bufferPool.flush();
        long stoppedTime = System.currentTimeMillis();
        long finalTime = stoppedTime - initialTime;
        displayInfo(resultFile, finalTime, fileName, bufferPool);
        bufferPool.printOutRecords();
    }
}
