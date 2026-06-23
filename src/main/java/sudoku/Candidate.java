package sudoku;

public class Candidate implements Cloneable, Comparable<Candidate> {
    private int value;
    private int index;

    public Candidate() {
    }

    public Candidate(int index, int value) {
        this.index = index;
        this.value = value;
    }

    public int compareTo(Candidate o) {
        int ret = this.value - o.value;
        if (ret == 0) {
            ret = this.index - o.index;
        }

        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Candidate)) {
            return false;
        }

        Candidate c = (Candidate) o;
        return this.index == c.index && this.value == c.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.value;
        return 29 * hash + this.index;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return this.index + "/" + this.value;
    }
}
