package com.github.ffremont.astrotheque.core;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class IoC {
    Map<String, Object> beans = new ConcurrentHashMap<>();

    public void started(){
        beans.values().forEach(bean -> {
            if(bean.getClass().isAssignableFrom(StartupListener.class)){
                ((StartupListener)bean).onStartup(this);
            }
        });
    }

    public <T> T get(Class<T> type){
        var key = type.getCanonicalName();
        if(!beans.containsKey(key)){
            try {
                beans.putIfAbsent(key, type.getConstructor(IoC.class).newInstance(this));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                try {
                    beans.putIfAbsent(key, type.getConstructor().newInstance());
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException ex) {
                    log.error("No constructor for {}", type.getSimpleName(),ex);

                    throw new RuntimeException("No constructor for " + type.getSimpleName(),ex);
                }
            }
        }
        return (T) beans.get(key);
    }
}
