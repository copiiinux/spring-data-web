package ch.copiiinux.springdataweb.validation;

public final class ValidationGroups {
    private ValidationGroups() {
    }

    /**
     * Applied on POST and PUT — all required fields must be present.
     */
    public interface Full {
    }

    /**
     * Applied on PATCH — fields are optional but validated when present.
     */
    public interface Patch {
    }
}