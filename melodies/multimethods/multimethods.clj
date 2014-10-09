(def debit-journal [:debit 25 "1 Oct 2014" "invoice"])
(def credit-journal [:credit 26 "3 Oct 2014" "payment"])

(defmulti save-j (fn [j] (first j)))

(defmethod save-j :debit [j]
  (println "DEBIT: " j))
(defmethod save-j :credit [j]
  (println "CREDIT: " j))

(save-j debit-journal)
(save-j credit-journal)
