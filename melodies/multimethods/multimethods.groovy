class Engine { 
    String toString() { 'Engine' }
}

class LOHEngine extends Engine {
    String toString() { 'LOHEngine' }
}

class Rocket {
    def fly(Engine e) {
        println "Rocket Flying with $e"
    }
}

class MoonRocket extends Rocket {
    def fly(LOHEngine e) {
        println "MoonRocket Flying with $e"
    }
}

Rocket r = new Rocket()
r.fly(new Engine())

Rocket mr = new MoonRocket()
mr.fly(new LOHEngine())
