package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 6, 2007
 * Time: 1:31:48 AM
 */
public class RegistrationContextNotExistException extends OpxiException {

    public RegistrationContextNotExistException(String str) {
        super(str);
    }
}
