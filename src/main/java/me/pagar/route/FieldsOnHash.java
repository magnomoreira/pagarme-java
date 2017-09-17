package me.pagar.route;

import com.google.gson.Gson;
import lombok.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class FieldsOnHash implements CanLoadFieldsFromSources, CanBecomeKeyValueVariable {

    //String => (number | string | Map)
    private HashMap<String, Object> fields = new HashMap<>();

    public FieldsOnHash(@NonNull String jsonString){
        loadParametersFrom(jsonString);
    }

    public FieldsOnHash(@NonNull Map<String, Object> parameters){
        loadParametersFrom(parameters);
    }

    public Map<String, Object> fields(){
        return this.fields;
    }

    public Integer getParameterAsInteger(@NonNull String parameterName) throws NumberFormatException{
        Object parameter = getParameterReference(parameterName);
        if(parameter != null) {
            Integer parameterAsInteger = Integer.valueOf(parameter.toString());
            return parameterAsInteger;
        } else {
            throw new NumberFormatException();
        }

    }

    public String getParameterAsString(@NonNull String parameterName) throws ClassCastException{
        Object parameter = getParameterReference(parameterName);
        if(parameter == null){
            return "null";
        } else {
            String parameterAsString = parameter.toString();
            return parameterAsString;
        }
    }

    public Boolean getParameterAsBoolean(@NonNull String parameterName) throws ClassCastException{
        Object parameter = getParameterReference(parameterName);
        if(parameter != null) {
            Boolean parameterAsBoolean = Boolean.valueOf(parameter.toString());
            return parameterAsBoolean;
        } else {
            throw new ClassCastException();
        }
    }

    public <T extends FieldsOnHash> List<T> getParameterAsList(@NonNull String parameterName) throws ClassCastException {
        Object parameter = getParameterReference(parameterName);
        return (List<T>) parameter;
    }

    public List<String> getParameterAsStringList(@NonNull String parameterName) throws ClassCastException {
        Object parameter = getParameterReference(parameterName);
        if(parameter != null) {
            return (List<String>) parameter;
        } else {
            throw new ClassCastException();
        }
    }

    public List<FieldsOnHash> getParameterAsObjectList(@NonNull String parameterName) throws ClassCastException {
        Object parameter = getParameterReference(parameterName);
        return (List<FieldsOnHash>) parameter;
    }

    @NonNull
    public <T extends CanLoadFieldsFromSources> T getParameterCasted(String parameterName, T classInstance) {
        Map<String, Object> clasInstanceParameters = getParameterAsMap(parameterName);
        classInstance.loadParametersFrom(clasInstanceParameters);
        return classInstance;
    }

    @NonNull
    public Map<String, Object> getParameterAsMap(String parameterName) throws ClassCastException {
        Object parameterValue = this.getParameterReference(parameterName);
        if(parameterValue != null) {
            return (Map<String, Object>)parameterValue;
        } else {
            throw new ClassCastException();
        }
    }

    public void setParameter(@NonNull String parameterName, String parameterValue){
        this.fields.put(parameterName, parameterValue);
    }

    public void setParameter(@NonNull String parameterName, Integer parameterValue){
        this.fields.put(parameterName, parameterValue);
    }

    public void setParameter(@NonNull String parameterName, Map<String, Object> parameterValue){
        this.fields.put(parameterName, parameterValue);
    }

    public void setParameter(@NonNull String parameterName, Boolean parameterValue){
        this.fields.put(parameterName, parameterValue);
    }

    public void setParameter(@NonNull String parameterName, FieldsOnHash child){
        this.fields.put(parameterName, child.fields());
    }

    public <T extends FieldsOnHash> void setParameter(@NonNull String parameterName, Collection<T> list) {
        List<Map<String, Object>> mappedObjects = list.stream()
                .map((item) -> item.fields())
                .collect(Collectors.toList());
        this.fields.put(parameterName, mappedObjects);
    }

    public void setParameterCollection(@NonNull String parameterName, Collection<String> collection) {
        this.fields.put(parameterName, collection);
    }

    @Override
    @NonNull
    public void loadParametersFrom(Map<String, Object> parameters) {
        fields.putAll(parameters);
    }

    //TODO - parametrize Gson
    @Override
    @NonNull
    public void loadParametersFrom(String jsonString) {
        Map<String, Object> hashedJson = new Gson().fromJson(jsonString, HashMap.class);
        loadParametersFrom(hashedJson);
    }

    //TODO - parametrize Gson
    @Override
    public String toJson(){
        String jsonString = new Gson().toJson(this.fields);
        return jsonString;
    }

    @NonNull
    protected Object getParameterReference(String parameterName) {
        Object parameterValue = this.fields.get(parameterName);
        return parameterValue;
    }

}