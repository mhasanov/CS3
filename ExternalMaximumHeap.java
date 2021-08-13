import java.io.IOException;

/**
 * This class serves as the data structure of a maximum heap, which will be
 * utilized to store the elements of the program. The class will also sort the
 * data.
 *
 * @author aribali3
 * @author Mehdi Hasanov
 * @version Aug 08, 2021
 */
public class ExternalMaximumHeap
{
    private BufferPool myBufferPool;
    private long       maxSizeHeap;
    private long       itemsInHeap;

    /**
     * Create a new ExternalMaximumHeap object.
     *
     * @param myBufferPool
     *            The data structure will communicate with this bufferPool
     *            object.
     * @param maxSizeHeap
     *            Max number of elements in the data structure.
     * @throws IOException
     *             when there is a problem with the File I/O.
     */
    public ExternalMaximumHeap(BufferPool myBufferPool, long maxSizeHeap)
        throws IOException
    {
        this.myBufferPool = myBufferPool;
        itemsInHeap = maxSizeHeap;
        this.maxSizeHeap = maxSizeHeap;
        heapify();
    }


    /**
     * This method will heapify all the elements of the data structure by
     * calling helper functions.
     *
     * @throws IOException
     *             when there is a problem with the file I/O.
     */
    public void heapify()
        throws IOException
    {
        for (long elemIndex = itemsInHeap / 2 - 1; elemIndex >= 0; elemIndex--)
        {
            heapifyHelper(elemIndex);
        }
    }


    /**
     * This method will sort the elements of the heap.
     *
     * @throws IOException
     *             if there is an issue with file I/O.
     */
    public void heapSortElements()
        throws IOException
    {
        for (int elemIndex = 0; elemIndex < maxSizeHeap; elemIndex++)
        {
            this.delMaxElem();
        }
    }


    /**
     * Helper method for the heapify. This will sift elements down until the
     * largest child is on the top.
     *
     * @param elemIndex
     *            the index of the element.
     */
    private void heapifyHelper(long elemIndex)
        throws IOException
    {
        while (!isElemLeaf(elemIndex))
        {
            long ind = elemLeftChild(elemIndex);
            if ((ind < (itemsInHeap - 1))
                && (myBufferPool.getKey(ind) < myBufferPool.getKey(ind + 1)))
            {
                ind++;
            }
            if (myBufferPool.getKey(elemIndex) >= (myBufferPool.getKey(ind)))
            {
                return;
            }

            swapElements(elemIndex, ind);
            elemIndex = ind;
        }
    }


    /**
     * Obtains the element's parent position.
     *
     * @param elemPosition
     *            the index of the child.
     * @return the position of the child's parent.
     */
    public long getPosParent(long elemPosition)
    {
        long result = (elemPosition - 1) / 2;
        return result;
    }


    /**
     * Checks whether the elements is a left element.
     *
     * @param elemIndex
     *            the index of the element.
     * @return true, if element is leaf, false otherwise.
     */
    public boolean isElemLeaf(long elemIndex)
    {
        return (elemIndex >= itemsInHeap / 2) && (elemIndex < itemsInHeap);
    }


    /**
     * Helper method which will swap the given elements in the data structure.
     *
     * @param firstElemPos
     *            Index of one of the elements that will be swapped.
     * @param secondElemPos
     *            Index of the other element that will be swapped.
     */
    private void swapElements(long firstElemPos, long secondElemPos)
        throws IOException
    {
        byte[] byteTemp = myBufferPool.get(firstElemPos);
        myBufferPool.set(firstElemPos, myBufferPool.get(secondElemPos));
        myBufferPool.set(secondElemPos, byteTemp);
    }


    /**
     * Obtains the index of item's left child.
     *
     * @param elemIndex
     *            position of the parent element.
     * @return the index of the parent's left child.
     */
    public long elemLeftChild(long elemIndex)
    {
        long result = 2 * elemIndex + 1;
        return result;
    }


    /**
     * This method will delete the highest element from the heap.
     *
     * @return the element that has been removed.
     * @throws IOException
     *             if there is a problem with I/O.
     */
    public byte[] delMaxElem()
        throws IOException
    {
        int number = 0;
        swapElements(number, --itemsInHeap);
        if (itemsInHeap != number)
        {
            heapifyHelper(number);
        }

        return myBufferPool.get(itemsInHeap);
    }
}
