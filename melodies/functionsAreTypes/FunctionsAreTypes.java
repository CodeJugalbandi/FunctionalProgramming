@FunctionalInterface
interface Bool {
  Object choose(Object x, Object y);

  public static final Bool True = new Bool() {
    public Object choose(Object x, Object y) {
      return x;
    }
    public String toString() { return "true"; }
  };
  
  public static final Bool False = new Bool() {
    public Object choose(Object x, Object y) {
      return y;
    }
    public String toString() { return "false"; }
  };
 
  default Bool not() {
    return (Bool) choose(Bool.False, Bool.True);
  }
  
  default Bool and(Bool other) {
    return (Bool) choose(other.choose(Bool.True, Bool.False), Bool.False);
  }
  
  default Bool or(Bool other) {
    return (Bool) choose(Bool.True, other.choose(Bool.True, Bool.False));
  }
  
  default Bool xor(Bool other) {
    return (Bool) choose(other.choose(Bool.False, Bool.True), other.choose(Bool.True, Bool.False));
  }
}

class FunctionsAreTypes {
  public static void main(String[] args) {
    Bool truthy = Bool.True;
    Bool falsy = Bool.False;
    
    System.out.println(truthy.choose(new Integer(10), new Integer(20)));
    System.out.println(falsy.choose(new Integer(10), new Integer(20)));
    
    System.out.println("Negation...");
    System.out.println(truthy.not());
    System.out.println(falsy.not());
    
    System.out.println("ANDing...");
    System.out.println(falsy.and(falsy));
    System.out.println(falsy.and(truthy));
    System.out.println(truthy.and(falsy));
    System.out.println(truthy.and(truthy));
    
    System.out.println("ORing...");
    System.out.println(falsy.or(falsy));
    System.out.println(falsy.or(truthy));
    System.out.println(truthy.or(falsy));
    System.out.println(truthy.or(truthy));
    
    System.out.println("XORing...");
    System.out.println(falsy.xor(falsy));
    System.out.println(falsy.xor(truthy));
    System.out.println(truthy.xor(falsy));
    System.out.println(truthy.xor(truthy));
    System.out.println("DONE");
  }
}
