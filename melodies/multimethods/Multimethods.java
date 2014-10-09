package multimethods;


class Engine {
    public String toString() { return "Engine"; }
}

class LOHEngine extends Engine {
    public String toString() { return "LOHEngine"; }
}

class Rocket {
    void fly(Engine e) {
        System.out.println("Rocket Flying with " + e);
    }
}

class MoonRocket extends Rocket {
    void fly(LOHEngine e) {
        System.out.println("MoonRocket Flying with " + e);
    }
}

public class Multimethods {
    public static void main(String[] args) {
        Rocket r = new Rocket();
        r.fly(new Engine());

        Rocket mr = new MoonRocket();
        mr.fly(new LOHEngine());
    }
}
