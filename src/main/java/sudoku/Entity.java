package sudoku;

public class Entity implements Cloneable {
    private int entityName;
    private int entityNumber;

    public Entity() {
    }

    public Entity(int name, int number) {
        this.entityName = name;
        this.entityNumber = number;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Entity)) {
            return false;
        }

        Entity c = (Entity) o;
        return this.entityName == c.entityName && this.entityNumber == c.entityNumber;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.entityName;
        return 13 * hash + this.entityNumber;
    }

    public int getEntityName() {
        return this.entityName;
    }

    public void setEntityName(int entityName) {
        this.entityName = entityName;
    }

    public int getEntityNumber() {
        return this.entityNumber;
    }

    public void setEntityNumber(int entityNumber) {
        this.entityNumber = entityNumber;
    }
}
