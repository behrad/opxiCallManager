package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.activitylog.schema.Calls;
import com.basamadco.opxi.activitylog.schema.TrunkCalls;
import com.basamadco.opxi.activitylog.schema.TrunkSvc;
import com.basamadco.opxi.activitylog.schema.TrunkUsage;
import com.basamadco.opxi.callmanager.call.Trunk;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Oct 18, 2007
 * Time: 1:31:33 AM
 */
public abstract class  AbstractTrunkActivityLogger extends ChildActivityLogger {

    private static final Logger logger = Logger.getLogger(ServiceActivityLogger.class.getName());

    protected final static String PREFIX_TRUNK = "TrunkId-";

    public AbstractTrunkActivityLogger(OpxiActivityLogger parent) {
        super(parent);
    }

    protected abstract TrunkSvc getTrunkSvc();
    protected abstract void setTrunkSvc(TrunkSvc ts);
    public TrunkCalls getTrunkCalls() {
//         return _trunkCalls;;
        return getTrunkSvc().getTrunkCalls();
        //TODO :  this method must throw an exception when return value is null.
    }

    public void setTrunkCalls( TrunkCalls tc) {
//        _trunkCalls = tc;
        getTrunkSvc().setTrunkCalls(tc);
    }

    protected TrunkUsage addTrunkUsage( Trunk t) {
        TrunkUsage tu = new TrunkUsage();
        tu.setName(t.getName());
        tu.setDialPattern(t.getDialPattern());
        initTrunkUsage(tu);
        getTrunkSvc().addTrunkUsage(tu);
        return tu;
    }

    protected TrunkUsage getOrAddTrunkUsage( Trunk t) {
        TrunkUsage[] tu = getTrunkSvc().getTrunkUsage();
        for (int i = 0; i < tu.length; i++) {
            if (tu[i].getName().equals(t.getName())) {
                return tu[i];
            }
        }
        return addTrunkUsage(t);
    }

    protected TrunkUsage getOrAddTrunkUsage(com.basamadco.opxi.callmanager.call.CallService cs) {
        Trunk t = (Trunk) cs.getTarget();
        return getOrAddTrunkUsage(t);
    }

    protected void initTrunkUsage(TrunkUsage tu) {
        tu.setCalls((Calls) parent.initStatistics(new Calls()));
        addStatistics(getTrunkId(tu.getName()), SummaryStatistics.newInstance());
    }

    protected String getTrunkId(String name) {
        return PREFIX_TRUNK + name;
    }
}
