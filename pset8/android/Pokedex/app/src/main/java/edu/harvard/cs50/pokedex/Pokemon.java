package edu.harvard.cs50.pokedex;

public class Pokemon {
    private String name;
    private String url;
    private boolean Catch = false;

    public boolean isCatch() {
        return Catch;
    }

    public void setCatch(boolean aCatch) {
        Catch = aCatch;
    }

    Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

}
