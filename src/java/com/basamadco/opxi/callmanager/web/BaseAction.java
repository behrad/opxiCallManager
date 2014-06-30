package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.ValueObject;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAO;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import org.apache.struts.action.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 21, 2006
 *         Time: 10:35:41 AM
 */
public abstract class BaseAction extends Action {

    private static final Logger logger = Logger.getLogger( BaseAction.class.getName() );


    public static final String ACTION_TYPE = "action";

    public static final String VALUE_OBJECT = "valueObject";

    public static final String VALUE_OBJECT_LIST = "valueObjectList";

    public static final String ID    = "id";

    public static final String FORM  = "form";

    public static final String ERROR = "error";

    /*
     * Action Types
     */
    protected static final int NULL_ACTION = 0;

    protected static final int CREATE = 1;

//    public static final int READ = 2;

    protected static final int UPDATE = 3;

//    public static final int DELETE = 4;

    protected static final int READ_ALL = 5;

    protected static final int READ_BY_ID = 6;

    protected static final int EDIT_BY_ID = 7;

    protected static final int DELETE_BY_ID = 8;

    protected static final int SPECIFIC = 11;


    protected static HashMap actionMap = new HashMap();
    static {
        actionMap.put( "", new Integer(NULL_ACTION ) );
        actionMap.put( "create", new Integer( CREATE ) );
//        actionMap.put("read", new Integer(READ));
        actionMap.put( "update", new Integer( UPDATE ) );
//        actionMap.put("delete", new Integer(DELETE));
        actionMap.put( "read_all", new Integer( READ_ALL ) );
        actionMap.put( "read_by_id", new Integer( READ_BY_ID ) );
        actionMap.put( "edit_by_id", new Integer( EDIT_BY_ID ) );
        actionMap.put( "delete_by_id", new Integer( DELETE_BY_ID ) );
        actionMap.put( "specific", new Integer( SPECIFIC ) );
    }

//    protected abstract Class getValueObjectClass();

    protected abstract DatabaseDAO getDAO() throws OpxiException;

    public final ActionForward execute( ActionMapping actionMapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response ) throws Exception {

        BaseActionContext ctx = new BaseActionContext(
                (DynaActionForm)form, request, actionMapping, getDAO().getValueObjectClass()
        );
        return doExecute( ctx, actionMapping, response );
    }

    public final ActionForward doExecute( BaseActionContext ctx, ActionMapping actionMapping,
                                          HttpServletResponse response ) {
        int actionCode = getActionCode( ctx );
        ActionForward forward;
        try {
            switch( actionCode ) {
                case NULL_ACTION:
                    forward = doNullAction( ctx );
                        break;
                case CREATE:
                    forward = doCreate( ctx );
                        break;
                case READ_ALL:
                    forward = doReadAll( ctx );
                        break;
                case DELETE_BY_ID:
                    forward = doDeleteById( ctx );
                        break;
                case READ_BY_ID:
                    forward = doReadById( ctx );
                        break;
                case EDIT_BY_ID:
                    forward = doEditById( ctx );
                        break;
                case SPECIFIC:
                    forward = doSpecific( ctx );
                        break;
                case UPDATE:
                    forward = doUpdate( ctx );
                        break;
                default:
                    throw new ServletException( "Unknown Action" + ": " + actionCode );
            }
        } catch( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            ctx.getRequest().setAttribute( "exception", e );
            forward = actionMapping.findForward( ERROR );
        }
        return forward;
    }

    protected ActionForward doNullAction( BaseActionContext ctx ) throws Exception {
        logger.finer( "************************** " );
//        Exchanger.exhangeValueObjectToForm(vo, ctx.getForm());
        ctx.getForm().set( ID, new Long(0) );
        ctx.getForm().set( ACTION_TYPE, "create" );
        return ctx.getActionMapping().findForward( "edit" );
    }

    protected ActionForward doReadAll( BaseActionContext ctx ) throws Exception {
        logger.finer( "************************** " );
        return listAll(ctx);
    }

    protected ActionForward doDeleteById( BaseActionContext ctx ) throws Exception {
        logger.finer( "************************** " );
        String id = (String)ctx.getRequest().getParameter( ID );
        if (id == null)
            throw new ServletException( "Id is null" );
        Long lId = Long.valueOf( id );
        getDAO().delete( getDAO().load( getDAO().getValueObjectClass(), lId ) );
        return listAll(ctx);
    }

    protected ActionForward doReadById( BaseActionContext ctx ) throws Exception {
        logger.finer( "************************** " );
//        String id = (String)ctx.getForm().get( ValueObject.ID );
//        if (id == null)
//            throw new ServletException(NULL_ID_MESSAGE);

//        ValueObject valueObject = getProxy().read(id);

        try {
//            ValueObject vo2 = (ValueObject) getValueObjectClass().newInstance();
//            exhangeResultSetToValueObject(ctx.getConnection(), "Select * from "+ctx.getEntity()+" where id='"+id+"'", new ValueObject[] {vo2});
//
        } catch (Exception e) {

        }

//        ctx.getRequest().setAttribute( VALUE_OBJECT, valueObject );
        return ctx.getActionMapping().findForward( "read" );
    }

