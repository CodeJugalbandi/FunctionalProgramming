package examples;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sequencing {
    public static void main(String[] args) {
        String sentence = "all mimsy were the borogoves";
        String capitalized = Arrays.asList(sentence.split(" "))
                .stream().map(String::toUpperCase)
                .filter(w -> w.length() <= 3)
                .collect(Collectors.joining(" "));

        System.out.println("capitalized = " + capitalized);


        Function<String, Stream<String>> split =
                s -> Arrays.asList(s.split(" ")).stream();

        Function<Stream<String>, Stream<String>> capitalize =
                words -> words.map(String::toUpperCase);

        Function<Stream<String>, Stream<String>> filter =
                words -> words.filter(word -> word.length() <= 3);

        Function<Stream<String>, String> join =
                words -> words.collect(Collectors.joining(" "));

        Function<String, String> andThenedSequence = split.andThen(capitalize).andThen(filter).andThen(join);
        System.out.println("andThenedSequence.apply(sentence) = " + andThenedSequence.apply(sentence));

        Function<String, String> composedSequence = join.compose(filter).compose(capitalize).compose(split);
        System.out.println("composedSequence.apply(sentence) = " + composedSequence.apply(sentence));

    }
}
