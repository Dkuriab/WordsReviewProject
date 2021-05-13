package wordReview;

public class Definition {
    private String word;

    public Definition(String word) {
        this.word = word;
    }

    public String getDef() {
        return word + "\n";
    }
}
