public class MyArrayList<E> {
    private final Object[] data;  // internal array
    private int size = 0;         // number of elements stored

    // Constructor with specified capacity
    public MyArrayList(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.data = new Object[capacity];
    }

    // Add element to the end
    public void add(E element) {
        if (size >= data.length) {
            throw new RuntimeException("Array is full. Use larger capacity.");
        }
        data[size++] = element;
    }

    // Get element at index
    public E get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return (E) data[index]; // still safe because only E's are added
    }

    // Return current number of elements
    public int size() {
        return size;
    }
}
