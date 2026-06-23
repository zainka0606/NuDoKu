package solver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RestrictedCommon implements Comparable<RestrictedCommon>, Cloneable {
    private int als1;
    private int als2;
    private int cand1;
    private int cand2;
    private int actualRC;

    public RestrictedCommon() {
    }

    public RestrictedCommon(int als1, int als2, int cand1) {
        this.als1 = als1;
        this.als2 = als2;
        this.cand1 = cand1;
        this.cand2 = 0;
    }

    public RestrictedCommon(int als1, int als2, int cand1, int cand2) {
        this(als1, als2, cand1);
        this.cand2 = cand2;
    }

    public RestrictedCommon(int als1, int als2, int cand1, int cand2, int actualRC) {
        this(als1, als2, cand1, cand2);
        this.actualRC = actualRC;
    }

    public boolean checkRC(RestrictedCommon rc, boolean firstTry) {
        this.actualRC = this.cand2 == 0 ? 1 : 3;
        if (rc == null) {
            if (this.cand2 != 0) {
                this.actualRC = firstTry ? 1 : 2;
            }

            return this.actualRC != 0;
        } else {
            switch (rc.actualRC) {
                case 0:
                default:
                    break;
                case 1:
                    this.actualRC = this.checkRCInt(rc.cand1, 0, this.cand1, this.cand2);
                    break;
                case 2:
                    this.actualRC = this.checkRCInt(rc.cand2, 0, this.cand1, this.cand2);
                    break;
                case 3:
                    this.actualRC = this.checkRCInt(rc.cand1, rc.cand1, this.cand1, this.cand2);
            }

            return this.actualRC != 0;
        }
    }

    private int checkRCInt(int c11, int c12, int c21, int c22) {
        if (c12 == 0) {
            if (c22 == 0) {
                return c11 == c21 ? 0 : 1;
            } else if (c11 == c22) {
                return 1;
            } else {
                return c11 == c21 ? 2 : 3;
            }
        } else {
            if (c22 == 0) {
                return c11 != c21 && c12 != c21 ? 1 : 0;
            }

            if ((c11 != c21 || c12 != c22) && (c11 != c22 || c12 != c21)) {
                if (c11 == c22 || c12 == c22) {
                    return 1;
                } else {
                    return c11 != c21 && c12 != c21 ? 3 : 2;
                }
            } else {
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return "RC(" + this.als1 + "/" + this.als2 + "/" + this.cand1 + "/" + this.cand2 + "/" + this.actualRC + ")";
    }

    public int compareTo(RestrictedCommon r) {
        int result = this.als1 - r.als1;
        if (result == 0) {
            result = this.als2 - r.als2;
            if (result == 0) {
                result = this.cand1 - r.cand1;
                if (result == 0) {
                    result = this.cand2 - r.cand2;
                }
            }
        }

        return result;
    }

    @Override
    public Object clone() {
        try {
            return (RestrictedCommon) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while cloning (RC)", ex);
            return null;
        }
    }

    public int getAls1() {
        return this.als1;
    }

    public void setAls1(int als1) {
        this.als1 = als1;
    }

    public int getAls2() {
        return this.als2;
    }

    public void setAls2(int als2) {
        this.als2 = als2;
    }

    public int getCand1() {
        return this.cand1;
    }

    public void setCand1(int cand1) {
        this.cand1 = cand1;
    }

    public int getCand2() {
        return this.cand2;
    }

    public void setCand2(int cand2) {
        this.cand2 = cand2;
    }

    public int getActualRC() {
        return this.actualRC;
    }

    public void setActualRC(int actualRC) {
        this.actualRC = actualRC;
    }
}
