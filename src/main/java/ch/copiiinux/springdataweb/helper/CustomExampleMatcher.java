package ch.copiiinux.springdataweb.helper;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.ExampleMatcher;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CustomExampleMatcher {
    public static final ExampleMatcher DEFAULT = ExampleMatcher.matching()
                                                               .withIgnoreCase()
                                                               .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
}

