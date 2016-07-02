package eu.thog.rulesbreaker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Reflections utilities used for some hooks
 * Created by Thog the 02/07/2016
 */
public class ReflectionUtils
{
    public static void setFinalField(Field field, Object instance, Object value) throws RuntimeException
    {
        field.setAccessible(true);
        try
        {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(instance, value);
        } catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(String.format("Cannot set field %s with value %s", field.getName(), value.toString()));
        }
    }

    public static Object getValue(Object instance, Field field)
    {
        try
        {
            return field.get(instance);
        } catch (IllegalAccessException e)
        {
            return null;
        }
    }

    public static void setValue(Object instance, Field field, Object value)
    {
        try
        {
            field.set(instance, value);
        } catch (IllegalAccessException e)
        {

        }
    }
}
