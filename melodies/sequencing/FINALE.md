(def using-arrow
  (->>  (split words #"\s")
        (map capitalize)
        (filter (fn [s] (<= 3 (count s))))
        (join "\n")))
(println using-arrow)

val composed = split andThen capitalize andThen filter andThen join
def composed = split >> capitalize >> filter >> join