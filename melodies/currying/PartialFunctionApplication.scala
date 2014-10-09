def salute(salutation: String, fname: String, lname: String) = s"$salutation $fname $lname"

val saluteMr = salute("Mr.", _: String, _: String)
saluteMr("Ryan", "Lemmer") //Mr. Ryan Lemmer
saluteMr("Dhaval", "Dalal") //Mr. Dhaval Dalal

val saluteJoe = salute(_: String, "Joe", _: String)
saluteJoe("Mr.", "Doe") //Mr. Joe Doe
saluteJoe("Mrs.", "Grow") //Mrs. Joe Grow

