(defn new-person [salutation f-name l-name]
  {:salutation salutation,
   :f-name f-name,
   :l-name l-name})

(println (new-person "Mr" "Eff" "Pee"))

(def new-mr (partial new-person "Mr"))
(println (new-mr "Eff" "Pee"))

(def new-joe
  (fn [salutation lname]
    (new-person salutation "Joe" lname)))
(println (new-joe "Mr" "Doe"))

(def new-joe-shorthand
  #(new-person % "Joe" "Doe"))

(println (new-joe-shorthand "Mrs"))

(def new-just-joe
  #(new-person %1 "Joe" %2))

(println (new-just-joe "Mrs" "Grow"))
