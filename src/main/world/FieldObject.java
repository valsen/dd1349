package main.world;

public abstract class FieldObject {

    private Field field;
    private Location location;

    /**
     * Create a FieldObject and place at the specified location in the correct field.
     * @param field The field of the object.
     * @param location The location of the object.
     */
    public FieldObject(Field field, Location location) {
        this.field = field;
        this.location = location;
        field.place(this, location);
    }

}
