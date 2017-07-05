# Countdown Timer

1. This app counts down from 10 seconds to zero, one second at a time.
2. Only make changes to CountdownWorker.java
3. On MinuteSecondDisplay, use this method to display the number of seconds left:
       public void setSeconds(int seconds)
4. Sleep between calls to setSeconds, but be careful to adjust your sleep time
   to keep from drifting off.
5. Use System.currentTimeMillis() to find out the currnet time in ms. Calculate
   the actual elapsed time and compare it to the expected elapsed time and
   adjust the next sleep time accordingly.
6. After reaching 0, print the actual total elapsed time using
   System.out.println(). It should be between 9960 ms and 10040 ms. If you get
   something more like 10500 ms, that is too far off.
