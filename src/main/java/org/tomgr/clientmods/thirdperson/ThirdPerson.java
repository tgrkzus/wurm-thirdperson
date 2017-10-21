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
    private float pitch = 75.0f;
    private float zoomFactor = 1.0f;

    private static final float ZOOM_MAX = 2.5f;
    private static final float ZOOM_MIN = 0.0f;

    private static final float PITCH_MAX = 90.0f;
    private static final float PITCH_MIN = 45.0f;

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


                                if (tpActive) {
                                    RenderVector camera = ReflectionUtil.getPrivateField(
                                            proxy, ReflectionUtil.getField(proxy.getClass(), "cameraOffset"));

                                    float x = camera.getX();
                                    float z = camera.getZ();
                                    double rotation = Math.atan2(z, x);
                                    return zoomFactor * -dist * Math.cos(rotation) * Math.cos(Math.toRadians(pitch)) + (float) method.invoke(proxy, args);
                                }
                                else {
                                    return method.invoke(proxy, args);
                                }
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
                                if (tpActive) {
                                    return zoomFactor * Math.sin(Math.toRadians(pitch)) + (float) method.invoke(proxy, args);
                                }
                                else {
                                    return method.invoke(proxy, args);
                                }
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
                                if (tpActive) {
                                    RenderVector camera = ReflectionUtil.getPrivateField(
                                            proxy, ReflectionUtil.getField(proxy.getClass(), "cameraOffset"));

                                    float x = camera.getX();
                                    float z = camera.getZ();
                                    double rotation = Math.atan2(z, x);
                                    return zoomFactor * -dist * Math.sin(rotation) * Math.cos(Math.toRadians(pitch)) + (float) method.invoke(proxy, args);
                                }
                                else {
                                    return method.invoke(proxy, args);
                                }
                            }
                        };
                    }
                });
        ModConsole.addConsoleListener(this);
    }

    public void addZoom(float factor) {
        setZoom(zoomFactor + factor);
    }

    public void setZoom(float factor) {
        zoomFactor = factor;

        if (zoomFactor < ZOOM_MIN) {
            zoomFactor = ZOOM_MIN;
        }

        if (zoomFactor > ZOOM_MAX) {
            zoomFactor = ZOOM_MAX;
        }
    }

    public void addPitch(float factor) {
        setPitch(pitch + factor);
    }

    public void setPitch(float factor) {
        pitch = factor;

        if (pitch < PITCH_MIN) {
            pitch = PITCH_MIN;
        }

        if (pitch > PITCH_MAX) {
            pitch = PITCH_MAX;
        }
    }

    public void toggleTP() {
        tpActive = !tpActive;
    }

    @Override
    public boolean handleInput(String string, Boolean aBoolean) {
        if (string != null && string.startsWith("toggle tp")) {
            toggleTP();
            return true;
        }

        if (string != null && string.startsWith("tp zoom-in")) {
            addZoom(-0.1f);
            return true;
        }

        if (string != null && string.startsWith("tp zoom-out")) {
            addZoom(0.1f);
            return true;
        }
        return false;
    }
}
