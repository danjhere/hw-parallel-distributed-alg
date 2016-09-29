# wait-notify-long-fifo

Assignment
 - write two classes: Fruit and FruitDemo
 - both classes shall be in the package: com.abc.fruit

Fruit class
 - Fruit shall be a self-running object (that is, it starts up an internal thread during construction).
 - The constructor for Fruit shall take a single argument of type String which is the name of the instance
 - The internal thread for a Fruit instance shall print the name supplied at construction 20 times, each time on a line by itself (System.out.println(name)). Optionally, the internal thread may sleep for a short period of time between printing lines.

FruitDemo class
 - FruitDemo is run (that is, it has the main() method).
 - Inside main(), create at least two instances of Fruit each with a different name.
 - When run each instance shall print 20 lines of output.
 - Adding Thread.sleep(long) calls inside Fruit will show more clearly that two threads are running at the same time, but as stated earlier, the sleep part is optional.
