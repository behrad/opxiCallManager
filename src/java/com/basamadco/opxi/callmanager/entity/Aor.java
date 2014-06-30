package com.basamadco.opxi.callmanager.entity;

/**
 * @author AM
 *         Date: Apr 29, 2008
 *         Time: 9:47:26 PM
 */
public class Aor {

    private String _name;

    private String _domain;

    public Aor( String name, String domain ) {
        set_name( name );
        set_domain( domain );

    }

    public Aor( String aor ) {
        set_aor( aor );
    }

    public String get_name() {
        return _name;
    }

    public void set_name( String _name ) {
        this._name = _name;
    }

    public String get_domain() {
        return _domain;
    }

    public void set_domain( String _domain ) {
        this._domain = _domain;
    }

    public String get_aor() {
        return "sip:" + get_name() + "@" + get_domain();
    }

    public void set_aor( String aor ) {
        String uri = aor;
        if (uri.startsWith( "sip:" )) {
            uri = uri.split( "sip:" )[1];
            String[] strs = aor.split( "@" );
            set_name( strs[0] );
            set_domain( strs[1] );
        }
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Aor aor = (Aor) o;

        if (!get_domain().equals( aor.get_domain() )) return false;
        if (!get_name().equals( aor.get_name() )) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = get_name().hashCode();
        result = 31 * result + get_domain().hashCode();
        return result;
    }

    public String toString() {
        return "AOR='" + get_aor() + "'";
    }
}
