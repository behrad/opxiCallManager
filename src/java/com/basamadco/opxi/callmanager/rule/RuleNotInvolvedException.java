package com.basamadco.opxi.callmanager.rule;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Sep 15, 2008
 *         Time: 4:09:06 PM
 */
public class RuleNotInvolvedException extends OpxiException {

    public RuleNotInvolvedException( String msg ) {
        super( msg );
    }
}
