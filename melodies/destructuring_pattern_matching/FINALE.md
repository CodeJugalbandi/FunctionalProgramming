#### Destructuring
def showPerson(p: Vector[Any]) = p match {
   case Vector(name, latitude, fears) => println(s"$name - $latitude - $fears")  
}
(defn show-person [[name latitude fears]]
  (println name " - " latitude " - " fears))
#### Pattern Matching
def save(j: Journal) = j match {
    case Journal("debit", amt, date, metaData)  => println(s"DEBIT $amt $date $metaData")
    case Journal("credit", amt, date, metaData) => println(s"CREDIT $amt $date $metaData")
}
(defmulti save-j (fn [j] (first j)))
(defmethod save-j :debit [j]
  (println "DEBIT: " j))
(defmethod save-j :credit [j]
  (println "CREDIT: " j))
