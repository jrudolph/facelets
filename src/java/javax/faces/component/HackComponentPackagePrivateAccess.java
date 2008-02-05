/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.faces.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class HackComponentPackagePrivateAccess {
    
    // PENDING not thread safe
    static Method toInvoke = null;
    
    public static void packageCallBeforeEndContextCallbacks(UIViewRoot instance,
            FacesContext context, String element) {
        try {
            if (null == toInvoke) {
                toInvoke = UIViewRoot.class.getDeclaredMethod("callBeforeEndContextCallbacks", 
                        FacesContext.class, String.class);
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
            toInvoke.invoke(instance, context, element);
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
        }
    }
    

}
