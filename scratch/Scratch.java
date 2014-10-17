class Engine {
  public String toString() {
    return "SolidStateEngine";
  }
}

class LOHEngine extends Engine {
  public String toString() {
    return "LOHEngine";
  }
}

class Rocket {
  public void fly(Engine e) {
    System.out.println("Flying Rocket with Engine " + e);
  }
}

class MoonRocket extends Rocket {
  public void fly(LOHEngine e) {
    System.out.println("Flying MoonRocket with Engine " + e);
  }
}

class Scratch {
  public static void main(String[] args) {
    Rocket r = new Rocket();
    r.fly(new Engine());
    
    Rocket m = new MoonRocket();
    m.fly(new LOHEngine());
  }
}




// import java.util.List;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.function.Function;
//
//
// class Scratch {
//   public static List<Integer> squares(List<Integer> in) {
//     List<Integer> squared = new ArrayList<>();
//     for(Integer n : in) {
//       squared.add(n * n);
//     }
//     return squared;
//   }
//
//   public static List<Integer> cubes(List<Integer> in) {
//     List<Integer> cubes = new ArrayList<>();
//     for(Integer n : in) {
//       cubes.add(n * n * n);
//     }
//     return cubes;
//   }
//
//   public static List<Integer> power(Function<Integer, Integer> raiseBy, List<Integer> in) {
//     List<Integer> raisedBy = new ArrayList<>();
//     for(Integer n : in) {
//       raisedBy.add(raiseBy.apply(n));
//     }
//     return raisedBy;
//   }
//
//   public static void main(String... args) {
//     List<Integer> in = Arrays.asList(1, 2, 3, 4);
//     System.out.println(squares(in));
//     System.out.println(cubes(in));
//
//     Function<Integer, Integer> two = (n) -> n * n;
//     System.out.println(power(two, in));
//
//     Function<Integer, Integer> three = (n) -> n * n *n;
//     System.out.println(power(three, in));
//
//     Function<Integer, Integer> add1 = (x) -> x + 1;
//     Function<Integer, Integer> add2 = (x) -> x + 2;
//     System.out.println(add1.andThen(add2).apply(2));
//   }
// }