    protected ActionForward doEditById( BaseActionContext ctx ) throws Exception {
        logger.finer( "************************** " );
        logger.finer( "form: " + ctx.getForm() );
        String voId = ctx.getRequest().getParameter( ID );
        if ( voId == null )
            throw new ServletException( "Value Object Id is null" );

        Long lId = Long.valueOf( voId );
        logger.finer( "lId: " + lId.toString() );
        ValueObject vo = getDAO().load( getDAO().getValueObjectClass(), lId );
        logger.finer( "VO: " + vo.toString() );
        exhangeValueObjectToForm( vo, ctx.getForm() );
//        ctx.getRequest().setAttribute( FORM, ctx.getForm() );
        ctx.getForm().set( ACTION_TYPE, "update" );
        return ctx.getActionMapping().findForward( "edit" );
    }

    protected ActionForward doCreate( BaseActionContext ctx ) throws Exception {
        logger.finer( "************************** " );
        if ( !isCancelled( ctx.getRequest() ) ) {
            ValueObject vo = (ValueObject)getDAO().getValueObjectClass().newInstance();
            exhangeFormToValueObject( ctx.getForm(), vo );
            getDAO().save( vo );
        }
        return listAll(ctx);
    }

    protected ActionForward doUpdate( BaseActionContext ctx ) throws Exception {
        logger.finer( "************************** " );
        Long id = (Long)ctx.getForm().get( ID );
        ValueObject valueObject = getDAO().load( getDAO().getValueObjectClass(), id );
        if ( !isCancelled( ctx.getRequest() ) ) {
            exhangeFormToValueObject( ctx.getForm(), valueObject );
            getDAO().update( valueObject );
        }
        ctx.getRequest().setAttribute( VALUE_OBJECT, valueObject );
        return ctx.getActionMapping().findForward( "read" );
    }

    protected ActionForward doSpecific( BaseActionContext ctx ) throws Exception  {
        logger.finer( "************************** " );
        return null;
    }

    protected ActionForward listAll( BaseActionContext ctx ) throws Exception {
        ctx.getRequest().setAttribute( VALUE_OBJECT_LIST, getDAO().listAll() );
        return ctx.getActionMapping().findForward( "list" );
    }

    private int getActionCode( BaseActionContext ctx ) {
        int actionCode = NULL_ACTION;
        logger.finer( "Request URI: " + ctx.getRequest().getRequestURI() );
        String action = (String)ctx.getForm().get( ACTION_TYPE );
        if ( OpxiToolBox.isEmpty( action ) ) {
            actionCode = NULL_ACTION;
            logger.finer( "===================== Action: " + action );
            action = "";
        } else {
            if ( action.indexOf( "," ) > 0 ) {
                ctx.setSubAction( action.substring( action.indexOf( "," ) + 1 ) );
                action = action.substring( 0, action.indexOf( "," ) );
                ctx.setAction( action );
                actionCode = ( (Integer)actionMap.get( action ) ).intValue();
            }
        }

        switch ( ctx.getRequest().getServletPath().charAt( 1 ) ) {
            case 'c' :
                actionCode = CREATE;
                break;
            case 'r' :
                if ( !action.startsWith("read") )
                    actionCode = READ_BY_ID;
                break;
            case 'u' :
                actionCode = UPDATE;
                if ( !action.startsWith( "update" ) )
                    actionCode = EDIT_BY_ID;
                break;
            case 's' :
                actionCode = SPECIFIC;
                if ( !action.startsWith("specific") )
                    actionCode = READ_ALL;
                break;
            case 'd' :
                    actionCode = DELETE_BY_ID;
                break;
            case 'l' :
                if ( !(action.startsWith("read") ) )
                    actionCode = READ_ALL;
                break;
            default:
                actionCode = READ_ALL;
        }
        logger.finer( "Result Action Code: " + actionCode );
        return actionCode;
    }

//    private Object newValueObject() throws Exception {
//        return getValueObjectClass().newInstance();
//    }

    protected void exhangeFormToValueObject( DynaActionForm form, Object valueObject ) {
        if ( valueObject == null || form == null )
            return;
        Class voClass = valueObject.getClass();
        Map map = form.getMap();
        for(Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
            String key = (String)iter.next();
            Object value = map.get( key );
            String methodName = "set" + key.toUpperCase().substring( 0, 1 ) + key.substring( 1 );
            if( value != null ) {
                try {
                    Method method = voClass.getMethod( methodName, new Class[] { value.getClass() } );
                    method.invoke( valueObject, new Object[] { value } );
                } catch( Exception exception ) {
//                    logger.warn( "Ignoring the following exception" + exception );
                }
            }
        }
    }

    protected void exhangeValueObjectToForm( Object valueObject, DynaActionForm form )  {
        if ( valueObject == null || form == null )
            return ;
        Class voClass = valueObject.getClass();
        Method[] methods = voClass.getMethods();
        for( int index = 0; index != methods.length; index++ ) {
            Method method = methods[ index ];
            if( method.getName().startsWith( "get" ) ) {
                String key = method.getName().substring( 3 );
                key = key.toLowerCase().substring( 0, 1 ) + key.substring( 1 );
                try {
                    Object value = method.invoke( valueObject, new Object[] {} );
                    logger.finer( "------------> " + key + "=" + value );
                        form.set( key, value );
                } catch( Exception exception ) {
//                    logger.warn( "Ignoring the following exception " + exception );
                }
            }

        }
    }

}