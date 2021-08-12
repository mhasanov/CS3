/**
 * 
 */



public class MaximumHeap {
    
    
    private long size;//n
    private long maxSize;//size
    
    private BufferPool pool;
    /**
     * 
     */
    public MaximumHeap(BufferPool pool, long size) {
        this.pool = pool;
        this.size = size;
        this.size = size;
        heapify();
    }
    
    
    
    /**
     * heapsize
     */
    public long getSize() {
        return size;
    }
    
    
    
    
    /**
     * 
     */
    public boolean isLeaf(long numb) {
        return (numb >= getSize()/2) && (numb < getSize());
    }
    
    
    
    /**
     * 
     */
    public long leftchild(long numb) {
        if(numb < getSize()/2) {
            return 2*numb + 1;
        }
    }
    
    
    
    
    
    
    /**
     * 
     */
    public long rightchild(long pos) {
        if(pos < (getSize()-1)/2) {
            return 2*pos + 2;
        }
    }
    
    
    
    /**
     * 
     */
    public long parent(long pos) {
        return (pos-1)/2;
    }
    
    
    /**
     * Heapify buildheap
     */
    public void heapify()
    {
        for (long i=getSize()/2-1; i>=0; i--)
        {
            siftdown(i);
        }
    }

    /**
     * 
     */
    private void siftdown(long i) {
        while (!isLeaf(i)) {
            long j = leftchild(i);
            if ((j<(getSize()-1)) &&
                (pool.getKey(j) < pool.getKey(j + 1)))
                j++;
            if (pool.getKey(i) >= (pool.getKey(j)))
                return;
            swap(i, j);
            i = j;
        }
    }
    
    
    
    /**
     * 
     */
    private void swap(long a, long b)
    {
        byte[] temp = pool.get(a);
        pool.set(a, pool.get(b));
        pool.set(b, temp);
    }
    
    
    
    
    /**
     * 
     */
    public byte[] removemax() {
        swap(0, --size);
        if (getSize() != 0) {
            siftdown(0);
        }
        return pool.get(getSize());
    }
    
    
    
    /**
     * 
     */
    public void insert(byte[] value) {
        assert size < maxSize : "Full";
        long temp = size++;
        pool.set(temp, value);
        while ((temp != 0)  &&
            (pool.getKey(temp) > (pool.getKey(parent(temp)))
                )) {
            swap(temp, parent(temp));
            temp = parent(temp);
        }
    }
    
    
    
    

    /**
     * 
     */
    public void heapsort() {
      for (int i=0; i<size; i++) {
          this.removemax();
      }
    }


}

