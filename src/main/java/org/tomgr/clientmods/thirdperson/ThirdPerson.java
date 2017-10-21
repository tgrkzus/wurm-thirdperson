package org.tomgr.clientmods.thirdperson;

import com.wurmonline.client.renderer.RenderVector;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;
import org.gotti.wurmunlimited.modsupport.console.ConsoleListener;
import org.gotti.wurmunlimited.modsupport.console.ModConsole;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ThirdPerson implements WurmClientMod, Initable, PreInitable, ConsoleListener {
    private boolean tpActive = false;
    private static Logger logger = Logger.getLogger(ThirdPerson.class.getName());
    private float dist = 2.5f;
    private float height = 2.5f;

    @Override
    public void preInit() {

    }

    @Override
    public void init() {


        HookManager.getInstance().registerHook("com.wurmonline.client.renderer.WorldRender",
                "getCameraX", null,
                new InvocationHandlerFactory() {
                    @Override
                    public InvocationHandler createInvocationHandler() {
                        return new InvocationHandler() {

                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                RenderVector camera = ReflectionUtil.getPrivateField(
                                        proxy, ReflectionUtil.getField(proxy.getClass(), "cameraOffset"));

                                float x = camera.getX();
                                float z = camera.getZ();
                                double rotation = Math.atan2(z, x);
                                return -dist * Math.cos(rotation) + (float) method.invoke(proxy, args);
                            }
                        };
                    }
                });

        HookManager.getInstance().registerHook("com.wurmonline.client.renderer.WorldRender",
                "getCameraY", null,
                new InvocationHandlerFactory() {
                    @Override
                    public InvocationHandler createInvocationHandler() {
                        return new InvocationHandler() {

                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                return height + (float) method.invoke(proxy, args);
                            }
                        };
                    }
                });

        HookManager.getInstance().registerHook("com.wurmonline.client.renderer.WorldRender",
                "getCameraZ", null,
                new InvocationHandlerFactory() {
                    @Override
                    public InvocationHandler createInvocationHandler() {
                        return new InvocationHandler() {

                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                RenderVector camera = ReflectionUtil.getPrivateField(
                                        proxy, ReflectionUtil.getField(proxy.getClass(), "cameraOffset"));

                                float x = camera.getX();
                                float z = camera.getZ();
                                double rotation = Math.atan2(z, x);
                                return -dist * Math.sin(rotation) + (float) method.invoke(proxy, args);
                            }
                        };
                    }
                });
        ModConsole.addConsoleListener(this);
    }

    public void toggleTP() {
        tpActive = !tpActive;

        if (tpActive) {

        }
        else {

        }
    }

    @Override
    public boolean handleInput(String string, Boolean aBoolean) {
        if (string != null && string.startsWith("toggle tp")) {
            System.out.println("Toggle TP");
            toggleTP();
        }
        return false;
    }
}
