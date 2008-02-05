/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.faces.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.faces.event.ComponentLifecycleListener;
import javax.faces.event.FacesListener;

/**
 *
 * @author edburns
 */
public class HackGetFacesListenersAccess {
    
    // PENDING not thread safe
    static Method toInvoke = null;
    
    public static FacesListener[] packageGetFacesListeners(UIComponentBase instance, 
            Class clazz) {
        FacesListener [] result = null;
        try {
            if (null == toInvoke) {
                toInvoke = UIComponentBase.class.getDeclaredMethod("packageGetFacesListeners", Class.class);
                toInvoke.setAccessible(true);
                /******
                Method [] methods = UIComponentBase.class.getDeclaredMethods();
                for (Method cur : methods) {
                    if (cur.getName().equals("packageGetFacesListeners")) {
                        toInvoke = cur;
                        toInvoke.setAccessible(true);
                        break;
                    }
                }
                
                 *******/
            }
            result = (FacesListener []) toInvoke.invoke(instance, clazz);
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
        catch (InvocationTargetException e) {
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (null == result) {
                result = new ComponentLifecycleListener[0];
            }
        }
        return result;
    }
    

}
