package pl.afyaan.encryptor.options;

/**
 * @author AFYaan
 * @created 09.03.2021
 * @project JJL-Encryptor
 */

public class Option {
    private String name;
    private String description;

    public Option(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
