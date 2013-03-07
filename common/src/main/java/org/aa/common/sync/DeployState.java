/**	   Copyright [2009] [www.apartech.co.uk]


**/
package org.aa.common.sync;

import java.util.Vector;

import org.aa.common.deploy.DeployInfo;



public class DeployState {

    public static long ntfyCount;
    public static String uninstallStatus ;
    public static String installStatus ;
    public static boolean websphere_process_starting ;
    public static boolean websphere_process_running ;
    public static boolean websphere_process_stopping;
    public static boolean websphere_process_stopped ;
    public static Vector nodes ;
    public static boolean distStatus ;
    public static boolean distStatusForUninstall;
    protected static DeployInfo deployInfo;
    public static int syncCount ;
    public static boolean websphere_nodesync_initiated;
    public static boolean websphere_nodesync_complete;
    
}
