/* **************************************************************************
 *
 * Copyright (C) 2002 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.basamadco.opxi.callmanager.sip.security;


class ParsedDirective extends Object
{
    public static final int  QUOTED_STRING_VALUE = 1;
    public static final int  TOKEN_VALUE         = 2;

    private int     m_valueType;
    private String  m_name;
    private String  m_value;

    ParsedDirective(
        String  name,
        String  value,
        int     type)
    {
        m_name = name;
        m_value = value;
        m_valueType = type;
    }

    String getValue()
    {
        return m_value;
    }

    String getName()
    {
        return m_name;
    }

    int getValueType()
    {
        return m_valueType;
    }

}