package org.achacha.webcardgame.web;

import com.google.gson.Gson;
import org.achacha.base.global.Global;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class GsonReader<T> implements MessageBodyReader<T> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] antns, MediaType mt) {
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] antns, MediaType mt, MultivaluedMap<String, String> mm, InputStream in) throws IOException, WebApplicationException {
        final Gson gson = Global.getInstance().getGson();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        return gson.fromJson(br, genericType);
    }
}