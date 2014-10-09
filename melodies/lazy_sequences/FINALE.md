(->> (range 1 1000)
     (map (partial * 2))
     (filter (partial > 10)))

val s = Stream(1 to 10:  _*)
        .map(_ * 2)  
        .filter(_ < 5)
        .take(2).force
        