# Circular FIFO array

Implementation of a Thread safe [Circular Buffer](https://en.wikipedia.org/wiki/Circular_buffer).

The data is stored in the slots array. count is the number of items currently in the FIFO. When the FIFO is not empty, head is the index of the next item to remove. When the FIFO is not full, tail is the index of the next available slot to use for an added item. Both head and tail need to jump to index 0  when they "increment" past the last valid index of slots (this is what makes it circular).
