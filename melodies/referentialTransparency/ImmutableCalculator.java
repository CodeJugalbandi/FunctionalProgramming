package referentialTransparency;

public class ImmutableCalculator {
    private Integer memory = 0;

    public ImmutableCalculator(Integer memory) {
        this.memory = memory;
    }

    public Integer recallMemory() {
        return memory;
    }

    public Integer add(Integer x, Integer y) {
        return x + y;
    }

    public ImmutableCalculator memoryPlus(Integer n) {
        return new ImmutableCalculator(add(memory, n));
    }
}
