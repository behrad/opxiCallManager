package com.basamadco.opxi.callmanager.entity;

/**
 * Represents OpxiCallManager domain objects
 * in persistance schema.
 * 
 * @hibernate.class
 * 
 * @author Jrad
 */
public class Domain extends ValueObject {

//    private Long id;

    private String name;

    /**
     * @hibernate.property
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if( name.indexOf( ":" ) > 0 ) {
            this.name = name.split( ":" )[ 0 ];
        }
    }

    public Domain(String name) {
        setName( name );
    }

    public Domain() {
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Domain domain = (Domain) o;

        if (id != null && domain.id != null ) {
            return id.equals( domain.id );
        }
        
        if (name != null ? !name.equals( domain.name ) : domain.name != null) return false;
        return true;
    }

    public int hashCode() {
        int result;
        result = ( id != null ? id.hashCode() : 0 );
        result = 29 * result + ( name != null ? name.hashCode() : 0 );
        return result;
    }

}
