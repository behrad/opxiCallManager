package com.basamadco.opxi.callmanager.pool.rules;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

/**
 * @author Jrad
 *         Date: Mar 5, 2006
 *         Time: 12:18:29 PM
 */
public class GroupOperand implements Operand {

    protected static final int G_NAME = 0;

    protected static final int G_PHONE_NUMBER = 1;

    private static final String GROUP_VAR_NAMES[] = {
            "name", "telephoneNumber", "greetingMsgURI", "dn", "cn"
    };

    private int var;

    public GroupOperand( String name ) {
        var = varNameToInt( name );
        if( var < 0 ) {
            throw new IllegalArgumentException( "No group attribute with name " + getAttributeName() + " defined." );
        }
    }

    public String getName() {
        return "Group";
    }

    /**
     * Evaluates current this attribute value for the specified group CN in active directory.
     *
     * @param o The String name of the group for which the attribute value will be returned
     * @return String attribute value of the specified group name in active directory
     */
    public String bind( Object o /* this is a PoolTarget */ ) {
//        String groupName = ( o instanceof String ) ? o.toString() : ((PoolTarget)o).getName();
        try {
//            DirectoryDAOFactory ddaof = (DirectoryDAOFactory) BaseDAOFactory.getDAOFactory( BaseDAOFactory.DIRECTORY );
//            String val = ddaof.getPoolTargetDAO().getAttributeValue( groupName, getAttributeName() );
            return OpxiToolBox.invokeGetterMethod( o, getAttributeName() ).toString();
        } catch ( OpxiException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAttributeName() {
        return GROUP_VAR_NAMES[ var ];
    }

    private int varNameToInt( String groupVarName ) {
        for (int i = 0; i < GROUP_VAR_NAMES.length; i++) {
            if( GROUP_VAR_NAMES[i].equals( groupVarName ) ) {
                return i;
            }
        }
        return -1;
    }

}
